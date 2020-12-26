package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    boolean isPlaying = false;
    int selectedSongPosition;

    TextView selectedSongNameTv;
    ImageView selectedSongIv;
    Button playBtn, nextBtn, prevBtn, addSongBtn;

    ArrayList<Song> songs;

    MusicServiceBroadcastReceiver musicServiceBroadcastReceiver = new MusicServiceBroadcastReceiver(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Registering broadcast receiver
        IntentFilter filter = new IntentFilter("com.musicplayer.COMPLETE_ACTION");
        registerReceiver(musicServiceBroadcastReceiver, filter);

        //Loading song list from db and filling song list
        ListView listView = findViewById(R.id.song_list);
        songs = DatabaseHelper.getInstance(this).getSongsArrayList();
        SongAdapter songAdapter = new SongAdapter(songs, this);
        listView.setAdapter(songAdapter);

        //Initializing selected song
        if(songs.size() != 0) {
            selectedSongPosition = 0;
            selectedSongNameTv = findViewById(R.id.selected_song_name);
            selectedSongIv = findViewById(R.id.selected_song_image);
            setSelectedSong(0);
        }
        else{
            // here we will display no songs in list
        }

        //Initializing pause-play button
        playBtn = findViewById(R.id.btn_play_pause);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    playBtn.setBackgroundResource(R.drawable.play);
                    stopMusic();
                }
                else{
                    playBtn.setBackgroundResource(R.drawable.pause);
                    playMusic();
                }
                isPlaying = !isPlaying;
            }
        });

        if(getIntent().hasExtra("playing")){
            playBtn.setBackgroundResource(R.drawable.pause);
            isPlaying = true; }

        addSongBtn = findViewById(R.id.btn_add_song);
        addSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_addsong);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);

                Button cancelDialogBtn = dialog.findViewById(R.id.btn_cancel_song_dialog);
                Button addDialogBtn = dialog.findViewById(R.id.btn_add_song_dialog);
                Button selectImageBtn = dialog.findViewById(R.id.btn_select_image);

                cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                addDialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText urlEt = dialog.findViewById(R.id.et_song_url);
                        String Url = urlEt.getText().toString();

                        //need to implement adding song to db and also checking if user selected an image, if not then transfer default image uri
                    }
                });

                dialog.show();
            }
        });

        nextBtn = findViewById(R.id.btn_next);
        prevBtn = findViewById(R.id.btn_prev);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevSong();
            }
        });
    }

    private void playMusic() {
        if(songs.size() != 0) {
            Intent intent = new Intent(this, MusicPlayerService.class);
            intent.putExtra("Link", songs.get(selectedSongPosition).getLink());
            intent.putExtra("Name", songs.get(selectedSongPosition).getName());

            startService(intent);
        }
    }

    private void stopMusic() {

        Intent intent = new Intent(this, MusicPlayerService.class);
        stopService(intent);
    }

    public void nextSong(){
        if(songs.size() != 0) {
            selectedSongPosition++;
            if (selectedSongPosition >= songs.size())
                selectedSongPosition = 0;

            setSelectedSong(selectedSongPosition);
            playSelectedSong();
        }
    }

    public void prevSong(){
        if(songs.size() != 0) {
            selectedSongPosition--;
            if (selectedSongPosition < 0)
                selectedSongPosition = songs.size() - 1;

            setSelectedSong(selectedSongPosition);
        }
    }

    public void setSelectedSong(int position) {

        if (songs.size() > position) {
            selectedSongPosition = position;
            selectedSongNameTv.setText(songs.get(position).getName());
            Glide.with(this)
                    .load(songs.get(position).getImage_URI())
                    .into(selectedSongIv);
        }
    }

    public void playSelectedSong() {

        stopMusic();
        playMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicServiceBroadcastReceiver);
        stopMusic();
    }
}
