package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView selectedSongNameTv;
    ImageView selectedSongIv;

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
        selectedSongNameTv = findViewById(R.id.selected_song_name);
        selectedSongIv = findViewById(R.id.selected_song_image);
        selectedSongNameTv.setText(songs.get(0).getName());
        Glide.with(this)
                .load(songs.get(0).getImage_URI())
                .into(selectedSongIv);

    }

    public void setSelectedSong(int position) {

        selectedSongNameTv.setText(songs.get(position).getName());
        Glide.with(this)
                .load(songs.get(position).getImage_URI())
                .into(selectedSongIv);

    }
}
