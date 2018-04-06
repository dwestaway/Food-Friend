package com.foodfriend.foodfriend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.foodfriend.foodfriend.AccountActivity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Dan on 21/02/2018.
 */

public class Tab3Messenger extends Fragment {

    ArrayList<Message> arrayList;

    ListView lv;

    String uidSentTo = "";
    String imageUrl = "";



    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab3messenger, container, false);

        arrayList = new ArrayList<Message>();

        lv = view.findViewById(R.id.listMessages);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //reference to database messages
        DatabaseReference ref = database.getReference().child("messages");
        //reference to database users
        final DatabaseReference usersRef = database.getReference().child("users");

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //get current user logged in user id
        final String currentUserID = user.getUid();

        //Load data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                uidSentTo = "";

                //initially clear the lists to avoid data being displayed multiple times and it updates live
                arrayList.clear();

                String nameSentTo = "";
                String lastMessage = "";
                //String imageUrl = "";


                //for every message in messages
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    //get userid from each userdata
                    //String uid = ds.getKey();


                    //if user id is equal to current user id, do not add that user data (so user does not match with themselves)


                        //add userids (keys)
                        //userids.add(uid);

                        //double longitude = (double) ds.child("longitude").getValue();
                        //double latitude = (double) ds.child("latitude").getValue();

                        //get the date of from the users match data
                        String date = (String) ds.child("date").getValue();


                        //use message class
                        //read in uid from message data and compare to current user id

                        uidSentTo = (String) ds.child("sentTo").getValue();

                        lastMessage = (String) ds.child("content").getValue();

                        nameSentTo = (String) ds.child("sentToName").getValue();

                        /*
                        //Create a new Match with all the required users data
                        arrayList.add(new Match(
                                (String) ds.child("profileImage").getValue(),
                                (String) ds.child("name").getValue(),
                                (String) ds.child("foodPOI").getValue(),
                                (String) ds.child("time").getValue()
                        ));

                        */

                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //String imageUrl = "";

                            imageUrl = (String) dataSnapshot.child(uidSentTo).child("profileImage").getValue();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

                //usersRef.getDatabase().child(uidSentTo).child("profileImage").getKey();

                ////////////////////
                //READ ME
                ////////////////////
                //nameSentTo, lastMessage and imageUrl are the Strings that need to be put in an object (Message or a new one)
                //...and sent to a CustomListAdapter that adapts the last message info (from each user they have recieved message) to the list
                //////////////////////
                Toast.makeText(getActivity(), "Name " + nameSentTo + " Message "+ lastMessage + " Image Url " + imageUrl, Toast.LENGTH_LONG).show();


                //Create a new Match with all the required users data
                arrayList.add(new Message(
                        lastMessage,
                        nameSentTo,
                        imageUrl
                ));


                //Create adapter that will be used to apply all the data to the list, this uses Match objects which hold the user data
                MessageListAdapter adapter = new MessageListAdapter(getActivity().getApplicationContext(), R.layout.list_layout, arrayList);
                //set the adapter to the list
                lv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
            }


        });






        //return super.onCreateView(inflater, container, savedInstanceState);

        return view;
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

        /*

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Toast.makeText(getActivity(), data.get(pos).get("Name"), Toast.LENGTH_SHORT).show();

                Toast.makeText(getActivity(), arrayList.get(i).getName(), Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getActivity(), ChatActivity.class));
            }
        });
        */

    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
