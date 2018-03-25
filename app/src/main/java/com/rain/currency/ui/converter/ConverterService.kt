package com.rain.currency.ui.converter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rain.currency.R
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.support.OverlayService
import com.rain.currency.ui.menu.MenuHandler
import com.rain.currency.ui.picker.CurrencyPickerDialog
import com.rain.currency.ui.picker.CurrencyType
import com.rain.currency.utils.getClicks
import com.rain.currency.utils.getScreenSize
import com.rain.currency.utils.getStreamText
import com.rain.currency.utils.loadIcon
import com.rain.currency.utils.setMoney
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ConverterService : OverlayService() {
    private val disposables = CompositeDisposable()
    private var hidingDisposable: Disposable? = null
    private val handler = Handler()

    private lateinit var background: Background
    private lateinit var removeBar: RemoveBar

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
    lateinit var currencyPicker: CurrencyPickerDialog
    @Inject
    lateinit var inputMethodManager: InputMethodManager
    @Inject
    lateinit var menuHandler: MenuHandler

    private fun screenSize() = getScreenSize(windowManager)

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        ButterKnife.bind(this, window)
        setUpMoneyButton()
        setUpEditText()
        bindViewModel()
    }

    override fun initialize() {
        background = Background(this)
        background.setOnClickListener { viewModel.onBackPressed() }
        background.attach()
        removeBar = RemoveBar(this)
    }

    private fun setUpEditText() {
        currencyPicker.onDismiss = this::onPickerDismiss
        menuHandler.attach(edtBase)
        menuHandler.attach(edtTarget)
    }

    private fun onPickerDismiss() {
        if (edtBase.hasFocus()) {
            inputMethodManager.showSoftInput(edtBase, 0)
        } else if (edtTarget.hasFocus()) {
            inputMethodManager.showSoftInput(edtTarget, 0)
        }
    }

    override fun onDragStarted(x: Float, y: Float) {
        removeBar.attach()
        focusMoneyButton()
    }

    override fun onDragEnded(x: Float, y: Float) {
        removeBar.detach()
        blurMoneyButton()
    }

    override fun onDragMoved(x: Float, y: Float) {
        removeBar.update(x, y)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpMoneyButton() {
        btnMoney.setOnTouchListener { view, event ->
            if (event.rawY > removeBar.getY() - removeBar.height && event.action == ACTION_UP) {
                stopService(Intent(this, ConverterService::class.java))
                return@setOnTouchListener true
            }
            return@setOnTouchListener onTouch(view, event)
        }
    }

    private fun bindViewModel() {
        val input = ConverterViewModel.Input(
                getClicks(btnRetry),
                getClicks(btnMoney),
                getStreamText(edtBase),
                getStreamText(edtTarget),
                currencyPicker.getUnit(CurrencyType.BASE),
                currencyPicker.getUnit(CurrencyType.TARGET)
        )
        val output = viewModel.bind(input)
        disposables.addAll(
                output.baseResult.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ setMoney(edtBase, it) }, Timber::e),
                output.targetResult.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ setMoney(edtTarget, it) }, Timber::e),
                output.baseCurrency.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ bindBaseCurrency(it) }, Timber::e),
                output.targetCurrency.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ bindTargetCurrency(it) }, Timber::e),
                output.showContent.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ bindContent(it) }, Timber::e),
                output.loadingVisibility.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ pbLoading.visibility = it }, Timber::e),
                output.errorVisibility.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ btnRetry.visibility = it }, Timber::e),
                output.expand.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ bindExpand(it) }, Timber::e)
        )
    }

    private fun bindContent(value: Boolean) {
        if (value) {
            content.visibility = View.VISIBLE
            edtBase.requestFocus()
            handler.postDelayed({
                inputMethodManager.showSoftInput(edtBase, 0)
            }, 100)
        } else {
            content.visibility = View.INVISIBLE
        }
    }

    private fun bindBaseCurrency(currencyInfo: CurrencyInfo) {
        tvBaseUnit.text = currencyInfo.unit
        tvBaseSymbol.text = currencyInfo.symbol
        loadIcon(ivBaseIcon, currencyInfo.icon)
    }

    private fun bindTargetCurrency(currencyInfo: CurrencyInfo) {
        tvTargetUnit.text = currencyInfo.unit
        tvTargetSymbol.text = currencyInfo.symbol
        loadIcon(ivTargetIcon, currencyInfo.icon)
    }

    private fun bindExpand(expand: Boolean) {
        if (expand) {
            showContent()
            focusMoneyButton()
            background.activateBackground()
        } else {
            hideContent()
            blurMoneyButton()
            background.deactivateBackground()
        }
    }

    private fun focusMoneyButton() {
        hidingDisposable?.dispose()
        btnMoney.alpha = 1f
    }

    private fun blurMoneyButton() {
        hidingDisposable?.dispose()
        hidingDisposable = Observable.just(0)
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ btnMoney.alpha = 0.25f }, Timber::e)
    }

    override fun onBackPressed() = viewModel.onBackPressed()

    override fun onDestroy() {
        removeBar.detach()
        background.detach()
        menuHandler.dismiss()
        currencyPicker.onDismiss = null
        viewModel.unbind()
        disposables.dispose()
        hidingDisposable?.dispose()
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
        menuHandler.dismiss()
    }

    @OnClick(R.id.ivBaseIcon, R.id.tvBaseUnit)
    fun onBaseClick() {
        currencyPicker.show(CurrencyType.BASE)
    }

    @OnClick(R.id.ivTargetIcon, R.id.tvTargetUnit)
    fun onTargetClick() {
        currencyPicker.show(CurrencyType.TARGET)
    }

    override fun getX() = screenSize().widthPixels

    override fun getY()= screenSize().heightPixels / 2 - resources.getDimensionPixelSize(R.dimen.button_money_size) / 2
}
