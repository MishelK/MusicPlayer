package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicServiceBroadcastReceiver extends BroadcastReceiver {

    MainActivity callback;

    public MusicServiceBroadcastReceiver(Context context) {
        this.callback =(MainActivity) context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if("com.musicplayer.COMPLETE_ACTION".equals(intent.getAction())) {
            callback.nextSong();
        }
    }
}
