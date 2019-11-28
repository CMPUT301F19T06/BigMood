package com.example.bigmood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertTrue;

/**
 * Test class for LoginActivity and BaseDrawerActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class SearchUserActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SearchUserActivity> rule = new ActivityTestRule<>(SearchUserActivity.class, false, false);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }


//    /**
//     * Test if search button goes to SearchUserActivity
//     */
//    @Test
//    public void checkSearchButton(){
//        Intent intent = new Intent(getApplicationContext(), BaseDrawerActivity.class);
//        intent.putExtra("USER_ID", "109926616595958819946");
//        intent.putExtra("User_Name", "Jarrett Yu");
//        rule.launchActivity(intent);
//        solo.assertCurrentActivity("Wrong Activity", DashboardActivity.class);
//
//        solo.clickOnView(solo.getView(R.id.action_search));
//        solo.assertCurrentActivity("Wrong Activity", SearchUserActivity.class);
//    }

    /**
     * Test if search button goes to SearchUserActivity
     */
    @Test
    public void checkSearch(){
        Intent intent = new Intent(getApplicationContext(), SearchUserActivity.class);
        intent.putExtra("USER_ID", "109926616595958819946");
        rule.launchActivity(intent);

        solo.assertCurrentActivity("Wrong Activity", SearchUserActivity.class);

        solo.enterText((EditText)solo.getView(R.id.search_field), "Joshua");
        solo.clickOnView(solo.getView(R.id.search_btn));
        solo.clearEditText((EditText) solo.getView(R.id.search_field));

        assertTrue(solo.waitForText("Joshua Derkson"));

        solo.clickInRecyclerView(0);
        solo.assertCurrentActivity("Wrong Activity", UserViewActivity.class);
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
