package com.rain.currency.ui.picker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.rain.currency.ui.picker.di.DaggerCurrencyPickerComponent
import butterknife.BindView
import butterknife.ButterKnife
import com.jakewharton.rxrelay2.PublishRelay
import com.rain.currency.CurrencyApp
import com.rain.currency.R
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.utils.getOverlayType
import com.rain.currency.utils.getStreamText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("InflateParams")
class CurrencyPickerDialog(private val context: Context) {
    private val result = PublishRelay.create<Pair<CurrencyType, CurrencyInfo>>()
    private val disposables = CompositeDisposable()
    private val handler = Handler()
    private lateinit var dialog: AlertDialog
    var onDismiss: (() -> Unit)? = null

    @Inject
    lateinit var adapter: CurrencyPickerAdapter

    @Inject
    lateinit var viewModel: CurrencyPickerViewModel

    @Inject
    lateinit var inputMethodManager: InputMethodManager

    @BindView(R.id.rvCurrencies)
    lateinit var rvCurrencies: RecyclerView

    @BindView(R.id.edtSearch)
    lateinit var edtSearch: EditText

    init {
        setUpDependency()
        setUpView()
    }

    private fun bindViewModel(currencyType: CurrencyType) {
        val input = CurrencyPickerViewModel.Input(
            currencyType,
            getStreamText(edtSearch),
            adapter.itemClicks()
        )
        val output = viewModel.bind(input)

        disposables.add(output.currencies
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ adapter.submitList(it) }, Timber::e)
        )
        disposables.add(output.result
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                result.accept(it)
                dialog.dismiss()
            }, Timber::e)
        )
    }

    private fun setUpDependency() {
        val app = context.applicationContext as CurrencyApp
        DaggerCurrencyPickerComponent.builder()
            .dependencies(app.component)
            .build()
            .inject(this)
    }

    private fun setUpView() {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_currency_picker, null, false)
        ButterKnife.bind(this, view)
        dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        dialog.window?.run {
            setType(getOverlayType())
        }
        dialog.setOnDismissListener {
            onDismiss?.invoke()
            disposables.clear()
            viewModel.unbind()
            edtSearch.setText("")
        }
        rvCurrencies.adapter = adapter
        rvCurrencies.layoutManager = LinearLayoutManager(context)
    }

    fun getUnit(currencyType: CurrencyType): Observable<String> {
        return result.hide()
            .filter { it.first == currencyType }
            .map { it.second }
            .map { it.unit }
    }

    fun show(currencyType: CurrencyType) {
        dialog.show()
        bindViewModel(currencyType)
        handler.postDelayed({
            inputMethodManager.showSoftInput(edtSearch, 0)
        }, 100L)
    }
}
