package com.keithsmyth.cutlery.view;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.keithsmyth.cutlery.data.DataTestHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateTaskMissingDataTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        DataTestHelper.clearData(mActivityTestRule.getActivity().getApplication());
    }

    @Test
    public void createTaskMissingDataTest() {
        ViewInteraction floatingActionButton = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.create_fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatButton = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.save_button), withText("Save"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction button = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.save_button), withText("Save"), isDisplayed()));
        button.check(matches(isDisplayed()));

    }
}
