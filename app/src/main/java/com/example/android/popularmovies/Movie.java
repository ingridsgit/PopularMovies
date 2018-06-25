package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private final int movieId;
    private final double vote;
    private final String title;
    private final String posterPath;
    private final String releaseDate;
    private final String overview;

    public Movie(int movieId, double vote, String title, String posterPath, String releaseDate, String overview) {
        this.movieId = movieId;
        this.vote = vote;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.overview = overview;
    }

    private Movie(Parcel in) {
        this.movieId = in.readInt();
        this.vote = in.readDouble();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.releaseDate = in.readString();
        this.overview = in.readString();
    }

    public double getVote() {
        return vote;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public int getMovieId() {
        return movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeDouble(vote);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(overview);
    }

}
