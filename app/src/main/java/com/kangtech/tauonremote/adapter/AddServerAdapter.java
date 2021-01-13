package com.kangtech.tauonremote.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.model.ServerModel;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.view.AddServer;
import com.kangtech.tauonremote.view.MainActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AddServerAdapter extends RecyclerView.Adapter<AddServerAdapter.AddViewHolder> {

    private ArrayList<ServerModel> rvData;
    Context context;
    private SharedPreferences.Editor editor;

    public AddServerAdapter(Context context, ArrayList<ServerModel> rvData) {
        this.rvData = rvData;
        this.context = context;
    }

    @NonNull
    @Override
    public AddServerAdapter.AddViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_server, parent, false);
        return new AddViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddServerAdapter.AddViewHolder holder, int position) {
        holder.tvServer.setText(rvData.get(position).ip);
        holder.tvServerStatus.setText(rvData.get(position).status);

        holder.llListServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = context.getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                editor.putBoolean("set_server", true);
                editor.putString("ip", rvData.get(position).ip);
                editor.apply();

                Server.Reload();

                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }

    public static class AddViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvServerStatus;
        private final TextView tvServer;
        private final LinearLayout llListServer;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServer = itemView.findViewById(R.id.tv_server);
            tvServerStatus = itemView.findViewById(R.id.tv_server_status);
            llListServer = itemView.findViewById(R.id.ll_list_server);

        }
    }
}
