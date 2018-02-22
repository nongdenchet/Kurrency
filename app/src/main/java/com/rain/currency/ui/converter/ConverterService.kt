package com.rain.currency.ui.converter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.rain.currency.R
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.support.OverlayService
import com.rain.currency.ui.picker.CurrencyPicker
import com.rain.currency.ui.picker.CurrencyType
import com.rain.currency.utils.getClicks
import com.rain.currency.utils.getOverlayType
import com.rain.currency.utils.getScreenSize
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
    private lateinit var removeBar: FrameLayout

    @BindView(R.id.pbLoading)
    lateinit var pbLoading: ProgressBar
    @BindView(R.id.edtBase)
    lateinit var edtBase: EditText
    @BindView(R.id.edtTarget)
    lateinit var edtTarget: EditText
    @BindView(R.id.container)
    lateinit var container: ViewGroup
    @BindView(R.id.content)
    lateinit var content: ViewGroup
    @BindView(R.id.btnMoney)
    lateinit var btnMoney: ImageView
    @BindView(R.id.btnRetry)
    lateinit var btnRetry: TextView

    @BindView(R.id.ivBaseIcon)
    lateinit var ivBaseIcon: ImageView
    @BindView(R.id.ivTargetIcon)
    lateinit var ivTargetIcon: ImageView
    @BindView(R.id.tvBaseSymbol)
    lateinit var tvBaseSymbol: TextView
    @BindView(R.id.tvTargetSymbol)
    lateinit var tvTargetSymbol: TextView
    @BindView(R.id.tvBaseUnit)
    lateinit var tvBaseUnit: TextView
    @BindView(R.id.tvTargetUnit)
    lateinit var tvTargetUnit: TextView

    @Inject
    lateinit var viewModel: ConverterViewModel
    @Inject
    lateinit var currencyPicker: CurrencyPicker
    @Inject
    lateinit var inputMethodManager: InputMethodManager

    private fun removeBarLayoutParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.RGBA_8888
        params.type = getOverlayType()
        params.gravity = Gravity.TOP or Gravity.START
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.width = removeBar.layoutParams.width
        params.height = removeBar.layoutParams.height
        params.y = removeBarY()
        return params
    }

    private fun screenSize() = getScreenSize(windowManager)

    private val removeBarHeight: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.remove_bar_height)
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        ButterKnife.bind(this, window)
        setUpView()
        bindViewModel()
    }

    private fun setUpView() {
        setUpMoneyButton()
        setUpRemoveBar()
        setUpEditText()
    }

    private fun setUpEditText() {
        currencyPicker.onDismiss = this::onPickerDismiss
    }

    private fun onPickerDismiss() {
        if (edtBase.hasFocus()) {
            inputMethodManager.showSoftInput(edtBase, 0)
        } else if (edtTarget.hasFocus()) {
            inputMethodManager.showSoftInput(edtTarget, 0)
        }
    }

    @SuppressLint("InflateParams")
    private fun setUpRemoveBar() {
        removeBar = LayoutInflater.from(this).inflate(R.layout.remove_bar, null) as FrameLayout
        removeBar.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, removeBarHeight)
    }

    private fun removeBarY() = screenSize().heightPixels - removeBarHeight

    override fun onDragStarted(x: Float, y: Float) {
        windowManager.addView(removeBar, removeBarLayoutParams())
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

    private fun getTriggerClicks(): Observable<Any> {
        return Observable.merge(
                getClicks(btnRetry),
                getClicks(btnMoney).doOnNext { viewModel.setExpand(true) }
        ).startWith(0)
    }

    private fun bindViewModel() {
        val input = ConverterViewModel.Input(
                getTriggerClicks(),
                getStreamText(edtBase),
                getStreamText(edtTarget),
                currencyPicker.getUnit(CurrencyType.BASE),
                currencyPicker.getUnit(CurrencyType.TARGET)
        )
        val output = viewModel.bind(input)

        disposables.add(output.baseResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setMoney(edtBase, it) }, Timber::e))
        disposables.add(output.targetResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setMoney(edtTarget, it) }, Timber::e))
        disposables.add(output.baseCurrency
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindBaseCurrency(it) }, Timber::e))
        disposables.add(output.targetCurrency
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindTargetCurrency(it) }, Timber::e))
        disposables.add(output.content
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindContent(it) }, Timber::e))
        disposables.add(output.loading
                .observeOn(AndroidSchedulers.mainThread())
                .map { if (it) View.VISIBLE else View.GONE }
                .subscribe({ pbLoading.visibility = it }, Timber::e))
        disposables.add(output.error
                .observeOn(AndroidSchedulers.mainThread())
                .map { if (it) View.VISIBLE else View.GONE }
                .subscribe({ btnRetry.visibility = it }, Timber::e))
        disposables.add(output.expand
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindExpand(it) }, Timber::e))
    }

    private fun bindContent(value: Boolean) {
        if (value) {
            content.visibility = View.VISIBLE
            edtBase.requestFocus()
            handler.postDelayed({
                inputMethodManager.showSoftInput(edtBase, 0)
            }, DELAY_UNTIL_GAIN_FOCUS)
        } else {
            content.visibility = View.INVISIBLE
        }
    }

    private fun bindBaseCurrency(currencyInfo: CurrencyInfo) {
        tvBaseUnit.text = currencyInfo.unit
        tvBaseSymbol.text = currencyInfo.symbol
        Glide.with(this)
                .load(currencyInfo.icon)
                .into(ivBaseIcon)
    }

    private fun bindTargetCurrency(currencyInfo: CurrencyInfo) {
        tvTargetUnit.text = currencyInfo.unit
        tvTargetSymbol.text = currencyInfo.symbol
        Glide.with(this)
                .load(currencyInfo.icon)
                .into(ivTargetIcon)
    }

    private fun bindExpand(expand: Boolean) {
        if (expand) {
            showContent()
        } else {
            hideContent()
        }
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
        currencyPicker.onDismiss = null
        if (removeBar.isAttachedToWindow) {
            windowManager.removeView(removeBar)
        }
        viewModel.unbind()
        disposables.dispose()
        super.onDestroy()
    }

    override fun container(window: ViewGroup): View {
        return LayoutInflater.from(this).inflate(R.layout.overlay_converter, window, false)
    }

    private fun showContent() {
        focusWindow()
        btnMoney.visibility = View.GONE
        container.visibility = View.VISIBLE
    }

    private fun hideContent() {
        btnMoney.visibility = View.VISIBLE
        container.visibility = View.GONE
        inputMethodManager.hideSoftInputFromWindow(edtBase.windowToken, 0)
        inputMethodManager.hideSoftInputFromWindow(edtTarget.windowToken, 0)
        unFocusWindow()
    }

    @OnClick(R.id.ivBaseIcon, R.id.tvBaseUnit)
    fun onBaseClick() {
        currencyPicker.show(CurrencyType.BASE)
    }

    @OnClick(R.id.ivTargetIcon, R.id.tvTargetUnit)
    fun onTargetClick() {
        currencyPicker.show(CurrencyType.TARGET)
    }
}
