package com.kangtech.tauonremote.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kangtech.tauonremote.R;

import java.util.ArrayList;

public class AddServerAdapter extends RecyclerView.Adapter<AddServerAdapter.AddViewHolder> {

    private ArrayList<String> rvData;
    Context context;

    public AddServerAdapter(ArrayList<String> rvData) {
        this.rvData = rvData;
    }

    @NonNull
    @Override
    public AddServerAdapter.AddViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_server, parent, false);
        return new AddServerAdapter.AddViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddServerAdapter.AddViewHolder holder, int position) {
        holder.tvServer.setText(rvData.get(position));
    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServer;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServer = itemView.findViewById(R.id.tv_server);

        }
    }
}
