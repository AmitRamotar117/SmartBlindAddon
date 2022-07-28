package ca.t10.blinddev.it.smartblindaddon;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EspressoTest {

    @Rule
    public ActivityScenarioRule<NewUserActivity> activityRule = new ActivityScenarioRule<NewUserActivity>(NewUserActivity.class);

    @Test
    public void nameTest()
    {
     onView(withHint("Name")).check(matches(isDisplayed()));
    }

    @Test
    public void phoneTest()
    {
        onView(withHint("Phone")).check(matches(isDisplayed()));
    }
    @Test
    public void emailTest()
    {
        onView(withHint("Enter Email")).check(matches(isDisplayed()));
    }
    @Test
    public void passwordTest()
    {
        onView(withHint("Enter Password")).check(matches(isDisplayed()));
    }
    @Test
    public void confirmTest()
    {
        onView(withHint("Confirm Password")).check(matches(isDisplayed()));
    }


}
