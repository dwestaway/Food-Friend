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
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Dan on 21/02/2018.
 */

public class Tab3Messenger extends Fragment {

    ArrayList<Message> arrayList;
    ArrayList<String> uniqueUserMessages;

    ListView lv;

    String uidSentTo = "";
    String uidSentFrom = "";
    //set as default profile avatar (this will be set if user has not uploaded a profile picture or it fails to load
    String imageUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";

    String recipient = "";


    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    final DatabaseReference usersRef = database.getReference().child("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab3messenger, container, false);

        arrayList = new ArrayList<Message>();
        uniqueUserMessages = new ArrayList<>();


        lv = view.findViewById(R.id.listMessages);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        //reference to database messages
        DatabaseReference ref = database.getReference().child("messages");
        //reference to database users


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //get current user logged in user id
        final String currentUserID = user.getUid();

        //Load data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //initially clear the lists to avoid data being displayed multiple times and it updates live
                arrayList.clear();
                uniqueUserMessages.clear();

                String nameSentTo;
                String nameSentFrom;
                String lastMessage;
                //String imageUrl;




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
                        uidSentFrom = (String) ds.child("sentFrom").getValue();

                        lastMessage = (String) ds.child("content").getValue();

                        nameSentTo = (String) ds.child("sentToName").getValue();
                        nameSentFrom = (String) ds.child("sentFromName").getValue();




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

                        //remove repeated chats from arrayList

                        /*
                        //Check if sender or reciever is the current user, if no then do not display other peoples messages
                        if(uidSentTo.equals(currentUserID) || !uidSentFrom.equals(currentUserID))
                        {

                            uniqueUserMessages.add(uidSentFrom);

                            getRecipientName(uidSentFrom);



                            //Create a new Match with all the required users data
                            arrayList.add(new Message(
                                    lastMessage,
                                    recipient,
                                    imageUrl
                            ));

                        }
                        else if (!uidSentTo.equals(currentUserID) || uidSentFrom.equals(currentUserID))
                        {
                            getRecipientName(uidSentTo);

                            uniqueUserMessages.add(uidSentTo);

                            Toast.makeText(getActivity(), recipient, Toast.LENGTH_LONG).show();

                            //Create a new Match with all the required users data
                            arrayList.add(new Message(
                                    lastMessage,
                                    recipient,
                                    imageUrl
                            ));
                        }
                        */




                    /*

                    for(Message m : arrayList)
                    {
                        //Toast.makeText(getActivity(), m.getSentFromName() + " " + nameSentTo, Toast.LENGTH_LONG).show();

                        if(!m.getSentFromName().equals(nameSentTo))
                        {

                        }
                    }
                    */
                    //Create a new Match with all the required users data
                    arrayList.add(new Message(
                            lastMessage,
                            nameSentTo,
                            imageUrl
                    ));

                    //int test = arrayList.size();



                }

                Toast.makeText(getActivity(), Integer.toString(arrayList.size()) , Toast.LENGTH_LONG).show();

                //Collections.reverse(arrayList);

                for(int i = 0; i < arrayList.size(); i++)
                {

                    String recipient = arrayList.get(i).getSentToName();

                    for(int j = 0; j < arrayList.size(); j++)
                    {
                        if(recipient.equals(arrayList.get(j).getSentToName()))
                        {
                            arrayList.remove(i);
                        }
                    }
                }


                //Toast.makeText(getActivity(), "Name " + nameSentTo + " Message "+ lastMessage + " Image Url " + imageUrl, Toast.LENGTH_LONG).show();





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


    void getRecipientName(final String userID)
    {

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipient = (String) dataSnapshot.child(userID).child("name").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
