package com.example.android.popularmovies;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

final class DataQuery {
    // Add your Api key here //
    private static final String API_KEY = "";

    private static final String LOG_TAG = DataQuery.class.getSimpleName();
    private static final String URI_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String KEY_NAME = "api_key";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_ID = "id";
    private static final String KEY_VOTE = "vote_average";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PATH = "poster_path";
    private static final String KEY_DATE = "release_date";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_KEY = "key";
    private static final String KEY_SITE = "site";
    private static final String VALUE_YOUTUBE = "youtube";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    static ArrayList<Movie> getDataFromApi(String sortBy) {
        URL url = buildUrl(sortBy);
        InputStream inputStream = makeHttpRequest(url);
        String response = readFromStream(inputStream);
        return extractMoviesFromJson(response);
    }

    static ArrayList<String> getMovieTrailers(int movieId) {
        URL url = buildUrl(movieId, DetailActivity.VIDEOS);
        InputStream inputStream = makeHttpRequest(url);
        String response = readFromStream(inputStream);
        return extractTrailersFromJson(response);
    }

    static ArrayList<String[]> getMovieReviews(int movieId) {
        URL url = buildUrl(movieId, DetailActivity.REVIEWS);
        InputStream inputStream = makeHttpRequest(url);
        String response = readFromStream(inputStream);
        return extractReviewsFromJson(response);
    }

    private static URL buildUrl(int movieId, String detail) {
        URL url = null;
        Uri.Builder uri = Uri.parse(URI_BASE)
                .buildUpon()
                .appendEncodedPath(String.valueOf(movieId))
                .appendEncodedPath(detail)
                .appendQueryParameter(KEY_NAME, API_KEY);

        try {
            url = new URL(uri.build().toString());
        } catch (MalformedURLException e) {
            Toast.makeText(MainActivity.context, R.string.url_error, Toast.LENGTH_LONG).show();
        }
        return url;
    }

    private static URL buildUrl(String sortBy) {
        URL url = null;
        Uri.Builder uri = Uri.parse(URI_BASE)
                .buildUpon()
                .appendEncodedPath(sortBy)
                .appendQueryParameter(KEY_NAME, API_KEY);

        try {
            url = new URL(uri.build().toString());
        } catch (MalformedURLException e) {
            Toast.makeText(MainActivity.context, R.string.url_error, Toast.LENGTH_LONG).show();
        }
        return url;
    }

    private static InputStream makeHttpRequest(URL url) {
        HttpURLConnection httpURLConnection;
        InputStream inputStream = null;
        int responseCode;
        if (url != null) {
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    Log.e(LOG_TAG, "Request impossible. Response code: " + responseCode);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return inputStream;
    }

    private static String readFromStream(InputStream inputStream) {
        StringBuilder jsonResponse = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            try {
                line = bufferedReader.readLine();
                while (line != null) {
                    jsonResponse.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonResponse.toString();
    }

    private static ArrayList<Movie> extractMoviesFromJson(String jsonResponse) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (!TextUtils.isEmpty(jsonResponse)) {
            try {
                JSONObject response = new JSONObject(jsonResponse);
                JSONArray results = response.optJSONArray(KEY_RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject movie = results.optJSONObject(i);
                    int movieId = movie.optInt(KEY_ID);
                    double vote = movie.optDouble(KEY_VOTE);
                    String title = movie.optString(KEY_TITLE);
                    String posterPath = movie.optString(KEY_PATH);
                    String releaseDate = movie.optString(KEY_DATE);
                    String overview = movie.optString(KEY_OVERVIEW);
                    movies.add(new Movie(movieId, vote, title, posterPath, releaseDate, overview));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movies;
    }

    private static ArrayList<String> extractTrailersFromJson(String jsonResponse) {
        ArrayList<String> trailers = new ArrayList<>();
        if (jsonResponse != null) {
            try {
                JSONObject response = new JSONObject(jsonResponse);
                JSONArray results = response.optJSONArray(KEY_RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject trailer = results.optJSONObject(i);
                    String key = trailer.optString(KEY_KEY);
                    String site = trailer.optString(KEY_SITE);
                    if (site.equalsIgnoreCase(VALUE_YOUTUBE)) {
                        trailers.add(key);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return trailers;
    }

    private static ArrayList<String[]> extractReviewsFromJson(String jsonResponse) {
        ArrayList<String[]> reviews = new ArrayList<>();
        if (jsonResponse != null) {
            try {
                JSONObject response = new JSONObject(jsonResponse);
                JSONArray results = response.optJSONArray(KEY_RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject review = results.optJSONObject(i);
                    String author = review.optString(KEY_AUTHOR);
                    String content = review.optString(KEY_CONTENT);
                    String[] values = new String[]{author, content};
                    reviews.add(values);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reviews;
    }

}
