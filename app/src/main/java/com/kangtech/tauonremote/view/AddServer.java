package com.kangtech.tauonremote.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.adapter.AddServerAdapter;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.status.StatusModel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AddServer extends AppCompatActivity {

    public static RecyclerView rvServer;
    private RecyclerView.LayoutManager layoutManager;
    public static ArrayList<String> dataSet;
    public static RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);

        NetworkSniffTask nettask = new NetworkSniffTask(getApplicationContext());
        nettask.execute();



        dataSet = new ArrayList<>();

        rvServer = findViewById(R.id.rv_addserver);

        layoutManager = new LinearLayoutManager(this);
        rvServer.setLayoutManager(layoutManager);

        adapter = new AddServerAdapter(dataSet);
        rvServer.setAdapter(adapter);


    }

    static class NetworkSniffTask extends AsyncTask<Void, Void, Void> {

        private static final String TAG = SyncStateContract.Constants._ID + "nstask";

        private WeakReference<Context> mContextRef;
        private String getStatus;

        public NetworkSniffTask(Context context) {
            mContextRef = new WeakReference<Context>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Let's sniff the network");

            try {
                Context context = mContextRef.get();

                if (context != null) {

                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    WifiInfo connectionInfo = wm.getConnectionInfo();
                    int ipAddress = connectionInfo.getIpAddress();
                    String ipString = Formatter.formatIpAddress(ipAddress);


                    Log.e(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
                    Log.e(TAG, "ipString: " + String.valueOf(ipString));

                    String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                    Log.e(TAG, "prefix: " + prefix);

                    for (int i = 110; i < 255; i++) {
                        String testIp = prefix + String.valueOf(i);
                        Log.i("tai", testIp);

                        String data = getJSON("http://" + testIp + ":7814/api1/status");
                        if (data.contains("{")) {
                            StatusModel statusModel = new Gson().fromJson(data, StatusModel.class);
                            if (!statusModel.status.isEmpty()) {
                                Log.e("ok", statusModel.progress.toString());
                                dataSet.add(testIp);
                                int delay = 1000; // 0,5 detik
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void run() {
                                       adapter.notifyDataSetChanged();
                                    }
                                });

                            }
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