package com.rain.currency.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.rain.currency.R
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.utils.getStreamSelection
import com.rain.currency.utils.getStreamText
import com.rain.currency.utils.setText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_converter.content
import kotlinx.android.synthetic.main.activity_converter.edtBase
import kotlinx.android.synthetic.main.activity_converter.edtTarget
import kotlinx.android.synthetic.main.activity_converter.pbLoading
import kotlinx.android.synthetic.main.activity_converter.spBase
import kotlinx.android.synthetic.main.activity_converter.spTarget


class ConverterActivity : AppCompatActivity() {
    private val viewModel = ConverterViewModel(CurrencyRepo(), ConverterReducer())
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)
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
                getStreamText(edtBase, window),
                getStreamText(edtTarget, window),
                getStreamSelection(spBase),
                getStreamSelection(spTarget)
        )
        val output = viewModel.bind(input)

        disposables.add(output.baseResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { setText(edtBase, it) })
        disposables.add(output.targetResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { setText(edtTarget, it) })
        disposables.add(output.spinnerViewModel
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { bindUnits(it) })
        disposables.addAll(output.loading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { bindLoading(it) })
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
