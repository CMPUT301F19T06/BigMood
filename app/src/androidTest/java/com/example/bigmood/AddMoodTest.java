package com.example.bigmood;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        startAdd();
        solo.clickOnButton("SAVE");
        solo.waitForActivity(ActivityMoodView.class,2000);
        solo.assertCurrentActivity("Wrong Activity", ActivityAddMood.class);
    }

    @Test
    public void testMoodDescription(){
        Intent intent = new Intent(getApplicationContext(), ActivityAddMood.class);
        intent.putExtra("USER_ID", "404");
        intent.putExtra("EDIT","AddingMode");
        String date = Timestamp.now().toDate().toString();
        intent.putExtra("DATE", date);
        Mood mood = mockMood();
        mood.setMoodUsername("Donald Trump");
        intent.putExtra("Mood",new Mood("404"));
        rule.launchActivity(intent);
        solo.enterText((EditText)solo.getView(R.id.moodDescription), "Hi there");
        solo.clickOnView(solo.getView(R.id.save_button));
        solo.waitForActivity(ActivityMoodView.class, 2000);
        solo.clickOnButton("EDIT");
        solo.waitForActivity(ActivityAddMood.class, 2000);

        solo.enterText((EditText)solo.getView(R.id.moodDescription), "This is more than 20 characters");
        solo.clickOnButton("SAVE");
        solo.assertCurrentActivity("Right activity",ActivityAddMood.class);
        solo.enterText((EditText)solo.getView(R.id.moodDescription), "This is four words");
        solo.clickOnButton("SAVE");
        solo.assertCurrentActivity("Right activity",ActivityAddMood.class);
        solo.enterText((EditText)solo.getView(R.id.moodDescription), "This is fine");
        solo.clickOnButton("SAVE");
        solo.waitForActivity(ActivityMoodView.class, 2000);
        solo.assertCurrentActivity("Right activity",ActivityMoodView.class);
    }

    @Test
    public void testSpinners() {
        String[] moods = {"Sad", "Happy", "Angry", "Scared", "Disgusted", "Bored"};
        String[] situations = {"alone", "with someone", "with a few others"};
        startAdd();
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

    /**
     * Add a mood
     **/
    @Test
    public void editTest() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        //Get view for EditText and enter a city name
        // 0 is the first spinner in the layout
        View view1 = solo.getView(Spinner.class, 0);
        solo.clickOnView(view1);
        solo.scrollToTop(); // I put this in here so that it always keeps the list at start
        // select the 10th item in the spinner
        solo.clickOnView(solo.getView(TextView.class, 2));

        solo.clickOnButton("SAVE"); //Select CONFIRM Button
        solo.waitForActivity("ActivityViewMood", 1000);

        solo.assertCurrentActivity("Wrong Activity", ActivityAddMood.class);

    }

    public void startAdd() {
        Intent intent = new Intent(getApplicationContext(), ActivityAddMood.class);
        intent.putExtra("USER_ID", "404");
        intent.putExtra("EDIT","AddingMode");
        String date = Timestamp.now().toDate().toString();
        String id = String.valueOf(Timestamp.now().hashCode());
        Mood mood = new Mood("404");
        mood.setMoodUsername("Donald Trump");
        mood.setMoodID(id);
        intent.putExtra("DATE", date);
        intent.putExtra("Mood", mood);
        rule.launchActivity(intent);
        solo.waitForActivity(ActivityAddMood.class, 2000);

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
