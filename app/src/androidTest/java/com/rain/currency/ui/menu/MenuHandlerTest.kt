package com.rain.currency.ui.menu

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.rain.currency.R
import com.rain.currency.ui.ensureOverlayPermission
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MenuHandlerTest {

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(MenuActivity::class.java)

    @Before
    fun setUp() {
        ensureOverlayPermission(activityTestRule.activity)
    }

    @Test
    fun menuCutAndPaste() {
        onView(allOf(withId(R.id.edtBase), isDisplayed())).perform(replaceText("12"), closeSoftKeyboard())
        onView(allOf(withId(R.id.edtBase), withText("12"), isDisplayed())).perform(longClick())
        onView(allOf(withId(R.id.tvMenu), withText("Cut"), isDisplayed()))
                .inRoot(withDecorView(not(`is`(activityTestRule.activity.window.decorView))))
                .perform(click())
        onView(allOf(withId(R.id.edtBase), isDisplayed())).check(matches(withText("")))

        onView(allOf(withId(R.id.edtTarget), isDisplayed())).perform(longClick())
        onView(allOf(withId(R.id.tvMenu), withText("Paste"), isDisplayed()))
                .inRoot(withDecorView(not(`is`(activityTestRule.activity.window.decorView))))
                .perform(click())
        onView(allOf(withId(R.id.edtTarget), isDisplayed())).check(matches(withText("12")))
    }

    @Test
    fun menuCopyAndPaste() {
        onView(allOf(withId(R.id.edtBase), isDisplayed())).perform(replaceText("12"), closeSoftKeyboard())
        onView(allOf(withId(R.id.edtBase), withText("12"), isDisplayed())).perform(longClick())
        onView(allOf(withId(R.id.tvMenu), withText("Copy"), isDisplayed()))
                .inRoot(withDecorView(not(`is`(activityTestRule.activity.window.decorView))))
                .perform(click())
        onView(allOf(withId(R.id.edtBase), isDisplayed())).check(matches(withText("12")))

        onView(allOf(withId(R.id.edtTarget), isDisplayed())).perform(longClick())
        onView(allOf(withId(R.id.tvMenu), withText("Paste")))
                .inRoot(withDecorView(not(`is`(activityTestRule.activity.window.decorView))))
                .perform(click())
        onView(allOf(withId(R.id.edtTarget), isDisplayed())).check(matches(withText("12")))
    }

    @Test
    fun menuClear() {
        onView(allOf(withId(R.id.edtBase), isDisplayed())).perform(replaceText("12"), closeSoftKeyboard())
        onView(allOf(withId(R.id.edtBase), withText("12"), isDisplayed())).perform(longClick())
        onView(allOf(withId(R.id.tvMenu), withText("Clear"), isDisplayed()))
                .inRoot(withDecorView(not(`is`(activityTestRule.activity.window.decorView))))
                .perform(click())
        onView(allOf(withId(R.id.edtBase), isDisplayed())).check(matches(withText("")))
    }
}
