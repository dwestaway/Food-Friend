package com.foodfriend.foodfriend;

import android.annotation.SuppressLint;
//import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.foodfriend.foodfriend.AccountActivity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Dan on 21/02/2018.
 */

public class Tab2Matches extends Fragment {


    ArrayList<Match> arrayList;
    ArrayList<String> userids;

    ListView lv;


    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab2matches, container, false);

        arrayList = new ArrayList<Match>();
        userids = new ArrayList<>();

        lv = view.findViewById(R.id.listMatches);

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



                //initially clear the lists to avoid data being displayed multiple times and it updates live
                arrayList.clear();
                userids.clear();

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
                        //nothing here
                    }
                    else
                    {

                        //add userids (keys)
                        userids.add(uid);

                        //double longitude = (double) ds.child("longitude").getValue();
                        //double latitude = (double) ds.child("latitude").getValue();

                        //get the date of from the users match data
                        String date = (String) ds.child("date").getValue();


                        //Create a new Match with all the required users data
                        arrayList.add(new Match(
                                (String) ds.child("profileImage").getValue(),
                                (String) ds.child("name").getValue(),
                                (String) ds.child("foodPOI").getValue(),
                                (String) ds.child("time").getValue()
                        ));


                    }
                }


                //Create adapter that will be used to apply all the data to the list, this uses Match objects which hold the user data
                CustomListAdapter adapter = new CustomListAdapter(getActivity().getApplicationContext(), R.layout.list_layout, arrayList);
                //set the adapter to the list
                lv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
            }


        });

        //return super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }





    @Override
    public void onStart() {
        super.onStart();

        //auth.addAuthStateListener(authListener);

        //list item click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                //Toast.makeText(getActivity(), arrayList.get(i).getName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                //send the user who is being clicked to the chat activity, this is to start a chat with the match you click on
                intent.putExtra("sentToName", arrayList.get(i).getName());
                intent.putExtra("sentTo", userids.get(i));


                startActivity(intent);

                //startActivity(new Intent(getActivity(), ChatActivity.class));
            }
        });

    }



}
