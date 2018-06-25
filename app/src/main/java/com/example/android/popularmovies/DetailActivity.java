package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.database.DbContract.Favorites;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    static final String VIDEOS = "videos";
    static final String REVIEWS = "reviews";
    private static final String KEY_MOVIE = "movie";
    static final String KEY_MOVIE_ID = "movieId";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private final ContentValues values = new ContentValues();
    private String currentTitle;
    private double currentVote;
    private String currentDate;
    private String currentPath;
    private String currentOverview;
    private Movie currentMovie;
    private int movieId;
    private View star;
    private Cursor selectedMovie;
    private TrailerAdapter trailerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState != null) {
            currentMovie = savedInstanceState.getParcelable(KEY_MOVIE);
        } else {
            Intent starterIntent = getIntent();
            currentMovie = starterIntent.getExtras().getParcelable(KEY_MOVIE);
        }

        TextView titleView = (TextView) findViewById(R.id.title_view);
        currentTitle = currentMovie.getTitle();
        titleView.setText(currentTitle);

        TextView voteView = (TextView) findViewById(R.id.vote);
        currentVote = currentMovie.getVote();
        voteView.setText(String.valueOf(currentVote) + "/10");

        TextView releaseDateView = (TextView) findViewById(R.id.release_date);
        String[] releaseDates = currentMovie.getReleaseDate().split("-");
        currentDate = releaseDates[0];
        releaseDateView.setText(currentDate);

        TextView overviewView = (TextView) findViewById(R.id.overview);
        currentOverview = currentMovie.getOverview();
        if (currentOverview != null && !currentOverview.isEmpty()) {
            overviewView.setText(currentOverview);
        }

        ImageView posterView = (ImageView) findViewById(R.id.poster);
        currentPath = currentMovie.getPosterPath();
        Picasso.with(getBaseContext()).load(IMAGE_BASE_URL + currentPath).into(posterView);

        star = findViewById(R.id.star);
        movieId = currentMovie.getMovieId();
        if (checkIfFavorite()) {
            star.setActivated(true);
        } else {
            star.setActivated(false);
        }

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfFavorite()) {
                    removeFromFavorites();
                } else {
                    addToFavorites();
                }
            }
        });

        Button reviewButton = (Button) findViewById(R.id.reviews);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewIntent = new Intent(DetailActivity.this, ReviewActivity.class);
                reviewIntent.putExtra(KEY_MOVIE_ID, movieId);
                startActivity(reviewIntent);
            }
        });

        trailerAdapter = new TrailerAdapter(getBaseContext(), new ArrayList<String>());
        ListView trailerList = (ListView) findViewById(R.id.trailer_list);
        trailerList.setAdapter(trailerAdapter);

        TrailerAsyncTask trailerAsyncTask = new TrailerAsyncTask();
        trailerAsyncTask.execute(movieId);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_MOVIE, currentMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkIfFavorite() {
        boolean isFavorite;
        String[] selectionArgs = new String[]{String.valueOf(movieId)};
        selectedMovie = getContentResolver().query(Favorites.FAVORITES_URI, null, Favorites.COLUMN_MOVIE_ID + "=?", selectionArgs, null);
        if (selectedMovie != null && selectedMovie.moveToFirst()) {
            isFavorite = true;
        } else {
            isFavorite = false;
        }
        return isFavorite;
    }

    private void removeFromFavorites() {
        if (selectedMovie.moveToFirst()) {
            long favoriteId = selectedMovie.getLong(selectedMovie.getColumnIndex(Favorites._ID));
            Uri movieUri = ContentUris.withAppendedId(Favorites.FAVORITES_URI, favoriteId);
            int deletedRows = getContentResolver().delete(movieUri, null, null);
            if (deletedRows > 0) {
                star.setActivated(false);
            }
        }
    }

    private void addToFavorites() {
        values.put(Favorites.COLUMN_MOVIE_ID, movieId);
        values.put(Favorites.COLUMN_VOTE, currentVote);
        values.put(Favorites.COLUMN_TITLE, currentTitle);
        values.put(Favorites.COLUMN_POSTER_PATH, currentPath);
        values.put(Favorites.COLUMN_RELEASE_DATE, currentDate);
        if (currentOverview != null && !currentOverview.isEmpty()) {
            values.put(Favorites.COLUMN_OVERVIEW, currentOverview);
        }
        Uri newFavorite = getContentResolver().insert(Favorites.FAVORITES_URI, values);
        if (newFavorite != null) {
            star.setActivated(true);
        }
    }

    private class TrailerAsyncTask extends AsyncTask<Integer, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            return DataQuery.getMovieTrailers(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (strings != null && !strings.isEmpty()) {
                trailerAdapter.addAll(strings);
            }
        }
    }
}
