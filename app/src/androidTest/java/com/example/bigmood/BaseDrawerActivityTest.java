package com.example.bigmood;

import android.content.Intent;
import android.graphics.Point;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

/**
 * Test class for BaseDrawerActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class BaseDrawerActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<BaseDrawerActivity> rule = new ActivityTestRule<>(BaseDrawerActivity.class, false, false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Test if nav drawer opens
     */
    @Test
    public void checkNavBar(){
        Intent intent = new Intent(getApplicationContext(), BaseDrawerActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        intent.putExtra("User_Name","Jarrett Yu");
        rule.launchActivity(intent);

        solo.waitForActivity(BaseDrawerActivity.class, 2000);
        solo.waitForActivity(DashboardActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);

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
        solo.searchText("My Profile");
        solo.searchText("Friends");
        solo.searchText("Maps");
        solo.searchText("Jarrett Yu");
    }

    /**
     * Test if search button goes to SearchUserActivity
     */
    @Test
    public void checkSearchButton(){
        Intent intent = new Intent(getApplicationContext(), BaseDrawerActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        intent.putExtra("User_Name", "Jarrett Yu");
        rule.launchActivity(intent);
        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);

        solo.clickOnView(solo.getView(R.id.action_search));
        solo.assertCurrentActivity("Wrong Activity", SearchUserActivity.class);

        assertTrue(solo.waitForText("Search User"));
    }

    /**
     * Test if nav drawer opens to My Profile
     */
    @Test
    public void checkMyProfile(){
        Intent intent = new Intent(getApplicationContext(), BaseDrawerActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        intent.putExtra("User_Name","Jarrett Yu");
        rule.launchActivity(intent);

        solo.waitForActivity(BaseDrawerActivity.class, 2000);
        solo.waitForActivity(DashboardActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);

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
        solo.searchText("My Profile");

        solo.clickOnText("My Profile");
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
    }

    /**
     * Test if nav drawer opens to Friends
     */
    @Test
    public void checkFriends(){
        Intent intent = new Intent(getApplicationContext(), BaseDrawerActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        intent.putExtra("User_Name","Jarrett Yu");
        rule.launchActivity(intent);

        solo.waitForActivity(BaseDrawerActivity.class, 2000);
        solo.waitForActivity(DashboardActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);

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
        solo.searchText("My Profile");

        solo.clickOnText("Friends");
        solo.assertCurrentActivity("Wrong Activity", FriendsActivity.class);
    }

    /**
     * Test if nav drawer opens to Maps
     */
    @Test
    public void checkMaps(){
        Intent intent = new Intent(getApplicationContext(), BaseDrawerActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        intent.putExtra("User_Name","Jarrett Yu");
        rule.launchActivity(intent);

        solo.waitForActivity(BaseDrawerActivity.class, 2000);
        solo.waitForActivity(DashboardActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);

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
        solo.searchText("My Profile");

        solo.clickOnText("Map");
        solo.assertCurrentActivity("Wrong Activity", GpsActivity.class);
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
