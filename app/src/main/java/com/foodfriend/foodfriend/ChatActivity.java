package com.foodfriend.foodfriend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private EditText editMessage;

    private DatabaseReference mDatabase;

    private RecyclerView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editMessage = findViewById(R.id.sendMessage);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("messages");

        messageList = (RecyclerView) findViewById(R.id.messageRecieved);

        messageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); //start displaying messages from the bottom

        messageList.setLayoutManager(linearLayoutManager); //set the layout manager for the message list

    }

    @Override
    protected void onStart() {
        super.onStart();

        //use firebase's RecyclerAdapter, parameters: Message class, message layout, message view holder and database reference
        FirebaseRecyclerAdapter <Message,MessageViewHolder> firebaseRec = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,R.layout.message,MessageViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.setContent(model.getContent());
            }
        };
        messageList.setAdapter(firebaseRec);
    }

    public void buttonClicked(View view) {

        //get text from edit text
        final String message = editMessage.getText().toString().trim();

        //if exit text is not empty
        if(!TextUtils.isEmpty(message)) {


            final DatabaseReference ref = mDatabase.push();

            //send message to server
            ref.child("content").setValue(message);
        }
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
    }
}

