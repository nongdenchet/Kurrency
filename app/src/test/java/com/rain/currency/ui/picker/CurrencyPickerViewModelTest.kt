package com.rain.currency.ui.picker

import android.support.v4.util.ArrayMap
import com.jakewharton.rxrelay2.PublishRelay
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.data.model.Exchange
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.support.CurrencyMapper
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date

class CurrencyPickerViewModelTest {
    private val keyword = PublishRelay.create<String>()
    private val itemSelection = PublishRelay.create<Int>()
    private lateinit var viewModel: CurrencyPickerViewModel

    @Mock
    lateinit var currencyRepo: CurrencyRepo
    @Mock
    lateinit var currencyMapper: CurrencyMapper

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val currencies = ArrayMap<String, Double>()
        currencies["USD"] = 1.0
        currencies["VND"] = 1.0 / 20000
        currencies["SGD"] = 1.0 / 2
        `when`(currencyRepo.fetchExchange(ArgumentMatchers.anyBoolean())).thenReturn(
                Single.just(Exchange("USD", Date(System.currentTimeMillis()), currencies)))
        `when`(currencyMapper.toInfo(ArgumentMatchers.anyString()))
                .thenAnswer { CurrencyInfo(it.arguments[0] as String, "$", 0) }

        viewModel = CurrencyPickerViewModel(currencyRepo, currencyMapper)
    }

    @Test
    fun shouldEmitEmptyList() {
        val output = bind(CurrencyType.BASE)
        output.currencies
                .test()
                .assertEmpty()
    }

    @Test
    fun shouldEmitAll() {
        val output = bind(CurrencyType.BASE)
        keyword.accept("")
        output.currencies
                .test()
                .assertValue { it.size == 3 }
    }

    @Test
    fun shouldEmitNoItem() {
        val output = bind(CurrencyType.BASE)
        keyword.accept("vng")
        output.currencies
                .test()
                .assertValue { it.isEmpty() }
    }

    @Test
    fun shouldEmitMatchItem() {
        val output = bind(CurrencyType.BASE)
        keyword.accept("usd")
        output.currencies
                .test()
                .assertValue { it.size == 1 }
    }

    @Test
    fun shouldEmitSelectedItem() {
        val output = bind(CurrencyType.BASE)
        val result = output.result.test()
        keyword.accept("usd")
        itemSelection.accept(0)
        result.assertValue {
            it.first == CurrencyType.BASE && it.second.unit == "USD"
        }
    }

    @Test
    fun shouldEmitNoSelectedItem() {
        val output = bind(CurrencyType.BASE)
        val result = output.result.test()
        keyword.accept("usd")
        itemSelection.accept(3)
        result.assertEmpty()
    }

    @After
    fun tearDown() {
        viewModel.unbind()
    }

    private fun bind(base: CurrencyType): CurrencyPickerViewModel.Output {
        return viewModel.bind(CurrencyPickerViewModel.Input(base, keyword, itemSelection))
    }
}