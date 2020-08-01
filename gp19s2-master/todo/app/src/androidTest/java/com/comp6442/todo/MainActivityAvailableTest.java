package com.comp6442.todo;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityAvailableTest {


    public static final String STRING_TO_BE_TYPED = "Reminders";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes.
     */
    @Rule public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void check_addButton_MainActivity() {
        // check pressing the add button on mainActivity
        onView(withId(R.id.addTodoItemButton)).perform(click());
    }


    @Test
    public void check_locationButton_MainActivity() {
        // check pressing the location button on mainActivity
        onView(withId(R.id.showLocationButton)).perform(click());

    }
    @Test
    public void check_reminderText_MainActivity() {
        // check the text view content
        onView(withId(R.id.remindersTitleLabel)).check(matches(withText(STRING_TO_BE_TYPED)));
    }

}
