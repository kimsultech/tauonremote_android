package com.kangtech.tauonremote.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.api.RetrofitClient;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_setting);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvIP = findViewById(R.id.tv_setting_ip);
        tvIP.setText(SharedPreferencesUtils.getString("ip", "127.0.0.1"));

        LinearLayout llChangeServer = findViewById(R.id.ll_setting_changeserver);
        llChangeServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                editor.putBoolean("set_server", false);
                editor.apply();


                Intent intent = new Intent(SettingsActivity.this, AddServer.class);
                intent.putExtra("FROM_SETTINGS", true);
                startActivity(intent);

                MainActivity.stopStatus();

                Intent intentService = new Intent(SettingsActivity.this, PlayingService.class);
                stopService(intentService);

                finishAffinity();
            }
        });

        SwitchMaterial switchMaterial = findViewById(R.id.sm_setting_light);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            switchMaterial.setChecked(false);
        } else {
            switchMaterial.setChecked(true);
        }
        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putBoolean("light_enable", true);
                    editor.apply();

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putBoolean("light_enable", false);
                    editor.apply();

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });

        SwitchMaterial smNotif = findViewById(R.id.sm_setting_notif);
        TextView tvInfoNotif = findViewById(R.id.tv_info_notif_enable);

        if (SharedPreferencesUtils.getBoolean("notif_enable", false)) {
            smNotif.setChecked(true);
        } else {
            smNotif.setChecked(false);
            tvInfoNotif.setVisibility(View.GONE);
        }
        smNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putBoolean("notif_enable", true);
                    editor.apply();

                    tvInfoNotif.setVisibility(View.VISIBLE);
                } else {
                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putBoolean("notif_enable", false);
                    editor.apply();

                    Intent intentService = new Intent(SettingsActivity.this, PlayingService.class);
                    stopService(intentService);

                    tvInfoNotif.setVisibility(View.GONE);
                }
            }
        });
    }
}