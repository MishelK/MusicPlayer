package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SongRecyclerAdapter extends RecyclerView.Adapter<SongRecyclerAdapter.ViewHolder> {

    private List<Song> songs;
    private Context context;
    private MainActivity callback;

    public SongRecyclerAdapter(List<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
        this.callback = (MainActivity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.song_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Song song = songs.get(position);

        holder.position = position;
        holder.songNameTv.setText(song.getName());
        Glide.with(context)
                .load(song.getImage_URI())
                .into(holder.songImage);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Integer position;
        ImageView songImage;
        TextView songNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songImage = itemView.findViewById(R.id.song_image);
            songNameTv = itemView.findViewById(R.id.song_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.songSelected(this.getAdapterPosition());
        }
    }

}
