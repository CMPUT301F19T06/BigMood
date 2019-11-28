package com.example.bigmood;

import android.content.Intent;
import android.graphics.Point;
import android.widget.ImageButton;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static junit.framework.TestCase.assertTrue;

public class UserViewActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<UserViewActivity> rule = new ActivityTestRule<>(UserViewActivity.class, false, false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void checkUsername() throws InterruptedException {
        Intent intent = new Intent(getApplicationContext(), UserViewActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        intent.putExtra("TARGET_ID", "109926616595958819946");
        rule.launchActivity(intent);
        solo.waitForActivity("UserViewActivity", 500);
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
        assertTrue(solo.searchText("Jarrett Yu"));
    }

    @Test
    public void checkOpenNavDrawer(){
        Intent intent = new Intent(getApplicationContext(), UserViewActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        intent.putExtra("TARGET_ID", "109926616595958819946");
        rule.launchActivity(intent);
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);

        Point deviceSize = new Point();
        solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(deviceSize);

        float screenWidth = deviceSize.x;
        float screenHeight = deviceSize.y;
        float fromX = 0;
        float toX = screenWidth / 2;
        float fromY = screenHeight / 2;
        float toY = fromY;

        solo.drag(fromX, toX, fromY, toY,5);
        assertTrue(solo.searchText("Dashboard"));
        assertTrue(solo.searchText("Friends"));
        assertTrue(solo.searchText("Map"));
        assertTrue(solo.searchText("My Profile"));
    }

    @Test
    public void checkRecyclerView() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
        solo.wait(1);
        assertTrue(solo.searchText("Happy"));
    }

    @Test
    public void checkFriend() throws InterruptedException {
        solo.clickOnView(solo.getView(R.id.user_view_add_friend));
        //todo: figure out how to slap in query
        assertTrue(true);
    }
}
