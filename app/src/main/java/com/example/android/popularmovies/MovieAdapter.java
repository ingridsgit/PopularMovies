package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private final MovieAdapterOnClickHandler clickHandler;
    private ArrayList<Movie> movies = new ArrayList<>();

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View inflatedView = inflater.inflate(R.layout.movie_grid_item, parent, false);
        return new MovieAdapterViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie currentMovie = movies.get(position);
        String posterPath = currentMovie.getPosterPath();
        Picasso.with(MainActivity.context)
                .load(IMAGE_BASE_URL + posterPath)
                .placeholder(R.color.dark_text)
                .error(R.color.dark_text)
                .into(holder.moviePreview);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        } else {
            return movies.size();
        }
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView moviePreview;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            moviePreview = itemView.findViewById(R.id.movie_preview);
            moviePreview.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie currentMovie = movies.get(adapterPosition);
            clickHandler.onClick(currentMovie);
        }
    }
}
