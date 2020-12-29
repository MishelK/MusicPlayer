package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    final int CAMERA_REQUEST = 0;
    final int GALLERY_REQUEST = 1;
    final int WRITE_PERMISSION_REQUEST = 3;

    boolean isPlaying = false;
    int selectedSongPosition;

    TextView selectedSongNameTv;
    ImageView selectedSongIv;
    Button playBtn, nextBtn, prevBtn, addSongBtn;

    ArrayList<Song> songs;
    RecyclerView recyclerView;
    SongRecyclerAdapter songRecyclerAdapter;

    Uri addedSongImageUri = null;
    File newSongFile;
    boolean camera_access;
    Uri cameraImageUri;

    MusicServiceBroadcastReceiver musicServiceBroadcastReceiver = new MusicServiceBroadcastReceiver(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("music", "oncreate");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());

        //Registering broadcast receiver
        IntentFilter filter = new IntentFilter("com.musicplayer.COMPLETE_ACTION");
        registerReceiver(musicServiceBroadcastReceiver, filter);

        songs = DatabaseHelper.getInstance(this).getSongsArrayList();
        recyclerView = findViewById(R.id.song_list);
        songRecyclerAdapter = new SongRecyclerAdapter(songs, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

        recyclerView.setAdapter(songRecyclerAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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

        if(getIntent().hasExtra("playing")) {
            playBtn.setBackgroundResource(R.drawable.pause);
            isPlaying = true;
        }
        if(getIntent().hasExtra("Position"))
            setSelectedSong(getIntent().getIntExtra("Position", 0));

        if(getIntent().hasExtra("action"))
            executePlayerAction();

        addSongBtn = findViewById(R.id.btn_add_song);
        addSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_addsong);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);
                Uri imageUri;

                Button cancelDialogBtn = dialog.findViewById(R.id.btn_cancel_song_dialog);
                Button addDialogBtn = dialog.findViewById(R.id.btn_add_song_dialog);
                Button selectImageBtn = dialog.findViewById(R.id.btn_select_image_storage);
                Button takeImageBtn = dialog.findViewById(R.id.btn_take_image);

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
                        EditText nameEt = dialog.findViewById(R.id.et_song_name);
                        String Url = urlEt.getText().toString();
                        String name = nameEt.getText().toString();
                        Uri imageUri;
                        if(addedSongImageUri != null)
                            imageUri = addedSongImageUri;
                        else {
                            Uri uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.drawable.default_image);
                            imageUri = uri;
                        }
                        //need to make sure edittext's are not empty
                        String songID = DatabaseHelper.getInstance(MainActivity.this).addSong(name, Url, imageUri.toString(), songs.size()-1);
                        Song song = new Song(songID, name, Url, imageUri, songs.size());
                        songs.add(song);
                        songRecyclerAdapter.notifyItemInserted(songs.size()-1);
                        addedSongImageUri = null;
                        dialog.dismiss();
                    }
                });
                selectImageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent selectImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(selectImage, GALLERY_REQUEST);
                    }
                });
                takeImageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cameraRequest();
                    }
                });
                if(camera_access)
                    takeImageBtn.setVisibility(View.VISIBLE);

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

        if(Build.VERSION.SDK_INT >= 23){
            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(hasWritePermission != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
            }
            else
                camera_access = true;
        }
        else
            camera_access = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        updateSongOrderInDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicServiceBroadcastReceiver);
        stopMusic();
        Log.i("aba", "ondestroy");
    }

    Song deletedSong = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(songs, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            if(fromPosition == selectedSongPosition)
                selectedSongPosition = toPosition;
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deletedSong = songs.get(position);
            switch (direction){
                case ItemTouchHelper.LEFT:
                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog_deletesong);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(true);
                    Button cancelBtn = dialog.findViewById(R.id.btn_cancel_delete_song);
                    Button confirmBtn = dialog.findViewById(R.id.btn_confirm_delete_song);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            songs.remove(position);
                            songRecyclerAdapter.notifyItemRemoved(position);
                            songs.add(position, deletedSong);
                            songRecyclerAdapter.notifyItemInserted(position);
                            dialog.dismiss();
                        }
                    });
                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseHelper.getInstance(MainActivity.this).deleteSong(songs.get(position).getId());
                            songs.remove(position);
                            songRecyclerAdapter.notifyItemRemoved(position);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;

                case ItemTouchHelper.RIGHT:
                    break;
            }
        }
    };

    private void playMusic() {
        if(songs.size() != 0) {
            Intent intent = new Intent(this, MusicPlayerService.class);
            intent.putExtra("Link", songs.get(selectedSongPosition).getLink());
            intent.putExtra("Name", songs.get(selectedSongPosition).getName());
            intent.putExtra("Position", selectedSongPosition);

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
            playSelectedSong();
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
        isPlaying = true;
        playBtn.setBackgroundResource(R.drawable.pause);

    }

    public void songSelected(int position) {

            setSelectedSong(position);
            playSelectedSong();
    }

    public void updateSongOrderInDatabase() { // Will be called onDestroy to save current song order in database
        if(!songs.isEmpty()){
            for(Integer i = 0; i < songs.size(); i++){
                DatabaseHelper.getInstance(this).updateSong(songs.get(i).getId(), songs.get(i).getName(), songs.get(i).getLink(), songs.get(i).getImage_URI().toString(), i);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("aba", "onactivityresult");
        Integer i = requestCode;
        Log.i("aba", i.toString());
        switch(requestCode) {
            case CAMERA_REQUEST: // case TakePicture
                if(resultCode == RESULT_OK){
                    Log.i("aba", "camera request , result ok");
                    addedSongImageUri = cameraImageUri;
                }
                break;
            case GALLERY_REQUEST: // case SelectImage
                if(resultCode == RESULT_OK) {
                    Log.i("aba", "gallery request");
                    addedSongImageUri = data.getData();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_PERMISSION_REQUEST){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Attaching pictures from camera unavailable", Toast.LENGTH_SHORT).show();
            }
            else
                camera_access = true;
        }
    }

    public void cameraRequest(){
        newSongFile = new File(Environment.getExternalStorageDirectory(),"piccdwac.jpg");
        cameraImageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.myapplication.provider", newSongFile);
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.i("aba", "cameraImageUri "+cameraImageUri.toString());
        Log.i("aba", "file.absolutepath  "+newSongFile.getAbsolutePath().toString());
        //takePicture.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(takePicture, CAMERA_REQUEST);
    }

    public void executePlayerAction() {
        String action = getIntent().getStringExtra("action");
        if(action.equals("prev")){
            prevSong();
        }
        if(action.equals("play")){
            playMusic();
        }
        if(action.equals("pause")){
            stopMusic();
        }
        if(action.equals("next")){
            nextSong();
        }
    }

}
