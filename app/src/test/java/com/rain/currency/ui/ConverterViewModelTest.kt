package com.rain.currency.ui

import android.support.v4.util.ArrayMap
import android.view.View
import com.jakewharton.rxrelay2.PublishRelay
import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.CurrencyInfo
import com.rain.currency.data.model.Exchange
import com.rain.currency.data.repo.CurrencyRepo
import com.rain.currency.domain.ConverterInteractor
import com.rain.currency.support.CurrencyMapper
import com.rain.currency.ui.converter.ConverterViewModel
import com.rain.currency.ui.converter.reducer.ConverterReducer
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.Date

class ConverterViewModelTest {
    private lateinit var converterViewModel: ConverterViewModel
    private val retryClicks = PublishRelay.create<Any>()
    private val moneyClicks = PublishRelay.create<Any>()
    private val backClicks = PublishRelay.create<Any>()
    private val baseChange = PublishRelay.create<String>()
    private val targetChange = PublishRelay.create<String>()
    private val baseUnitChange = PublishRelay.create<String>()
    private val targetUnitChange = PublishRelay.create<String>()

    @Mock
    private lateinit var currencyRepo: CurrencyRepo
    @Mock
    private lateinit var currencyMapper: CurrencyMapper

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mockSchedulers()
        mockData()
        converterViewModel = ConverterViewModel(
                currencyRepo,
                ConverterReducer(ConverterInteractor()),
                currencyMapper)
    }

    private fun mockSchedulers() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    }

    private fun mockData() {
        val currencies = ArrayMap<String, Double>()
        currencies["USD"] = 1.0
        currencies["VND"] = 1.0 / 20000
        currencies["SGD"] = 1.0 / 2

        `when`(currencyRepo.fetchLastCurrency()).thenReturn(
                Single.just(Pair("USD", "VND")).map { Currency(baseUnit = it.first, targetUnit = it.second) })
        `when`(currencyRepo.fetchExchange(ArgumentMatchers.anyBoolean())).thenReturn(
                Single.just(Exchange("USD", Date(System.currentTimeMillis()), currencies)))
        `when`(currencyMapper.toInfo(ArgumentMatchers.anyString()))
                .thenAnswer { CurrencyInfo(it.arguments[0] as String, "$", 0) }
    }

    private fun bind(): ConverterViewModel.Output {
        return converterViewModel.bind(ConverterViewModel.Input(retryClicks, moneyClicks,
                baseChange, targetChange, baseUnitChange, targetUnitChange, backClicks))
    }

    @Test
    fun shouldExpands() {
        val output = bind()
        val observer = output.expand.test()
        moneyClicks.accept(1)
        observer.assertValues(false, true)
    }

    @Test
    fun shouldCollapses() {
        val output = bind()
        val observer = output.expand.test()
        moneyClicks.accept(1)
        backClicks.accept(1)
        observer.assertValues(false, true, false)
    }

    @Test
    fun shouldDoNothing() {
        val output = bind()
        val observer = output.expand.test()
        backClicks.accept(1)
        observer.assertValues(false)
    }

    @Test
    fun shouldEmitLoading() {
        val output = bind()
        val observer = output.loadingVisibility.test()
        moneyClicks.accept(1)
        observer.assertValues(View.GONE, View.VISIBLE, View.GONE)
    }

    @Test
    fun shouldRecoverAfterFailed() {
        `when`(currencyRepo.fetchLastCurrency()).thenReturn(Single.error(RuntimeException()))
        val output = bind()
        moneyClicks.accept(1)
        mockData()
        retryClicks.accept(1)
        baseChange.accept("1")
        output.targetResult.test().assertValue("20000")
    }

    @Test
    fun shouldRetry() {
        val output = bind()
        val observer = output.loadingVisibility.test()
        moneyClicks.accept(1)
        retryClicks.accept(1)
        observer.assertValues(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE)
    }

    @Test
    fun shouldEmitInitState() {
        val output = bind()
        output.baseResult.test().assertValue("")
        output.targetResult.test().assertValue("")
    }

    @Test
    fun shouldEmitNewTarget() {
        val output = bind()
        baseChange.accept("1")
        output.targetResult.test().assertValue("20000")
    }

    @Test
    fun shouldEmitNewBase() {
        val output = bind()
        targetChange.accept("150000")
        output.baseResult.test().assertValue("7.50")
    }

    @Test
    fun shouldEmitNewTargetWhenTargetUnitChange() {
        val output = bind()
        baseChange.accept("15")
        targetUnitChange.accept("SGD")
        output.targetResult.test().assertValue("30.00")
    }

    @Test
    fun shouldStoreWhenTargetUnitChange() {
        val output = bind()
        output.targetCurrency.subscribe()
        targetUnitChange.accept("SGD")
        verify(currencyRepo).storeTargetUnit("SGD")
    }

    @Test
    fun shouldStoreWhenBaseUnitChange() {
        val output = bind()
        output.baseCurrency.subscribe()
        baseUnitChange.accept("SGD")
        verify(currencyRepo).storeBaseUnit("SGD")
    }

    @Test
    fun shouldEmitNewTargetWhenBaseUnitChange() {
        val output = bind()
        baseChange.accept("1")
        baseUnitChange.accept("VND")
        output.targetResult.test().assertValue("1.00")
    }

    @Test
    fun shouldEmitNewTargets() {
        val output = bind()
        val observer = output.targetResult.test()
        baseChange.accept("1")
        baseChange.accept("hello")
        baseChange.accept("-200")
        baseChange.accept("0.1")
        observer.assertValues("", "20000", "", "2000")
    }

    @Test
    fun shouldEmitNewBases() {
        val output = bind()
        val observer = output.baseResult.test()
        targetChange.accept("1000")
        targetChange.accept("hello")
        targetChange.accept("-200")
        targetChange.accept("10000")
        observer.assertValues("", "0.05", "", "0.50")
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }
}
