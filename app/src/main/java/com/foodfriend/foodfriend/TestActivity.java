package com.foodfriend.foodfriend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Dan on 16/05/2018.
 */


public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //LinearLayout view = new LinearLayout(this);
        //view.setId(1);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.test_layout);

        setContentView(frameLayout);
    }
}
