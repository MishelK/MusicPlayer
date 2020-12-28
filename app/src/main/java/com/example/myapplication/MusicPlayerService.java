package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener{

    private MediaPlayer player = new MediaPlayer();

    private String songName;
    private Integer position;

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
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(this, "my_music_service");
            builder.setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Playing music")
                    .setContentText("Playing "+ songName +", enjoy");

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("playing",true);
            intent.putExtra("Position", position);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            startForeground(1,builder.build());
        }
        else{
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Playing music")
                    .setContentText("Playing "+ songName +", enjoy");

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("playing",true);
            intent.putExtra("Position", position);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            startForeground(1,builder.build());

        }

    }

    private void sendCompleteBroadcast(){
        Intent intent = new Intent("com.musicplayer.COMPLETE_ACTION");
        sendBroadcast(intent);
    }
}
