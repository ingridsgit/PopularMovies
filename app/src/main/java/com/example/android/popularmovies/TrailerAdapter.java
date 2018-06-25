package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


class TrailerAdapter extends ArrayAdapter<String> {

    public TrailerAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, parent, false);
            TextView trailerView = convertView.findViewById(R.id.trailer_view);
            trailerView.setText(getContext().getString(R.string.trailer) + " " + (position + 1));

            final String trailerKey = getItem(position);
            trailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri trailerUri = Uri.parse("https://www.youtube.com/watch?v=" + trailerKey);
                    Intent watchTrailer = new Intent(Intent.ACTION_VIEW, trailerUri);
                    if (watchTrailer.resolveActivity(getContext().getPackageManager()) != null) {
                        getContext().startActivity(watchTrailer);
                    }
                }
            });
        }
        return convertView;
    }
}
