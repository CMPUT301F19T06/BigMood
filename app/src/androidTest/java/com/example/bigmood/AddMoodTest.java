//package com.example.bigmood;
//
//import android.app.Activity;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.test.platform.app.InstrumentationRegistry;
//import androidx.test.rule.ActivityTestRule;
//
//import com.google.firebase.Timestamp;
//import com.robotium.solo.Solo;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//
//import java.util.ArrayList;
//
///**
// * Test cases for add / edit mood activity
// */
//public class AddMoodTest {
//    private String userId,username;
//    private Solo solo;
//    /**
//     * Start ActivityAddMood
//     */
//    @Rule
//    public ActivityTestRule<ActivityAddMood> rule = new ActivityTestRule<>(ActivityAddMood.class,true,true);
//
//    @Before
//    public void setUp() throws Exception{
//        solo.clickOnButton(R.);
//        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
//    }
//    /**
//     * Gets the Activity
//     * @throws Exception
//     */
//    @Test
//    public void start() throws Exception{
//        Activity activity = rule.getActivity();
//    }
//
//    /**
//     * Add a mood
//     **/
//    @Test
//    public void checkList() {
//        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
//        solo.assertCurrentActivity("Wrong Activity", ActivityAddMood.class);
//        //Get view for EditText and enter a city name
//        solo.isSpinnerTextSelected(R.array.editmood_moodspinner,"Happy");
//        solo.isSpinnerTextSelected(R.array.editmood_moodsituation_spinner,"in a group");
//
//        solo.clickOnButton("CONFIRM"); //Select CONFIRM Button
//        solo.clearEditText((EditText) solo.getView(R.id.moodDescription)); //Clear the EditText
//        solo.enterText((EditText) solo.getView(R.id.moodDescription), "Living life");
//
//    }
//}
