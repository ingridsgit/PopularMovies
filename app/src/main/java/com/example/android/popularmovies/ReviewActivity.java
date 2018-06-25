package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {
    private ReviewAdapter reviewAdapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ListView listView;
    private static final String KEY_LIST_STATE = "state";
    private Parcelable listState;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        progressBar = (ProgressBar) findViewById(R.id.review_progress_bar);
        emptyView = (TextView) findViewById(R.id.empty_view);
        emptyView.setText(R.string.no_reviews);
        emptyView.setVisibility(View.GONE);

        Intent starterIntent = getIntent();
        final int movieId = starterIntent.getIntExtra(DetailActivity.KEY_MOVIE_ID, 1);

        reviewAdapter = new ReviewAdapter(getBaseContext(), new ArrayList<String[]>());
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(reviewAdapter);

        new AsyncTask<Integer, Void, ArrayList<String[]>>(){

            @Override
            protected ArrayList<String[]> doInBackground(Integer... params) {
                return DataQuery.getMovieReviews(params[0]);
            }
            @Override
            protected void onPostExecute(ArrayList<String[]> strings) {
                progressBar.setVisibility(View.GONE);
                if (strings == null || strings.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    if (activeNetwork == null || !activeNetwork.isConnected()) {
                        emptyView.setText(R.string.no_internet);
                    }
                } else {
                    reviewAdapter.addAll(strings);
                    restoreListState(savedInstanceState);
                }
            }
        }.execute(movieId);
    }

    private void restoreListState(Bundle savedInstanceState){
        if (savedInstanceState != null){
            listState = savedInstanceState.getParcelable(KEY_LIST_STATE);
            listView.onRestoreInstanceState(listState);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LIST_STATE, listView.onSaveInstanceState());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
