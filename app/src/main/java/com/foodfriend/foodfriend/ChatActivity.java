package com.foodfriend.foodfriend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private EditText editMessage;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editMessage = findViewById(R.id.sendMessage);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("messages");
    }

    public void buttonClicked(View view) {

        //get text from edit text
        final String message = editMessage.getText().toString().trim();

        //if exit text is not empty
        if(!TextUtils.isEmpty(message)) {


            final DatabaseReference ref = mDatabase.push();

            //send message to server
            ref.child("message").setValue(message);
        }
    }
}

