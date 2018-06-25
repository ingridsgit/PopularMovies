package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        MovieAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String FAVORITES = "favorites";
    private static final int LOADER_ID = 1;
    private static final String KEY_SORT_BY = "sortBy";
    private static final String POPULAR = "popular";
    private static final String BEST_RATED = "top_rated";
    private static final String KEY_LAYOUT_STATE = "state";
    public static Context context;
    private static String sortBy;
    private final LoaderManager loaderManager = getLoaderManager();
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SharedPreferences sharedPreferences;
    private GridLayoutManager layoutManager;
    private Parcelable savedLayoutState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setText(R.string.no_movie);
        emptyView.setVisibility(View.INVISIBLE);

        RecyclerView recyclerView = findViewById(R.id.recycler_grid);
        layoutManager = new GridLayoutManager(this, getSpanCount());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);
        if (savedInstanceState != null){
            savedLayoutState = savedInstanceState.getParcelable(KEY_LAYOUT_STATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SORT_BY, sortBy);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sortBy = sharedPreferences.getString(KEY_SORT_BY, POPULAR);
        switch (sortBy) {
            case POPULAR:
                sortByPopular();
                break;
            case BEST_RATED:
                sortByBestRated();
                break;
            case FAVORITES:
                showFavorites();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LAYOUT_STATE, layoutManager.onSaveInstanceState() );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.most_popular:
                sortByPopular();
                break;
            case R.id.best_rated:
                sortByBestRated();
                break;
            case R.id.favorites:
                showFavorites();
                break;
        }
        return true;
    }

    private void sortByPopular() {
        sortBy = POPULAR;
        setTitle(R.string.most_popular);
        loaderManager.restartLoader(LOADER_ID, null, this);
        movieAdapter.notifyDataSetChanged();
    }

    private void sortByBestRated() {
        sortBy = BEST_RATED;
        setTitle(R.string.best_rated);
        loaderManager.restartLoader(LOADER_ID, null, this);
        movieAdapter.notifyDataSetChanged();
    }

    private void showFavorites() {
        sortBy = FAVORITES;
        setTitle(R.string.favorites);
        loaderManager.restartLoader(LOADER_ID, null, this);
        movieAdapter.notifyDataSetChanged();
    }

    private int getSpanCount(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float columnSize = getResources().getDimension(R.dimen.grid_column_width);
        float spanCount = displayMetrics.widthPixels / columnSize;
        Log.i("MAINACTIVITY", String.valueOf(displayMetrics.widthPixels));
        return (int)spanCount;
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null || !activeNetwork.isConnected()) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
        }
        return new MovieAsyncLoader(this, sortBy);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        progressBar.setVisibility(View.INVISIBLE);
        movieAdapter.setMovies(null);
        if (data != null && !data.isEmpty())
        {
            emptyView.setVisibility(View.INVISIBLE);
            movieAdapter.setMovies(data);
            if (savedLayoutState != null) {
                layoutManager.onRestoreInstanceState(savedLayoutState);
            }
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra("movie", movie);
        startActivity(detailIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_SORT_BY)) {
            String preference = sharedPreferences.getString(key, POPULAR);
            switch (preference) {
                case POPULAR:
                default:
                    sortByPopular();
                    break;
                case BEST_RATED:
                    sortByBestRated();
                    break;
                case FAVORITES:
                    showFavorites();
            }
        }

    }
}
