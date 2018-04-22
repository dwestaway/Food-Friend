package com.foodfriend.foodfriend.AccountActivity;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.foodfriend.foodfriend.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dan on 22/04/2018.
 */
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity activity = null;

    @Before
    public void setUp() throws Exception
    {
        activity = activityTestRule.getActivity();
    }

    @Test
    public void activityLaunchTest()
    {
        View view = activity.findViewById(R.id.buttonLogin);

        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception
    {
        activity = null;
    }

}