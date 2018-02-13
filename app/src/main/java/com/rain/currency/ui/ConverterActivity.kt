package com.rain.currency.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.rain.currency.R
import com.rain.currency.utils.getStreamSelection
import com.rain.currency.utils.getStreamText
import com.rain.currency.utils.setText
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_converter.content
import kotlinx.android.synthetic.main.activity_converter.edtBase
import kotlinx.android.synthetic.main.activity_converter.edtTarget
import kotlinx.android.synthetic.main.activity_converter.pbLoading
import kotlinx.android.synthetic.main.activity_converter.spBase
import kotlinx.android.synthetic.main.activity_converter.spTarget
import timber.log.Timber
import javax.inject.Inject

class ConverterActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var viewModel: ConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)
        bindViewModel()
    }

    private fun bindUnits(config: ConverterConfigurations) {
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
                .subscribe({ setText(edtBase, it) }, Timber::e))
        disposables.add(output.targetResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setText(edtTarget, it) }, Timber::e))
        disposables.add(output.converterConfigurations
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindUnits(it) }, Timber::e))
        disposables.addAll(output.loading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindLoading(it) }, Timber::e))
    }

    private fun bindLoading(loading: Boolean) {
        pbLoading.visibility = if (loading) View.VISIBLE else View.GONE
        content.visibility = if (loading) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        viewModel.unbind()
        disposables.dispose()
        super.onDestroy()
    }
}
