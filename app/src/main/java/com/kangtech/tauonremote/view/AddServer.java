package com.kangtech.tauonremote.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.adapter.AddServerAdapter;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.ServerModel;
import com.kangtech.tauonremote.model.status.StatusModel;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Delayed;

public class AddServer extends AppCompatActivity {

    public static RecyclerView rvServer;
    private RecyclerView.LayoutManager layoutManager;
    public static ArrayList<ServerModel> dataSet;
    public static RecyclerView.Adapter adapter;

    public static RecyclerRefreshLayout refreshLayout;
    public static NetworkSniffTask nettask;

    private SharedPreferences.Editor editor;

    private Toolbar toolbar;

    private TextInputEditText tieIP;
    private Button btnRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);

        if (SharedPreferencesUtils.getBoolean("set_server", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            nettask = new NetworkSniffTask(getApplicationContext());
            nettask.execute();
        }


        refreshLayout = findViewById(R.id.srl_server);
        toolbar = findViewById(R.id.toolbar_addserver);
        tieIP = findViewById(R.id.tie_ip);
        btnRemote = findViewById(R.id.btn_remote);

        btnRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(tieIP.getText()).length() == 0) {
                    Toast.makeText(AddServer.this, "Enter IP address", Toast.LENGTH_SHORT).show();
                } else {
                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putBoolean("set_server", true);
                    editor.putString("ip", tieIP.getText().toString());
                    editor.apply();

                    Server.Reload();
                    nettask.cancel(true);

                    Intent intent = new Intent(AddServer.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_server_refresh) {
                    dataSet.clear();
                    nettask.cancel(true);
                    refreshLayout.setRefreshing(false);

                    if (nettask.isCancelled()) {
                        nettask = new NetworkSniffTask(getApplicationContext());
                        nettask.execute();

                        int delay = 500;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.setRefreshing(true);
                            }
                        },delay);
                    }
                }

                return true;
            }
        });

        refreshLayout.setRefreshing(true);
        refreshLayout.setEnabled(false);


        dataSet = new ArrayList<>();

        rvServer = findViewById(R.id.rv_addserver);

        layoutManager = new LinearLayoutManager(this);
        rvServer.setLayoutManager(layoutManager);

        adapter = new AddServerAdapter(this, dataSet);
        rvServer.setAdapter(adapter);


    }

    static class NetworkSniffTask extends AsyncTask<Void, Void, Void> {

        private static final String TAG = SyncStateContract.Constants._ID + "nstask";

        private final WeakReference<Context> mContextRef;

        public NetworkSniffTask(Context context) {
            mContextRef = new WeakReference<Context>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Context context = mContextRef.get();

                if (context != null) {

                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    WifiInfo connectionInfo = wm.getConnectionInfo();
                    int ipAddress = connectionInfo.getIpAddress();
                    String ipString = Formatter.formatIpAddress(ipAddress);


                    Log.e(TAG, "activeNetwork: " + activeNetwork);
                    Log.e(TAG, "ipString: " + ipString);

                    String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                    Log.e(TAG, "prefix: " + prefix);

                    for (int i = 0; i < 255; i++) {
                        if (isCancelled())
                            break;

                        String testIp = prefix + String.valueOf(i);
                        Log.i("tai", testIp);

                        String data = getJSON("http://" + testIp + ":7814/api1/status");
                        if (data.contains("{")) {
                            StatusModel statusModel = new Gson().fromJson(data, StatusModel.class);
                            if (!statusModel.status.isEmpty()) {

                                dataSet.add(new ServerModel(i, testIp, statusModel.status));

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                       adapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        }

                        if (i >= 254) {
                            refreshLayout.setRefreshing(false);
                        }

                    }
                }
            } catch (Throwable t) {
                Log.e(TAG, "Well that's not good.", t);
            }

            return null;
        }

        private String getJSON(String url) {
            HttpURLConnection connection = null;
            try {
                URL urlnya = new URL(url);
                connection = (HttpURLConnection) urlnya.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-length", "0");
                connection.setUseCaches(false);
                connection.setAllowUserInteraction(false);
                connection.setReadTimeout(150);
                connection.setConnectTimeout(1000);
                connection.connect();

                int status = connection.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return url;
        }
    }
}