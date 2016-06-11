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
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateTaskTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        DataTestHelper.clearData(mActivityTestRule.getActivity().getApplication());
    }

    @Test
    public void createTaskTest() {
        ViewInteraction floatingActionButton = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.create_fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.name_edit), isDisplayed()));
        appCompatEditText.perform(replaceText("Single Day"));

        ViewInteraction appCompatEditText2 = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.frequency_value_edit), isDisplayed()));
        appCompatEditText2.perform(replaceText("1"));

        ViewInteraction appCompatImageButton = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.icon_button), withContentDescription("Set Icon"), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction recyclerView = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.icon_recycler),
                withParent(withId(com.keithsmyth.cutlery.R.id.bottom_sheet)),
                isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(1, click()));

        ViewInteraction appCompatButton = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.save_button), withText("Save"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.name_text), withText("Single Day"), isDisplayed()));
        textView.check(matches(withText("Single Day")));

        ViewInteraction textView2 = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.due_undo_text), withText("Due"), isDisplayed()));
        textView2.check(matches(withText("Due")));

    }
}
