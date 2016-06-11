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
import static org.hamcrest.CoreMatchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditTaskTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        DataTestHelper.clearData(mActivityTestRule.getActivity().getApplication());
    }

    @Test
    public void editTaskTest() {
        ViewInteraction floatingActionButton = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.create_fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatImageButton = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.icon_button), withContentDescription("Set Icon"), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction recyclerView = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.icon_recycler),
                withParent(withId(com.keithsmyth.cutlery.R.id.bottom_sheet)),
                isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatEditText = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.name_edit), isDisplayed()));
        appCompatEditText.perform(replaceText("FACE"));

        ViewInteraction appCompatEditText2 = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.frequency_value_edit), isDisplayed()));
        appCompatEditText2.perform(replaceText("1"));

        ViewInteraction appCompatButton = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.save_button), withText("Save"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction frameLayout = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.click_view), isDisplayed()));
        frameLayout.perform(click());

        ViewInteraction appCompatEditText3 = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.name_edit), withText("FACE"), isDisplayed()));
        appCompatEditText3.perform(replaceText("Changed"));

        ViewInteraction appCompatButton2 = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.save_button), withText("Save"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.name_text), withText("Changed"), isDisplayed()));
        textView.check(matches(withText("Changed")));

        ViewInteraction textView2 = onView(
            allOf(withId(com.keithsmyth.cutlery.R.id.due_undo_text), withText("Due"), isDisplayed()));
        textView2.check(matches(withText("Due")));

    }
}
