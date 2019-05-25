package com.rain.currency.ui.converter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.rain.currency.R
import com.rain.currency.ui.cleanSharePrefs
import com.rain.currency.ui.ensureOverlayPermission
import com.rain.currency.ui.getMockServerPort
import com.rain.currency.ui.mockLiveCurrency
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
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
    private val webServer = MockWebServer()

    private lateinit var device: UiDevice
    private lateinit var decorView: View

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(ConverterActivity::class.java)

    @Before
    fun setUp() {
        initMockWebServer()
        cleanSharePrefs()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        activityTestRule.activity.run {
            ensureOverlayPermission(this)
            decorView = window.decorView
            startService(intent())
        }
        Thread.sleep(1000)
    }

    private fun initMockWebServer() {
        webServer.run {
            setDispatcher(object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.path.contains("/latest")) {
                        return MockResponse().apply {
                            setBody(mockLiveCurrency())
                        }
                    }

                    return MockResponse()
                }
            })
            start(getMockServerPort())
        }
    }

    private fun intent() = Intent(activityTestRule.activity, ConverterService::class.java)

    @Test
    fun onInit_expandContent() {
        onView(withId(R.id.btnMoney))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.content))
                .inRoot(withDecorView(not(`is`(decorView))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun clickMoneyButton_expandContent() {
        device.pressBack()
        device.pressBack()
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
        cleanSharePrefs()
        webServer.shutdown()
    }
}
