package com.rain.currency.ui.intro;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.rain.currency.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class IntroActivityTest {

    @Rule public ActivityTestRule<IntroActivity> mActivityTestRule = new ActivityTestRule<>(IntroActivity.class);

    @Test
    public void introActivityTest() {
        ViewInteraction circleImageView = onView(allOf(withId(R.id.btnMoney), childAtPosition(withClassName(is("android.widget" +
                ".RelativeLayout")), 1), isDisplayed()));
        circleImageView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction circleImageView2 = onView(allOf(withId(R.id.ivBaseIcon), childAtPosition(allOf(withId(R.id.baseContainer),
                childAtPosition(withId(R.id.content), 0)), 0), isDisplayed()));
        circleImageView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction editText = onView(allOf(withId(R.id.edtSearch), childAtPosition(childAtPosition(withId(android.R.id.custom), 0),
                0), isDisplayed()));
        editText.perform(replaceText("sgd"), closeSoftKeyboard());

        ViewInteraction recyclerView = onView(allOf(withId(R.id.rvCurrencies), childAtPosition(withClassName(is("android.widget" +
                ".RelativeLayout")), 2)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction editText2 = onView(allOf(withId(R.id.edtBase), childAtPosition(allOf(withId(R.id.baseContainer), childAtPosition
                (withId(R.id.content), 0)), 2), isDisplayed()));
        editText2.perform(replaceText("20"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(allOf(withId(R.id.edtBase), withText("2"), childAtPosition(allOf(withId(R.id.baseContainer),
                childAtPosition(withId(R.id.content), 0)), 2), isDisplayed()));
        editText3.perform(replaceText("20"));

        ViewInteraction editText4 = onView(allOf(withId(R.id.edtBase), withText("20"), childAtPosition(allOf(withId(R.id.baseContainer),
                childAtPosition(withId(R.id.content), 0)), 2), isDisplayed()));
        editText4.perform(closeSoftKeyboard());

        ViewInteraction editText5 = onView(allOf(withId(R.id.edtTarget), withText("15.25"), childAtPosition(childAtPosition(withId(R.id
                .content), 2), 2), isDisplayed()));
        editText5.check(matches(withText("15.25")));

        ViewInteraction textView = onView(allOf(withId(R.id.tvBaseUnit), withText("SGD"), childAtPosition(allOf(withId(R.id
                .baseContainer), childAtPosition(withId(R.id.content), 0)), 1), isDisplayed()));
        textView.check(matches(withText("SGD")));

        ViewInteraction textView2 = onView(allOf(withId(R.id.tvTargetUnit), withText("USD"), childAtPosition(childAtPosition(withId(R.id
                .content), 2), 1), isDisplayed()));
        textView2.check(matches(withText("USD")));

    }

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt
                        (position));
            }
        };
    }
}
