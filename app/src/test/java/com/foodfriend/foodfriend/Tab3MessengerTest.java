package com.foodfriend.foodfriend;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dan on 22/04/2018.
 */
public class Tab3MessengerTest {

    @Test
    public void splitStringTest() throws Exception
    {

        String inputString = "cc5VpIaO5QRsuSvZSCARzqPLZzp2 66uhmIhPCVZYhIKuXZ1a8DLAlr13";
        int inputNum = 29;

        String[] expected = {"cc5VpIaO5QRsuSvZSCARzqPLZzp2","66uhmIhPCVZYhIKuXZ1a8DLAlr13"};
        String[] output;

        Tab3Messenger tab3Messenger = new Tab3Messenger();

        output = tab3Messenger.splitByNumber(inputString, inputNum);

        assertArrayEquals(expected, output);


    }

}