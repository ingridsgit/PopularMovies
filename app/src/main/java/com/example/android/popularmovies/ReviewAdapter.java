package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class ReviewAdapter extends ArrayAdapter<String[]> {


    public ReviewAdapter(@NonNull Context context, @NonNull List<String[]> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        }

        TextView authorView = convertView.findViewById(R.id.author_view);
        TextView contentView = convertView.findViewById(R.id.content_view);

        String[] reviews = getItem(position);
        try {
            authorView.setText(reviews[0]);
            contentView.setText(reviews[1]);
        } catch (NullPointerException e){
            e.printStackTrace();
        }


        return convertView;
    }
}

