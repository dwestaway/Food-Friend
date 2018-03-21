package com.foodfriend.foodfriend;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class TabbedActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;


    public static ViewPager mViewPager;
    private FirebaseAuth auth;

    private final static int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        auth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG) //floating button in bottom right
                        .setAction("Action", null).show();
            }
        });

    }


    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.menu_tabbed, menu);
    //    return true;
    //}

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    //    int id = item.getItemId();

        //noinspection SimplifiableIfStatement
    //    if (id == R.id.action_settings) {
    //        return true;
    //    }

    //    return super.onOptionsItemSelected(item);
    //}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_tabbed,menu);

        return super.onCreateOptionsMenu(menu);
    }

    //Drop down options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //If signout button is clicked
        if(item.getItemId() == R.id.sign_out)
        {
            auth.signOut();
            finish();
        }
        //If change profile image button is clicked
        if(item.getItemId() == R.id.changeImage)
        {
            //get intent for the gallery
            Intent galleryIntent = new Intent();

            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*"); //set gallery to only be images

            startActivityForResult(galleryIntent, GalleryPick); //Open gallery

            Toast.makeText(getApplicationContext(), "Choose your image", Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();

            //Launch crop activity
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
        }
    }

    //Code for tabs using Pager Adapter, allows user to click between tabs
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Tab1Profile tab1 = new Tab1Profile();
                    return tab1;
                case 1:
                    Tab2Matches tab2 = new Tab2Matches();
                    return tab2;
                case 2:
                    Tab3Messenger tab3 = new Tab3Messenger();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }
}
