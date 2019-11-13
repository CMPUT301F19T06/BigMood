package com.example.bigmood;

import android.graphics.Point;
import android.os.Bundle;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class UserViewActivityTest {
    private Solo solo;

    public class MockUserViewActivity extends UserViewActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getLayoutInflater().inflate(R.layout.activity_view_user, frameLayout);
            toolbar.setTitle("UserView");

            this.userId = "109926616595958819946";
            this.hasViewPermission = true;

            this.userName = findViewById(R.id.user_view_username);
            this.recyclerView = findViewById(R.id.dashboard_recyclerview);
            this.getUserName();
            if(hasViewPermission) {
                this.setMoodListener();
                this.initRecyclerView();
            }
        }
    }

    @Rule
    public ActivityTestRule<MockUserViewActivity> rule = new ActivityTestRule<>(MockUserViewActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void checkUsername() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", MockUserViewActivity.class);
        solo.wait(1);
        assertTrue(solo.searchText("Jarrett Yu"));
    }

    @Test
    public void checkOpenNavDrawer(){
        solo.assertCurrentActivity("Wrong Activity", MockUserViewActivity.class);

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

    @Test
    public void checkRecyclerView() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", MockUserViewActivity.class);
        solo.wait(1);
        assertTrue(solo.searchText("New Mood"));
    }
}
