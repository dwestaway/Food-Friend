package com.foodfriend.foodfriend;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 03/04/2018.
 */

public class CustomListAdapter extends ArrayAdapter<Match> {

    ArrayList<Match> matches;
    Context context;
    int resource;

    public CustomListAdapter(Context context, int resource, ArrayList<Match> matches) {
        super(context, resource, matches);

        this.matches = matches;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.list_layout, null, true);

        }
        Match match = getItem(position);

        //set list item image by getting image url from Match object, using Picasso
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageUser);
        Picasso.with(context).load(match.getImage()).into(imageView);

        //set list item name text from Match
        TextView name = (TextView) convertView.findViewById(R.id.textName);
        name.setText(match.getName());

        //set list item place of interest text from Match
        TextView poi = (TextView) convertView.findViewById(R.id.textPOI);
        poi.setText(match.getPoi());


        return convertView;
    }
}
