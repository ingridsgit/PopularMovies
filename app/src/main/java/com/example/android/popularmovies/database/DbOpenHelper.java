package com.example.android.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.database.DbContract.Favorites;


class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DbContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE = "CREATE TABLE " + Favorites.TABLE_NAME +
                "(" + Favorites.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Favorites.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + Favorites.COLUMN_VOTE + " REAL NOT NULL, "
                + Favorites.COLUMN_TITLE + " TEXT NOT NULL, "
                + Favorites.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + Favorites.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + Favorites.COLUMN_OVERVIEW + " TEXT); ";
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
