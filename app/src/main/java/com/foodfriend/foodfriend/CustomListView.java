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

/**
 * Created by Dan on 08/03/2018.
 */

public class CustomListView extends ArrayAdapter<String> {

    private String[] names;
    private String[] poi;
    private Integer[] images;

    private Activity context;

    public CustomListView(Activity context, String[] names, String[] poi, Integer[]images) {
        super(context, R.layout.list_layout,names);

        this.context = context;
        this.names = names;
        this.poi = poi;
        this.images = images;
    }

    @NonNull
    @Override
    //get the list view and set
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;
        ViewHolder viewHolder = null;

        if(r == null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.list_layout,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.iv.setImageResource(images[position]);
        viewHolder.tv1.setText(names[position]);
        viewHolder.tv2.setText(poi[position]);

        return r;


    }
    class ViewHolder
    {
        TextView tv1;
        TextView tv2;
        ImageView iv;

        ViewHolder(View v)
        {
            tv1 = iv.findViewById(R.id.textName);
            tv2 = iv.findViewById(R.id.textPlace);
            iv = iv.findViewWithTag(R.id.imageUser);
        }
    }
}
