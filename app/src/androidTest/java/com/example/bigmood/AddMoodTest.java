package com.example.bigmood;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static junit.framework.TestCase.assertTrue;

/**
 * Test cases for add / edit mood activity
 */
public class AddMoodTest {
    private String userId,username;
    private Solo solo;
    /**
     * Start ActivityAddMood
     */
    @Rule
    public ActivityTestRule<ActivityAddMood> rule = new ActivityTestRule<>(ActivityAddMood.class,false,false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    public Mood mockMood(){
        return new Mood("Disgusted","Sad lief","#87C000",Timestamp.now(),"404");
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void checkAddMood() throws Exception{
        Intent intent = new Intent(getApplicationContext(), ActivityAddMood.class);
        intent.putExtra("USER_ID", "404");
        intent.putExtra("EDIT","AddingMode");
        String date = Timestamp.now().toDate().toString();
        intent.putExtra("DATE", date);
        Mood mood = mockMood();
        mood.setMoodUsername("Donald Trump");
        intent.putExtra("Mood",new Mood("404"));
        rule.launchActivity(intent);
        solo.waitForActivity(ActivityAddMood.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", ActivityAddMood.class);

    }

    @Test
    public void testMoodView() {
        addMood("Happy");
        solo.clickOnText("My Profile");
        solo.waitForActivity("UserViewActivity", 1000);
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
        assertTrue(solo.searchText("Happy"));
        solo.clickOnText("Happy");
        solo.waitForActivity("ActivityMoodView");
        solo.assertCurrentActivity("Wrong Activity", ActivityMoodView.class);
        deleteMood("Happy");
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
        solo.waitForActivity("ActivityAddMood", 500);
        solo.assertCurrentActivity("Wrong Activity", ActivityAddMood.class);
        solo.waitForActivity("ActivityAddMood", 1000);
        solo.clickOnMenuItem("Disgusted");
        solo.clickOnMenuItem("Alone");
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
    /**
     * Add a mood
     **/
    @Test
    public void checkList() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", ActivityAddMood.class);
        //Get view for EditText and enter a city name
        solo.isSpinnerTextSelected(R.array.editmood_moodspinner,"Happy");
        solo.isSpinnerTextSelected(R.array.editmood_moodsituation_spinner,"in a group");

        solo.clickOnButton("SAVE"); //Select CONFIRM Button
        solo.waitForActivity("ActivityViewMood", 1000);
        solo.assertCurrentActivity("Wrong Activity", ActivityAddMood.class);

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
