package com.foodfriend.foodfriend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.foodfriend.foodfriend.AccountActivity.LoginActivity;
import com.foodfriend.foodfriend.AccountActivity.SignupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dan on 21/02/2018.
 */

public class Tab2Matches extends ListFragment {


    int[] images = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};

    ArrayList<Integer> pics = new ArrayList<>();

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> poi = new ArrayList<>();
    ArrayList<String> times = new ArrayList<>();
    ArrayList<String> userids = new ArrayList<>();

    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        pics.add(R.mipmap.ic_launcher);
        pics.add(R.mipmap.ic_launcher);
        pics.add(R.mipmap.ic_launcher);
        pics.add(R.mipmap.ic_launcher);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("users");

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //get current user logged in user id
        final String currentUserID = user.getUid();

        //Load data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                data.clear();
                names.clear();
                poi.clear();
                times.clear();

                //get the current users location
                //double currentUserLong = (double) dataSnapshot.child(currentUserID).child("longitude").getValue();
                //double currentUserLat = (double) dataSnapshot.child(currentUserID).child("latitude").getValue();


                //for every child/userid in users
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    //get userid from each userdata
                    String uid = ds.getKey();

                    //if user id is equal to current user id, do not add that user data (so user does not match with themselves)
                    if(uid.equals(currentUserID))
                    {

                    }
                    else
                    {

                        //get each users name and add to ArrayList
                        names.add((String) ds.child("name").getValue());
                        //get each users foodPOI and add to ArrayList
                        poi.add((String) ds.child("foodPOI").getValue());
                        //get each users time and add to ArrayList
                        times.add((String) ds.child("time").getValue());

                        //add userids (keys)
                        userids.add(uid);

                        //double longitude = (double) ds.child("longitude").getValue();
                        //double latitude = (double) ds.child("latitude").getValue();

                        String date = (String) ds.child("date").getValue();

                    }
                }

                //Create hashmap for image and text to go in list
                HashMap<String, String> map = new HashMap<String, String>();


                //loop through names
                for(int i = 0; i < names.size(); i++)
                {
                    map = new HashMap<String, String>();
                    map.put("Name", names.get(i) + "\n" + poi.get(i) + "\n" + times.get(i)); //put each piece of data from the ArrayLists into one string and add it to the hashmap
                    //map.put("Image", Integer.toString(images[i]));


                    //prevents app crashing because data is not yet updated from server
                    try {
                        map.put("Image", Integer.toString(pics.get(i)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //prevents list being displayed multiple times because of previous fix
                    if (data.size() < names.size())
                    {
                        data.add(map);
                    }



                }

                //Keys in the map
                String[] from = {"Name","Image"};

                int[] to = {R.id.textName,R.id.imageUser};

                //Adapter
                adapter = new SimpleAdapter(getActivity(), data, R.layout.list_layout, from, to);
                setListAdapter(adapter);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(getActivity(), LoginActivity.class));

                auth.signOut();
                //finish();
            } else {
                //setDataToView(user);

            }
        }

    };


    @Override
    public void onStart() {
        super.onStart();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {


                Toast.makeText(getActivity(), data.get(pos).get("Name"), Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getActivity(), ChatActivity.class));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}
