package com.example.android.popularmovies.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class DbContract {

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String FAVORITES_PATH = "favorites";

    private DbContract() {
    }

    public static final class Favorites implements BaseColumns {
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                + "/" + FAVORITES_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                + "/" + FAVORITES_PATH;
        public static final Uri FAVORITES_URI = Uri.withAppendedPath(BASE_URI, FAVORITES_PATH);
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "path";
        public static final String COLUMN_RELEASE_DATE = "date";
        public static final String COLUMN_OVERVIEW = "overview";

    }

}
