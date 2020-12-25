package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean isPlaying = false;
    int selectedSongPosition;

    TextView selectedSongNameTv;
    ImageView selectedSongIv;
    Button playBtn;

    ArrayList<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loading song list from db and filling song list
        ListView listView = findViewById(R.id.song_list);
        songs = DatabaseHelper.getInstance(this).getSongsArrayList();
        SongAdapter songAdapter = new SongAdapter(songs, this);
        listView.setAdapter(songAdapter);

        //Initializing selected song
        selectedSongPosition = 0;
        selectedSongNameTv = findViewById(R.id.selected_song_name);
        selectedSongIv = findViewById(R.id.selected_song_image);
        setSelectedSong(0);


        //Initializing pause-play button
        playBtn = findViewById(R.id.btn_play_pause);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    playBtn.setText(("Play"));
                    stopMusic();
                }
                else{
                    playBtn.setText("Stop");
                    playMusic();
                }
                isPlaying = !isPlaying;
            }
        });

        if(getIntent().hasExtra("playing")){
            playBtn.setText("Stop");
            isPlaying = true; }



    }

    private void playMusic() {

        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.putExtra("Link", songs.get(selectedSongPosition).getLink());
        intent.putExtra("Name", songs.get(selectedSongPosition).getName());
        startService(intent);
    }

    private void stopMusic() {

        Intent intent = new Intent(this, MusicPlayerService.class);
        stopService(intent);
    }

    public void setSelectedSong(int position) {

        if (songs.size() > position) {
            selectedSongNameTv.setText(songs.get(position).getName());
            Glide.with(this)
                    .load(songs.get(position).getImage_URI())
                    .into(selectedSongIv);
        }
    }
}
