package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import com.example.android.popularmovies.database.DbContract.Favorites;

import java.util.ArrayList;

class MovieAsyncLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private final String sortBy;

    public MovieAsyncLoader(Context context, String sortBy) {
        super(context);
        this.sortBy = sortBy;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        if (sortBy.equals(MainActivity.FAVORITES)) {
            Cursor favorites = getContext().getContentResolver().query(Favorites.FAVORITES_URI, null, null, null, null);
            int movieId;
            double vote;
            String title;
            String posterPath;
            String releaseDate;
            String overview;
            ArrayList<Movie> favoriteList = new ArrayList<>();
            if (favorites != null && favorites.moveToFirst()) {
                for (int i = 0; i < favorites.getCount(); i++) {
                    favorites.moveToPosition(i);
                    movieId = favorites.getInt(favorites.getColumnIndex(Favorites.COLUMN_MOVIE_ID));
                    vote = favorites.getDouble(favorites.getColumnIndex(Favorites.COLUMN_VOTE));
                    title = favorites.getString(favorites.getColumnIndex(Favorites.COLUMN_TITLE));
                    posterPath = favorites.getString(favorites.getColumnIndex(Favorites.COLUMN_POSTER_PATH));
                    releaseDate = favorites.getString(favorites.getColumnIndex(Favorites.COLUMN_RELEASE_DATE));
                    overview = favorites.getString(favorites.getColumnIndex(Favorites.COLUMN_OVERVIEW));
                    Movie newMovie = new Movie(movieId, vote, title, posterPath, releaseDate, overview);
                    favoriteList.add(newMovie);
                }
            }
            favorites.close();
            return favoriteList;
        } else {
            return DataQuery.getDataFromApi(sortBy);
        }
    }


}
