package com.foodfriend.foodfriend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import static android.content.ContentValues.TAG;

/**
 * Created by Dan on 21/02/2018.
 */

public class Tab3Messenger extends Fragment {

    ArrayList<Message> arrayList;
    ArrayList<Message> uniqueMessages;
    ArrayList<String> messageUIDs;

    ArrayList<String> uniqueUIDs;

    ListView lv;

    String uidSentTo = "";
    String uidSentFrom = "";
    //set as default profile avatar (this will be set if user has not uploaded a profile picture or it fails to load
    String imageUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";

    String recipient = "";


    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    //final DatabaseReference usersRef = database.getReference().child("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab3messenger, container, false);

        arrayList = new ArrayList<Message>();
        uniqueMessages = new ArrayList<>();

        messageUIDs = new ArrayList<>();
        uniqueUIDs = new ArrayList<>();

        lv = view.findViewById(R.id.listMessages);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //reference to database messages
        DatabaseReference ref = database.getReference();

        //get instance of the current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //get current user logged in user id
        final String currentUserID = user.getUid();


        //Load data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //initially clear the lists to avoid data being displayed multiple times and it updates live
                arrayList.clear();
                messageUIDs.clear();

                String nameSentTo = "";
                String lastMessage = "";


                //for every chatroom in messages
                for(DataSnapshot ds : dataSnapshot.child("messages").getChildren()){

                    //get userid from each userdata
                    //String uid = ds.getKey();

                    //double longitude = (double) ds.child("longitude").getValue();
                    //double latitude = (double) ds.child("latitude").getValue();

                    //get the date of from the users match data
                    String date = (String) ds.child("date").getValue();

                    //This is the name of the chat between 2 people, it is named using both users ID
                    String chatRoom = ds.getKey();

                    //Split the chat room name into the 2 seperate userid's
                    String[] chatRoomIDs = splitByNumber(chatRoom, 29);

                    //if the current user ID is one of the id's in the chat room name
                    if(currentUserID.equals(chatRoomIDs[0]))
                    {
                        recipient = chatRoomIDs[1];

                        //loop through messages and find the content (message), this replace the previous messages the last one is shown
                        for(DataSnapshot messages : ds.getChildren())
                        {
                            lastMessage = (String) messages.child("content").getValue();
                        }

                        //Create a new Match with all the required users data
                        arrayList.add(new Message(
                                lastMessage,
                                nameSentTo,
                                imageUrl
                        ));

                        //add each user ID for each message to an arrayList
                        messageUIDs.add(recipient);

                    }
                    else if(currentUserID.equals(chatRoomIDs[1]))
                    {
                        recipient = chatRoomIDs[0];

                        //loop through messages and find the content (message), this replace the previous messages the last one is shown
                        for(DataSnapshot messages : ds.getChildren())
                        {
                            lastMessage = (String) messages.child("content").getValue();
                        }

                        //Create a new Match with all the required users data
                        arrayList.add(new Message(
                                lastMessage,
                                nameSentTo,
                                imageUrl
                        ));

                        //add each user ID for each message to an arrayList
                        messageUIDs.add(recipient);

                    }
                    //else: do not add message to the arrayList

                }


                //Loop through the arrayList of messages, get the users image using the userID
                for(int i = 0; i < arrayList.size(); i++)
                {
                    //get the user for the users image, using list of all user ids from chat messages to find the user image
                    String url = (String) dataSnapshot.child("users").child(messageUIDs.get(i)).child("profileImage").getValue();
                    //get the users name and set it to each message in arrayList
                    String sentToName = (String) dataSnapshot.child("users").child(messageUIDs.get(i)).child("name").getValue();

                    //update each arrayList message to new image url
                    arrayList.get(i).setTime(url);
                    arrayList.get(i).setUsername(sentToName);
                }


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

        return view;
    }

    //Split string method, uses String and character number to split at
    private String[] splitByNumber(String s, int chunkSize){
        int chunkCount = (s.length() / chunkSize) + (s.length() % chunkSize == 0 ? 0 : 1);
        String[] returnVal = new String[chunkCount];
        for(int i=0;i<chunkCount;i++){
            returnVal[i] = s.substring(i*chunkSize, Math.min((i+1)*chunkSize-1, s.length()));
        }
        return returnVal;
    }


    @Override
    public void onStart() {
        super.onStart();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                intent.putExtra("sentToName", arrayList.get(i).getUsername());
                intent.putExtra("sentTo", messageUIDs.get(i));

                startActivity(intent);
            }
        });

    }

}
