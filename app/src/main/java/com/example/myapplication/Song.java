package com.example.myapplication;

import android.net.Uri;

public class Song {

    String id;
    String name;
    String link;
    Uri image_URI;

    public Song(String id, String name, String link, Uri image_URI) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.image_URI = image_URI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Uri getImage_URI() {
        return image_URI;
    }

    public void setImage_URI(Uri image_URI) {
        this.image_URI = image_URI;
    }
}
