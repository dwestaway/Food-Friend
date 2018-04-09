package com.foodfriend.foodfriend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.foodfriend.foodfriend.AccountActivity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {

    private EditText editMessage;

    private DatabaseReference mDatabase;
    private DatabaseReference databaseUsers;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private RecyclerView messageList;

    public String sentTo;
    public String sentToName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Recieve recipient of message
        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            sentTo = extras.getString("sentTo");

            sentToName = extras.getString("sentToName");

            Toast.makeText(getApplicationContext(), sentToName + " " + sentTo, Toast.LENGTH_SHORT).show();
        }

        editMessage = findViewById(R.id.sendMessage);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("messages"); //get instance of the messages in the database
        auth = FirebaseAuth.getInstance();

        messageList = (RecyclerView) findViewById(R.id.messageRecieved);

        messageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); //start displaying messages from the bottom

        messageList.setLayoutManager(linearLayoutManager); //set the layout manager for the message list

        //check if current user is null
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null)
                {
                    startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(authStateListener);

        //use firebase's RecyclerAdapter, parameters: Message class, message layout, message view holder and database reference
        FirebaseRecyclerAdapter <Message,MessageViewHolder> firebaseRec = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class, R.layout.message, MessageViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {

                //populate the textviews with database data
                viewHolder.setContent(model.getContent());
                viewHolder.setUsername(model.getSentToName());
                viewHolder.setTime(model.getTime());
            }
        };
        messageList.setAdapter(firebaseRec);
    }

    public void buttonClicked(View view) {

        currentUser = auth.getCurrentUser();

        databaseUsers = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()); //get the userid of the current user

        //get text from edit text
        final String message = editMessage.getText().toString().trim();

        //if exit text is not empty
        if(!TextUtils.isEmpty(message)) {

            final DatabaseReference ref = mDatabase.push();

            databaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //send message to server
                    ref.child("content").setValue(message);
                    //set message name, get it from account name in users data
                    ref.child("sentFromName").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                    //get current time
                    DateFormat df = new SimpleDateFormat("hh:mm a dd.MM.yyyy ");
                    String currentTime = df.format(Calendar.getInstance().getTime());

                    //send the time of message to server
                    ref.child("time").setValue(currentTime);

                    //save the uid of the current user who sent the message
                    ref.child("sentFrom").setValue(currentUser.getUid());

                    //sent to recipients uid
                    ref.child("sentTo").setValue(sentTo);
                    ref.child("sentToName").setValue(sentToName);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //allows the user to easily scroll by using the messagelist item count
            messageList.scrollToPosition(messageList.getAdapter().getItemCount());

        }

        //clear message text box when message is sent
        editMessage.getText().clear();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        View view;

        public MessageViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setContent(String content) {
            TextView chatMessage = view.findViewById(R.id.messageText); //get reference to users message from message.xml
            chatMessage.setText(content);
        }
        public void setUsername(String sentFromName) {
            TextView nameMessage = view.findViewById(R.id.nameText); //get reference to users name text
            nameMessage.setText(sentFromName);
        }
        public void setTime(String time) {
            TextView timeMessage = view.findViewById(R.id.timeText);
            timeMessage.setText(time);
        }

    }
}

