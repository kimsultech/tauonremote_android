package com.kangtech.tauonremote.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;

public class PlayingService extends Service {

    private String getTitle;
    private String getArtist;
    private int getTrackID;
    private Bitmap getBitmap;
    private String getStatus;
    private int icon;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getTitle = intent.getStringExtra("serviceTitle");
        getArtist = intent.getStringExtra("serviceArtist");
        getTrackID = intent.getIntExtra("serviceTrackID", -1);


        runStatus();

        if(intent.getAction() != null && intent.getAction().equals("STOP")) {
            // Stop Service and Notification
            stopSelf();
        } else if (intent.getAction() != null && intent.getAction().equals("PREV")) {
            if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                MainActivity.prevRequest();
            } else {
                MainActivity.sReqPrev();
            }
        } else if (intent.getAction() != null && intent.getAction().equals("NEXT")) {
            if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                MainActivity.nextRequest();
            } else {
                MainActivity.sReqNext();
            }
        } else if (intent.getAction() != null && intent.getAction().equals("PLAY")) {
            switch (getStatus) {
                case "playing":
                    if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                        MainActivity.requestPause();
                    } else {
                        MainActivity.sReqPlayPause();
                    }
                    //icon = R.drawable.ic_round_play_circle_24;
                    break;
                case "paused":
                case "stopped":
                    if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                        MainActivity.requestPlay();
                    } else {
                        MainActivity.sReqPlayPause();
                    }
                    //icon = R.drawable.ic_round_pause_circle_24;
                    break;
            }
        }  else if (intent.getAction() != null && intent.getAction().equals("PAUSE")) {
            MainActivity.requestPause();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load("http://" + SharedPreferencesUtils.getString("ip", "127.0.0.1") + ":7814/api1/pic/medium/" + getTrackID)
                        .centerCrop()
                        .placeholder(R.drawable.ic_round_music_note_24)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                getBitmap = resource;
                                notificationInit();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private void runStatus() {
        int delay = 500;
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    getStatus = SharedPreferencesUtils.getString("status", "");
                } else {
                    getStatus = SharedPreferencesUtils.getString("sStatus", "");
                }

                runStatus();
            }
        },delay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void notificationInit() {

        /*PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, .putExtra("a", ""), PendingIntent.FLAG_NO_CREATE);*/

        Intent notificationIntent = new Intent(this, MainActivity.class);
        //notificationIntent.putExtra("FROM_SERVICE", true);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // prev
        Intent prevIntent = new Intent(this, PlayingService.class);
        prevIntent.setAction("PREV");
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, prevIntent, 0);

        // play
        Intent playIntent = new Intent(this, PlayingService.class);
        playIntent.setAction("PLAY");
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        // pause
        Intent pauseIntent = new Intent(this, PlayingService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        // next
        Intent nextIntent = new Intent(this, PlayingService.class);
        nextIntent.setAction("NEXT");
        PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        // Stop Service
        Intent mIntent = new Intent(this, PlayingService.class);
        mIntent.setAction("STOP");
        PendingIntent stopIntent = PendingIntent.getService(this, 0, mIntent, 0);

        Notification customNotification = new NotificationCompat.Builder(getApplicationContext(), "tauon_31")
                .setSmallIcon(R.drawable.ic_v4_f)
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
                        .setShowActionsInCompactView(0,1,2))
                // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.ic_round_prev2_24, "Previous", pendingPrevIntent) // #0
                // TODO icon play pause dynamic change
                .addAction(R.drawable.ic_play_pause, "Play", pendingPlayIntent)  // #1
                .addAction(R.drawable.ic_round_next2_24, "Next", pendingNextIntent)     // #2
                .addAction(R.drawable.ic_round_close_24, "STOP", stopIntent) // #3
                .setContentTitle(getTitle)
                .setContentText(getArtist)
                .setLargeIcon(getBitmap)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "tauon_31";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Tauon Remote & Stream",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            //mBuilder.setChannelId(channelId);
            //mNotificationManager.notify(0, customNotification);
        } else {
            //mNotificationManager.notify(0, customNotification);
        }

        startForeground(1, customNotification);

    }
}
