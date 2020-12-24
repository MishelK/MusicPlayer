package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance = null;

    public static final String DATABASE_NAME = "MusicPlayer.db";

    public static final String TABLE_NAME_SONGS = "songs_table";
    public static final String COL_0_SONGS_ID = "Song_Id";
    public static final String COL_1_SONGS_NAME = "Song_Name";
    public static final String COL_2_SONGS_LINK = "Song_Link";
    public static final String COL_3_SONGS_IMAGE = "Song_Img_Path";

    public static DatabaseHelper getInstance(Context context){

        if(instance == null)
            instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME_SONGS + "(" + COL_0_SONGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_1_SONGS_NAME + " TEXT," + COL_2_SONGS_LINK + " TEXT," + COL_3_SONGS_IMAGE + " TEXT)"); // Creating the table

        addSong("bob", "http://www.syntax.org.il/xtra/bob.m4a", "");
        addSong("bob1", "http://www.syntax.org.il/xtra/bob1.m4a", "");
        addSong("bob2", "http://www.syntax.org.il/xtra/bob2.mp3", "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SONGS);
        onCreate(db);
    }

    public boolean addSong(String name, String link, String imagePath) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_SONGS_NAME, name);
        contentValues.put(COL_2_SONGS_LINK, link);
        contentValues.put(COL_3_SONGS_IMAGE, imagePath);

        long result = db.insert(TABLE_NAME_SONGS, null, contentValues); // Inserting new data into the table
        return result != -1;
    }

    public boolean deleteSong(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME_SONGS, COL_0_SONGS_ID + " = ?", new String[]{id});
        return result != 0;
    }

    public Song[] getAllSongs() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME_SONGS, null); // Returns all songs in table
        Song[] songs = null;

        if(result.getCount() > 0) {
            songs = new Song[result.getCount()];
            int i = 0;

            while(result.moveToNext()) {
                songs[i].song_id = result.getString(0);
                songs[i].song_name = result.getString(1);
                songs[i].song_link = result.getString(2);
                songs[i].song_image_path = result.getString(3);
                i++;
            }
        }
        return songs;
    }



}
