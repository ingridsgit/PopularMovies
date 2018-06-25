package com.example.android.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.database.DbContract.Favorites;


public class DbContentProvider extends ContentProvider {

    private static final int FAVORITES = 600;
    private static final int FAVORITES_ID = 601;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String LOG_TAG = DbContentProvider.class.getSimpleName();

    static {
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.FAVORITES_PATH, FAVORITES);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.FAVORITES_PATH + "/#", FAVORITES_ID);
    }

    private DbOpenHelper dbOpenHelper;

    @Override
    public boolean onCreate() {
        dbOpenHelper = new DbOpenHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case FAVORITES:
                cursor = database.query(Favorites.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVORITES_ID:
                selection = Favorites.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Favorites.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query, unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                Integer movieId = values.getAsInteger(Favorites.COLUMN_MOVIE_ID);
                if (movieId == null || movieId < 0) {
                    throw new IllegalArgumentException("Invalid movie ID");
                }
                Double vote = values.getAsDouble(Favorites.COLUMN_VOTE);
                if (vote == null || vote < 0) {
                    throw new IllegalArgumentException("Vote requires a positive value");
                }
                String title = values.getAsString(Favorites.COLUMN_TITLE);
                if (title == null || title.isEmpty()) {
                    throw new IllegalArgumentException("Invalid title");
                }
                String posterPath = values.getAsString(Favorites.COLUMN_POSTER_PATH);
                if (posterPath == null || posterPath.isEmpty()) {
                    throw new IllegalArgumentException("Invalid path");
                }
                String releaseDate = values.getAsString(Favorites.COLUMN_RELEASE_DATE);
                if (releaseDate == null || releaseDate.isEmpty()) {
                    throw new IllegalArgumentException("Invalid date");
                }
                long rowId = database.insert(Favorites.TABLE_NAME, null, values);
                if (rowId == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                return ContentUris.withAppendedId(uri, rowId);
            default:
                throw new IllegalArgumentException("Cannot query, unknown URI " + uri);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int deletedRows;
        switch (match) {
            case FAVORITES:
                deletedRows = database.delete(Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES_ID:
                selection = Favorites.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI " + uri);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int updatedRows;
        switch (match) {
            case FAVORITES_ID:
                if (values.containsKey(Favorites.COLUMN_MOVIE_ID)) {
                    Integer movieId = values.getAsInteger(Favorites.COLUMN_MOVIE_ID);
                    if (movieId == null || movieId < 0) {
                        throw new IllegalArgumentException("Invalid movie ID");
                    }
                }

                if (values.containsKey(Favorites.COLUMN_VOTE)) {
                    Double vote = values.getAsDouble(Favorites.COLUMN_VOTE);
                    if (vote == null || vote < 0) {
                        throw new IllegalArgumentException("Vote requires a positive value");
                    }
                }

                if (values.containsKey(Favorites.COLUMN_TITLE)) {
                    String title = values.getAsString(Favorites.COLUMN_TITLE);
                    if (title == null || title.isEmpty()) {
                        throw new IllegalArgumentException("Invalid title");
                    }
                }

                if (values.containsKey(Favorites.COLUMN_POSTER_PATH)) {
                    String posterPath = values.getAsString(Favorites.COLUMN_POSTER_PATH);
                    if (posterPath == null || posterPath.isEmpty()) {
                        throw new IllegalArgumentException("Invalid path");
                    }
                }

                if (values.containsKey(Favorites.COLUMN_RELEASE_DATE)) {
                    String releaseDate = values.getAsString(Favorites.COLUMN_RELEASE_DATE);
                    if (releaseDate == null || releaseDate.isEmpty()) {
                        throw new IllegalArgumentException("Invalid date");
                    }
                }

                if (values.size() == 0) {
                    return 0;
                }

                selection = Favorites.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                updatedRows = database.update(Favorites.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI " + uri);
        }
        return updatedRows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return Favorites.CONTENT_LIST_TYPE;
            case FAVORITES_ID:
                return Favorites.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Invalid URI " + uri);
        }
    }
}
