package com.foodfriend.foodfriend;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dan on 21/02/2018.
 */

public class Tab2Matches extends ListFragment {


    String[] names = {"Dan", "Bob", "Steve", "Jacob"};
    String[] poi = {"McDonalds", "Burger King", "Gregs, Rowes", "Yo Sushi"};
    //String[] times = {"Morning", "Mid-day", "Morning", "Evening"};
    int[] images = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};

    ArrayList<String> times;

    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        times = new ArrayList<>();

        times.add("Morning");
        times.add("Mid-day");
        times.add("Evening");
        times.add("Morning");

        HashMap<String, String> map = new HashMap<String, String>();

        for(int i = 0; i < names.length; i++)
        {
            map = new HashMap<String, String>();
            map.put("Name", names[i] + "\n" + poi[i] + "\n" + times.get(i));
            map.put("Image", Integer.toString(images[i]));

            data.add(map);



        }

        //Keys in the map
        String[] from = {"Name","Image"};

        int[] to = {R.id.textName,R.id.imageUser};

        //Adapter
        adapter = new SimpleAdapter(getActivity(), data, R.layout.list_layout, from, to);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

                Toast.makeText(getActivity(), data.get(pos).get("Name"), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
