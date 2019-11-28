package com.example.bigmood;

import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import android.widget.ImageButton;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.Dash;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class UserViewActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<DashboardActivity> rule = new ActivityTestRule<>(DashboardActivity.class, false, false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void checkUsername() throws InterruptedException {
        startDashboard();
        slideOutDrawer();
        solo.clickOnText("My Profile");
        solo.waitForActivity("UserViewActivity", 500);
        assertTrue(solo.searchText("Jarrett Yu"));
    }

    @Test
    public void checkOpenNavDrawer(){
        startDashboard();
        slideOutDrawer();
        solo.clickOnText("My Profile");
        solo.waitForActivity("UserViewActivity", 500);
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
        slideOutDrawer();
        assertTrue(solo.searchText("Dashboard"));
        assertTrue(solo.searchText("Friends"));
        assertTrue(solo.searchText("Map"));
        assertTrue(solo.searchText("My Profile"));
    }

    @Test
    public void checkRecyclerView() throws InterruptedException {
        startDashboard();
        addMood("Happy");
        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);
        slideOutDrawer();
        solo.clickOnText("My Profile");
        solo.waitForActivity("UserViewActivity", 1000);
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
        assertTrue(solo.searchText("Happy"));
        deleteMood("Happy");
    }

    @Test
    public void testFilter() {
        startDashboard();
        addMood("Disgusted");
        addMood("Sad");

        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);
        slideOutDrawer();
        solo.clickOnText("My Profile");
        solo.waitForActivity("UserViewActivity", 1000);
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
        assertTrue(solo.searchText("Sad"));
        assertTrue(solo.searchText("Disgust"));

        solo.clickOnText("Filter by...");
        solo.clickOnText("Sad");
        solo.clickOnText("Submit");

        assertTrue(solo.searchText("Disgusted"));

        solo.clickOnText("Filter by...");
        solo.clickOnText("Sad");
        solo.clickOnText("Submit");

        deleteMood("Sad");
        deleteMood("Disgusted");
    }

    @Test
    public void testSort() {
        // TODO: implement
    }

    @Test
    public void testMoodView() {
        startDashboard();
        addMood("Happy");
        slideOutDrawer();
        solo.clickOnText("My Profile");
        solo.waitForActivity("UserViewActivity", 1000);
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
        assertTrue(solo.searchText("Happy"));
        solo.clickOnText("Happy");
        solo.waitForActivity("ActivityMoodView");
        solo.assertCurrentActivity("Wrong Activity", ActivityMoodView.class);
        deleteMood("Happy");
    }

    public void slideOutDrawer() {
        Point deviceSize = new Point();
        solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(deviceSize);

        float screenWidth = deviceSize.x;
        float screenHeight = deviceSize.y;
        float fromX = 0;
        float toX = screenWidth / 2;
        float fromY = screenHeight / 2;
        float toY = fromY;

        solo.drag(fromX, toX, fromY, toY,5);
    }

    public void startDashboard() {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        intent.putExtra("User_Name","Jarrett Yu");
        rule.launchActivity(intent);
        solo.waitForActivity("DashboardActivity", 500);
        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);
    }

    public void addMood(String title) {
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity("ActivityAddMood", 1000);
        solo.clickOnText("Happy");
        solo.clickOnText(title);
        solo.clickOnButton("SAVE");
        solo.waitForActivity("ActivityViewMood", 1000);
        solo.goBack();
        solo.waitForActivity("DashboardActivity", 1000);
    }

    public void deleteMood(String title) {
        solo.clickOnText(title);
        solo.waitForActivity("ActivityViewMood", 1000);
        solo.clickOnText("EDIT");
        solo.waitForActivity("ActivityAddMood", 1000);
        solo.clickOnView(solo.getView(R.id.deleteMood));
    }
}
