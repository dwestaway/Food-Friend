package com.foodfriend.foodfriend;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

/**
 * Created by Dan on 21/02/2018.
 */

public class Tab2Matches extends Fragment {

    ListView list;
    Context context;
    Adapter adapter;

    String[] names = {"Dan", "Bob", "Steve", "Grapes"};
    String[] poi = {"McDonalds", "Burger King", "Gregs", "Yo Sushi"};
    Integer[] images = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab2matches, container, false);



        list = rootView.findViewById(R.id.listMain);

        CustomListView customListView = new CustomListView(getActivity(), names, poi, images);

        //list.setAdapter(customListView);



        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
