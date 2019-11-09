package com.example.bigmood;

import android.app.Activity;
import android.graphics.Point;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test class for LoginActivity and BaseDrawerActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Test if nav drawer opens
     */
    @Test
    public void checkOpenNavDrawer(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.waitForActivity(BaseDrawerActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", BaseDrawerActivity.class);

        Point deviceSize = new Point();
        solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(deviceSize);

        float screenWidth = deviceSize.x;
        float screenHeight = deviceSize.y;
        float fromX = 0;
        float toX = screenWidth / 2;
        float fromY = screenHeight / 2;
        float toY = fromY;

        solo.drag(fromX, toX, fromY, toY,5);
        solo.searchText("Dashboard");
        solo.searchText("Friends");
    }


    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}