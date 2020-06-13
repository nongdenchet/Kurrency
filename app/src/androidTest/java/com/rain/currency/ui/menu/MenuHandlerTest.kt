package com.rain.currency.ui.menu

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.rain.currency.R
import com.rain.currency.ui.ensureOverlayPermission
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@LargeTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
        onView(allOf(withId(R.id.edtBase), isDisplayed())).perform(
            replaceText("12"),
            closeSoftKeyboard()
        )
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
        onView(allOf(withId(R.id.edtBase), isDisplayed())).perform(
            replaceText("12"),
            closeSoftKeyboard()
        )
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
        onView(allOf(withId(R.id.edtBase), isDisplayed())).perform(
            replaceText("12"),
            closeSoftKeyboard()
        )
        onView(allOf(withId(R.id.edtBase), withText("12"), isDisplayed())).perform(longClick())
        onView(allOf(withId(R.id.tvMenu), withText("Clear"), isDisplayed()))
            .inRoot(withDecorView(not(`is`(activityTestRule.activity.window.decorView))))
            .perform(click())
        onView(allOf(withId(R.id.edtBase), isDisplayed())).check(matches(withText("")))
    }
}
