package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener{
    final int NOTIF_ID = 1;

    private MediaPlayer player = new MediaPlayer();

    private String songName;
    private Integer position;

    NotificationManager manager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.reset();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String link = intent.getStringExtra("Link");
        songName = intent.getStringExtra("Name");
        position = intent.getIntExtra("Position", 0);

        if(!player.isPlaying()) {
            try {
                player.setDataSource(link);
                //player.prepare(); //sync function which will clog the thread while doing its work
                player.prepareAsync(); //will load the thread on another thread and when finished, will call onPrepared
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(player != null){
            if(player.isPlaying())
                player.stop();
            player.release();
        }
        if(manager != null)
            manager.cancel(NOTIF_ID);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        sendCompleteBroadcast();
        stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        player.start();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("my_music_service","My music service", NotificationManager.IMPORTANCE_LOW);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            //Notification.Builder builder = new Notification.Builder(this, "my_music_service");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this , "my_music_service");
            builder.setSmallIcon(android.R.drawable.ic_media_play);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("playing",true);
            intent.putExtra("Position", position);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.layout.notification_layout, pendingIntent);

            Intent prevIntent = new Intent(this, MainActivity.class);
            prevIntent.putExtra("action", "prev");
            prevIntent.putExtra("playing",true);
            prevIntent.putExtra("Position", position);
            PendingIntent prevPendingIntent = PendingIntent.getActivity(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_notif_prev, prevPendingIntent);

            Intent playIntent = new Intent(this, MainActivity.class);
            playIntent.putExtra("action", "play");
            playIntent.putExtra("playing",true);
            playIntent.putExtra("Position", position);
            PendingIntent playPendingIntent = PendingIntent.getActivity(this, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_notif_play, playPendingIntent);

            Intent pauseIntent = new Intent(this, MainActivity.class);
            pauseIntent.putExtra("action", "pause");
            pauseIntent.putExtra("Position", position);
            PendingIntent pausePendingIntent = PendingIntent.getActivity(this, 2, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_notif_pause, pausePendingIntent);

            Intent nextIntent = new Intent(this, MainActivity.class);
            nextIntent.putExtra("action", "next");
            nextIntent.putExtra("playing",true);
            nextIntent.putExtra("Position", position);
            PendingIntent nextPendingIntent = PendingIntent.getActivity(this, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_notif_next, nextPendingIntent);

            builder.setContent(remoteViews);
            manager.notify(NOTIF_ID, builder.build());

        }
        else{
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null);
            builder.setSmallIcon(android.R.drawable.ic_media_play);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("playing",true);
            intent.putExtra("Position", position);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.layout.notification_layout, pendingIntent);

            Intent prevIntent = new Intent(this, MainActivity.class);
            prevIntent.putExtra("action", "prev");
            prevIntent.putExtra("playing",true);
            prevIntent.putExtra("Position", position);
            PendingIntent prevPendingIntent = PendingIntent.getActivity(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_notif_prev, prevPendingIntent);

            Intent playIntent = new Intent(this, MainActivity.class);
            playIntent.putExtra("action", "play");
            playIntent.putExtra("playing",true);
            playIntent.putExtra("Position", position);
            PendingIntent playPendingIntent = PendingIntent.getActivity(this, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_notif_play, playPendingIntent);

            Intent pauseIntent = new Intent(this, MainActivity.class);
            pauseIntent.putExtra("action", "pause");
            pauseIntent.putExtra("Position", position);
            PendingIntent pausePendingIntent = PendingIntent.getActivity(this, 2, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_notif_pause, pausePendingIntent);

            Intent nextIntent = new Intent(this, MainActivity.class);
            nextIntent.putExtra("action", "next");
            nextIntent.putExtra("playing",true);
            nextIntent.putExtra("Position", position);
            PendingIntent nextPendingIntent = PendingIntent.getActivity(this, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_notif_next, nextPendingIntent);

            builder.setContent(remoteViews);
            manager.notify(NOTIF_ID, builder.build());

        }

    }

    private void sendCompleteBroadcast(){
        Intent intent = new Intent("com.musicplayer.COMPLETE_ACTION");
        sendBroadcast(intent);
    }
}
