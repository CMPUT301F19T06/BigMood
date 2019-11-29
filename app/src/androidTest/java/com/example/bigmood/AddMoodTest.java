package com.example.bigmood;

import android.content.Intent;
import android.graphics.Point;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static junit.framework.TestCase.assertTrue;

/**
 *  Tests add and view mood activities
 */
public class AddMoodTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<DashboardActivity> rule = new ActivityTestRule<>(DashboardActivity.class, false, false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void basicAddViewTest() {
        startDashboard();
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity("ActivityAddMood", 500);
        solo.assertCurrentActivity("Wrong Activity", ActivityAddMood.class);
        solo.clickOnText("SAVE");
        solo.assertCurrentActivity("Wrong Activity", ActivityMoodView.class);
        assertTrue(solo.searchText("Happy"));
        solo.goBack();
        solo.waitForActivity("DashboardActivity");
        deleteMood("Happy");
    }

    @Test
    public void testSpinners() {
        String[] moods = {"Sad", "Happy", "Angry", "Scared", "Disgusted", "Bored"};
        String[] situations = {"alone", "with someone", "with a few others"};
        startDashboard();
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity("ActivityAddMood", 500);
        solo.clickOnText("Happy");
        for (String string : moods) {
            solo.clickOnText(string);
            assertTrue(solo.searchText(string));
            solo.clickOnText(string);
        }
        solo.clickOnText("Touched");
        assertTrue(solo.searchText("Touched"));

        solo.clickOnText("in a crowd");
        for (String string : situations) {
            solo.clickOnText(string);
            assertTrue(solo.searchText(string));
            solo.clickOnText(string);
        }
        solo.clickOnText("in a crowd");
        assertTrue(solo.searchText("in a crowd"));
    }

    @Test
    public void addMoodTest2() {
        startDashboard();
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity("ActivityAddMood", 1000);
        solo.sleep(1000);
        solo.clickOnText("Happy");
        solo.sleep(1000);
        solo.clickOnText("Sad");
        assertTrue(solo.searchText("Sad"));

        solo.clickOnText("in a crowd");
        solo.sleep(500);
        solo.clickOnText("alone");
        assertTrue(solo.searchText("alone"));

        solo.enterText((EditText)solo.getView(R.id.moodDescription_edit), "Description is way too long");
        solo.clickOnText("SAVE");
        solo.sleep(500);
        solo.assertCurrentActivity("Wrong activity", ActivityAddMood.class);

        solo.clearEditText((EditText)solo.getView(R.id.moodDescription_edit));
        solo.typeText((EditText)solo.getView(R.id.moodDescription_edit), "Too many words test");
        solo.clickOnText("SAVE");
        solo.sleep(500);
        solo.assertCurrentActivity("Wrong activity", ActivityAddMood.class);

        solo.clearEditText((EditText)solo.getView(R.id.moodDescription_edit));
        solo.typeText((EditText)solo.getView(R.id.moodDescription_edit), "Just right");
        solo.clickOnText("SAVE");
        solo.waitForActivity("ActivityMoodView", 500);
        solo.assertCurrentActivity("Wrong activity", ActivityMoodView.class);

        solo.goBack();
        solo.waitForActivity("DashboardActivity", 500);
        deleteMood("Sad");
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
        intent.putExtra("USER_ID", "404");
        intent.putExtra("User_Name","Donald Trump");
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

    @Test
    public void checkFriend() throws InterruptedException {
        solo.clickOnView(solo.getView(R.id.user_view_add_friend));
        assertTrue(solo.searchText("Sent follow request!"));
    }
}
