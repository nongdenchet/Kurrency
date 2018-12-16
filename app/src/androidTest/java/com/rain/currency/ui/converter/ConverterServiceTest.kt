package com.rain.currency.ui.converter

import android.content.Intent
import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import com.rain.currency.EspressoApp
import com.rain.currency.R
import com.rain.currency.ui.ensureOverlayPermission
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@LargeTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ConverterServiceTest {
    private lateinit var device: UiDevice
    private lateinit var decorView: View
    private lateinit var sharedPreferences: SharedPreferences

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(ConverterActivity::class.java)

    @Before
    fun setUp() {
        sharedPreferences = (activityTestRule.activity.application as EspressoApp)
                .component
                .getSharePreferences()
        sharedPreferences.edit()
                .clear()
                .apply()

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        ensureOverlayPermission(activityTestRule.activity)
        decorView = activityTestRule.activity.window.decorView
        activityTestRule.activity.startService(intent())
    }

    private fun intent() = Intent(activityTestRule.activity, ConverterService::class.java)

    @Test
    fun onInit_collapseContent() {
        onView(withId(R.id.btnMoney))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.content))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun clickMoneyButton_expandContent() {
        onView(allOf(withId(R.id.btnMoney), isDisplayed()))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(click())
        onView(withId(R.id.content))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.btnMoney))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun pressBack_collapseContent() {
        onView(allOf(withId(R.id.btnMoney), isDisplayed()))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(click())
        onView(allOf(withId(R.id.edtBase)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(replaceText("20"), closeSoftKeyboard())
        device.pressBack()

        onView(withId(R.id.content))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.btnMoney))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun shouldConvertCorrectly() {
        onView(allOf(withId(R.id.btnMoney), isDisplayed()))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(click())
        onView(allOf(withId(R.id.ivBaseIcon)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(click())
        onView(allOf(withId(R.id.edtSearch)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(replaceText("sgd"), closeSoftKeyboard())
        onView(allOf(withId(R.id.rvCurrencies)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(allOf(withId(R.id.edtBase)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(replaceText("20"))
        onView(allOf(withId(R.id.edtBase)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .perform(closeSoftKeyboard())

        onView(allOf(withId(R.id.tvBaseUnit)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withText("SGD")))
        onView(allOf(withId(R.id.tvTargetUnit)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withText("USD")))
        Thread.sleep(1000)
        onView(allOf(withId(R.id.edtTarget)))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withText("10")))
    }

    @After
    fun tearDown() {
        activityTestRule.activity.stopService(intent())
        sharedPreferences.edit()
                .clear()
                .apply()
    }
}
