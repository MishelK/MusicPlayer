package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class SongAdapter extends BaseAdapter implements View.OnClickListener {

    private List<Song> songs;
    private Context context;

    public SongAdapter(List<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.song_layout, parent, false);
        }

        Song song = songs.get(position);

        TextView songNameTv = convertView.findViewById(R.id.song_name);
        ImageView imageView = convertView.findViewById(R.id.song_image);

        convertView.setTag(song.getId());
        convertView.setOnClickListener(this);

        songNameTv.setText(song.getName());
        Glide.with(context)
                .load(song.getImage_URI())
                .into(imageView);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        
    }

}
