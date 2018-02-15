package com.rain.currency.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rain.currency.R
import com.rain.currency.support.OverlayService
import com.rain.currency.utils.getOverlayType
import com.rain.currency.utils.getScreenSize
import com.rain.currency.utils.getStreamSelection
import com.rain.currency.utils.getStreamText
import com.rain.currency.utils.setMoney
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject


class ConverterService : OverlayService() {
    private val DELAY_UNTIL_GAIN_FOCUS = 100L
    private val disposables = CompositeDisposable()
    private val handler = Handler()
    private lateinit var inputManager: InputMethodManager
    private lateinit var removeBar: FrameLayout

    @BindView(R.id.spBase)
    lateinit var spBase: Spinner
    @BindView(R.id.spTarget)
    lateinit var spTarget: Spinner
    @BindView(R.id.pbLoading)
    lateinit var pbLoading: ProgressBar
    @BindView(R.id.edtBase)
    lateinit var edtBase: EditText
    @BindView(R.id.edtTarget)
    lateinit var edtTarget: EditText
    @BindView(R.id.content)
    lateinit var content: ViewGroup
    @BindView(R.id.btnMoney)
    lateinit var btnMoney: ImageView

    @Inject
    lateinit var viewModel: ConverterViewModel

    private val removeBarLayoutParams: WindowManager.LayoutParams by lazy {
        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.RGBA_8888
        params.type = getOverlayType()
        params.gravity = Gravity.TOP or Gravity.START
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.width = removeBar.layoutParams.width
        params.height = removeBar.layoutParams.height
        params.y = removeBarY()
        params
    }

    private val screenSize: DisplayMetrics by lazy {
        getScreenSize(windowManager)
    }

    private val removeBarHeight: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.remove_bar_height)
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        ButterKnife.bind(this, container)
        setUpView()
        bindViewModel()
    }

    private fun setUpView() {
        setUpMoneyButton()
        setUpRemoveBar()
    }

    @SuppressLint("InflateParams")
    private fun setUpRemoveBar() {
        removeBar = LayoutInflater.from(this).inflate(R.layout.remove_bar, null) as FrameLayout
        removeBar.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, removeBarHeight)
    }

    private fun removeBarY() = screenSize.heightPixels - removeBarHeight

    override fun onDragStarted(x: Float, y: Float) {
        windowManager.addView(removeBar, removeBarLayoutParams)
    }

    override fun onDragEnded(x: Float, y: Float) {
        windowManager.removeView(removeBar)
    }

    override fun onDragMoved(x: Float, y: Float) {
        removeBar.setBackgroundColor(ContextCompat.getColor(this,
                if (y > removeBarY() - removeBarHeight) R.color.red
                else R.color.light_red))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpMoneyButton() {
        btnMoney.setOnTouchListener { view, event ->
            if (event.rawY > removeBarY() - removeBarHeight && event.action == ACTION_UP) {
                stopService(Intent(this, ConverterService::class.java))
                return@setOnTouchListener true
            }
            return@setOnTouchListener onTouch(view, event)
        }
    }

    private fun bindUnits(config: ConverterConfiguration) {
        spBase.adapter = configureAdapter(config.units)
        spTarget.adapter = configureAdapter(config.units)
        spBase.setSelection(config.baseIndex)
        spTarget.setSelection(config.targetIndex)
    }

    private fun configureAdapter(units: Array<String>): ArrayAdapter<String> {
        return ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
    }

    private fun bindViewModel() {
        val input = ConverterViewModel.Input(
                Observable.just(1),
                getStreamText(edtBase),
                getStreamText(edtTarget),
                getStreamSelection(spBase),
                getStreamSelection(spTarget)
        )
        val output = viewModel.bind(input)

        disposables.add(output.baseResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setMoney(edtBase, it) }, Timber::e))
        disposables.add(output.targetResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setMoney(edtTarget, it) }, Timber::e))
        disposables.add(output.converterConfiguration
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindUnits(it) }, Timber::e))
        disposables.addAll(output.loading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindLoading(it) }, Timber::e))
        disposables.addAll(output.expand
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindExpand(it) }, Timber::e))
    }

    private fun bindExpand(expand: Boolean) {
        if (expand) {
            showContent()
        } else {
            hideContent()
        }
    }

    private fun bindLoading(loading: Boolean) {
        pbLoading.visibility = if (loading) View.VISIBLE else View.GONE
        content.visibility = if (loading) View.GONE else View.VISIBLE
    }

    override fun onBackPressed(): Boolean {
        return if (viewModel.isExpand()) {
            viewModel.setExpand(false)
            true
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        windowManager.removeView(removeBar)
        viewModel.unbind()
        disposables.dispose()
        super.onDestroy()
    }

    override fun content(container: ViewGroup): View {
        return LayoutInflater.from(this).inflate(R.layout.overlay_converter, container, false)
    }

    private fun showContent() {
        focusWindow()
        btnMoney.visibility = View.GONE
        content.visibility = View.VISIBLE
        edtBase.requestFocus()
        handler.postDelayed({
            inputManager.showSoftInput(edtBase, 0)
        }, DELAY_UNTIL_GAIN_FOCUS)
    }

    private fun hideContent() {
        btnMoney.visibility = View.VISIBLE
        content.visibility = View.GONE
        inputManager.hideSoftInputFromWindow(edtBase.windowToken, 0)
        inputManager.hideSoftInputFromWindow(edtTarget.windowToken, 0)
        unFocusWindow()
    }

    @OnClick(R.id.btnMoney)
    fun onBtnMoneyClicked() {
        viewModel.setExpand(true)
    }
}
