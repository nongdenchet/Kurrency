package com.rain.currency.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnLongClick
import com.rain.currency.R
import com.rain.currency.support.OverlayService
import com.rain.currency.utils.getStreamSelection
import com.rain.currency.utils.getStreamText
import com.rain.currency.utils.setText
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject


class ConverterService : OverlayService() {
    private val disposables = CompositeDisposable()
    private lateinit var inputManager: InputMethodManager

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        ButterKnife.bind(this, container)
        btnMoney.setOnTouchListener(this)
        bindViewModel()
    }

    private fun bindUnits(viewModel: SpinnerViewModel) {
        spBase.adapter = configureAdapter(viewModel.units)
        spTarget.adapter = configureAdapter(viewModel.units)
        spBase.setSelection(viewModel.baseIndex)
        spTarget.setSelection(viewModel.targetIndex)
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
                .subscribe({ setText(edtBase, it) }, Timber::e))
        disposables.add(output.targetResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setText(edtTarget, it) }, Timber::e))
        disposables.add(output.spinnerViewModel
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindUnits(it) }, Timber::e))
        disposables.addAll(output.loading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindLoading(it) }, Timber::e))
    }

    private fun bindLoading(loading: Boolean) {
        pbLoading.visibility = if (loading) View.VISIBLE else View.GONE
        content.visibility = if (!loading && expand) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        viewModel.unbind()
        disposables.dispose()
        super.onDestroy()
    }

    override fun content(container: ViewGroup): View {
        return LayoutInflater.from(this).inflate(R.layout.overlay_converter, container, false)
    }

    private fun showContent() {
        btnMoney.visibility = View.GONE
        content.visibility = View.VISIBLE
        expand = true
        edtBase.requestFocus()
        inputManager.showSoftInput(edtBase, 0)
    }

    private fun hideContent() {
        btnMoney.visibility = View.VISIBLE
        content.visibility = View.GONE
        expand = false
        inputManager.hideSoftInputFromWindow(edtBase.windowToken, 0)
        inputManager.hideSoftInputFromWindow(edtTarget.windowToken, 0)
    }

    @OnClick(R.id.content)
    fun onContentClicked() {
        hideContent()
    }

    @OnLongClick(R.id.btnMoney)
    fun onBtnMoneyLongClicked(): Boolean {
        if (!moving) {
            stopService(Intent(this, ConverterService::class.java))
            return true
        }
        return false
    }

    @OnClick(R.id.btnMoney)
    fun onBtnMoneyClicked() {
        if (expand) {
            hideContent()
        } else {
            showContent()
        }
    }
}
