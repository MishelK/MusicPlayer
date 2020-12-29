package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance = null;

    public static final String DATABASE_NAME = "MusicPlayer.db";

    public static final String TABLE_NAME_SONGS = "songs_table";
    public static final String COL_0_SONGS_ID = "Song_Id";
    public static final String COL_1_SONGS_NAME = "Song_Name";
    public static final String COL_2_SONGS_LINK = "Song_Link";
    public static final String COL_3_SONGS_IMAGE = "Song_Img_Path"; //will hold the image's URI
    public static final String COL_4_SONGS_POSITION = "Song_Position";

    private Context context;

    public static DatabaseHelper getInstance(Context context){

        if(instance == null)
            instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME_SONGS + "(" + COL_0_SONGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_1_SONGS_NAME + " TEXT," + COL_2_SONGS_LINK + " TEXT," + COL_3_SONGS_IMAGE + " TEXT," + COL_4_SONGS_POSITION + " INTEGER)"); // Creating the table

        seedDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SONGS);
        onCreate(db);
    }

    public String addSong(String name, String link, String imagePath, Integer position) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_SONGS_NAME, name);
        contentValues.put(COL_2_SONGS_LINK, link);
        contentValues.put(COL_3_SONGS_IMAGE, imagePath);
        contentValues.put(COL_4_SONGS_POSITION, position);

        String result = Long.toString(db.insert(TABLE_NAME_SONGS, null, contentValues)); // Inserting new data into the table
        return result;
    }

    public boolean deleteSong(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME_SONGS, COL_0_SONGS_ID + " = ?", new String[]{id});
        return result != 0;
    }

    public boolean updateSong(String id, String name, String link, String imagePath, Integer position) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_0_SONGS_ID, id);
        contentValues.put(COL_1_SONGS_NAME, name);
        contentValues.put(COL_2_SONGS_LINK, link);
        contentValues.put(COL_3_SONGS_IMAGE, imagePath);
        contentValues.put(COL_4_SONGS_POSITION, position);
        long result = db.update(TABLE_NAME_SONGS, contentValues, COL_0_SONGS_ID + " = ?", new String[]{id});
        return result != 0;
    }

    public ArrayList<Song> getSongsArrayList() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME_SONGS, null); // Returns all songs in table
        ArrayList<Song> songs = new ArrayList<>();

        if(result.getCount() > 0) {
            while(result.moveToNext()) {
                songs.add(new Song(result.getString(0), result.getString(1), result.getString(2), Uri.parse(result.getString(3)), result.getInt(4)));
            }
        }

        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                Integer o1Position = o1.getPosition();
                Integer o2Position = o2.getPosition();
                return o1Position - o2Position;
            }
        });

        return songs;
    }

    public boolean addSongForSeed(String name, String link, String imagePath, Integer position, SQLiteDatabase db) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_SONGS_NAME, name);
        contentValues.put(COL_2_SONGS_LINK, link);
        contentValues.put(COL_3_SONGS_IMAGE, imagePath);
        contentValues.put(COL_4_SONGS_POSITION, position);

        long result = db.insert(TABLE_NAME_SONGS, null, contentValues); // Inserting new data into the table
        return result != -1;
    }

    public void seedDatabase(SQLiteDatabase db){

        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.default_image);
        String defaultImageUri = uri.toString();

        addSongForSeed("bob", "https://www.syntax.org.il/xtra/bob.m4a", defaultImageUri,0 , db);
        addSongForSeed("bob1", "https://www.syntax.org.il/xtra/bob1.m4a", defaultImageUri,1, db);
        addSongForSeed("bob2", "https://www.syntax.org.il/xtra/bob2.mp3", defaultImageUri,2, db);
    }


}
