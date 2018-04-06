package com.foodfriend.foodfriend;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dan on 06/04/2018.
 */

public class MessageListAdapter extends ArrayAdapter<Message> {

    ArrayList<Message> messages;
    Context context;
    int resource;

    public MessageListAdapter(Context context, int resource, ArrayList<Message> messages) {
        super(context, resource, messages);

        this.messages = messages;
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
        Message message = getItem(position);

        //set list item image by getting image url from Match object, using Picasso
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageUser);
        //Picasso.with(context).load(message.getTime()).into(imageView);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/food-friend-7ea9a.appspot.com/o/ProfileImages%2Fcc5VpIaO5QRsuSvZSCARzqPLZzp2.jpg?alt=media&token=c5f8c43e-c2e9-485b-b0bb-6a091e63a56b").into(imageView);

        //set list item name text from Message
        TextView name = (TextView) convertView.findViewById(R.id.textName);
        name.setText(message.getUsername());

        //set list item place of interest text from Message
        TextView content = (TextView) convertView.findViewById(R.id.textPOI);
        content.setText(message.getContent());

        TextView empty = (TextView) convertView.findViewById(R.id.textTime);
        empty.setText("");



        return convertView;
    }
}
