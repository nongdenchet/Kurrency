package com.rain.currency.ui

import android.support.v4.util.ArrayMap
import com.jakewharton.rxrelay2.PublishRelay
import com.rain.currency.data.local.UserCurrencyStore
import com.rain.currency.data.model.Currency
import com.rain.currency.data.model.Exchange
import com.rain.currency.data.network.CurrencyApi
import com.rain.currency.data.repo.CurrencyRepo
import io.reactivex.Observable
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.Date

class ConverterViewModelTest {
    private lateinit var converterViewModel: ConverterViewModel
    private val baseChange = PublishRelay.create<String>()
    private val targetChange = PublishRelay.create<String>()
    private val baseUnitChange = PublishRelay.create<Int>()
    private val targetUnitChange = PublishRelay.create<Int>()
    private val userCurrencyStore = mock(UserCurrencyStore::class.java)

    class CurrencyRepoStub constructor(userCurrencyStore: UserCurrencyStore)
        : CurrencyRepo(mock(CurrencyApi::class.java), userCurrencyStore) {
        override fun fetchExchange(): Single<Exchange> {
            val currencies = ArrayMap<String, Double>()
            currencies["USD"] = 1.0
            currencies["VND"] = 1.0 / 20000

            return Single.fromCallable {
                return@fromCallable Exchange("USD", Date(System.currentTimeMillis()), currencies)
            }
        }

        override fun fetchLastCurrency(): Single<Currency> {
            return Single.just(Pair("USD", "VND"))
                    .map { Currency(baseUnit = it.first, targetUnit = it.second) }
        }
    }

    @Before
    fun setUp() {
        converterViewModel = ConverterViewModel(CurrencyRepoStub(userCurrencyStore), ConverterReducer())
    }

    private fun bind(trigger: Observable<Any> = Observable.just(1)): ConverterViewModel.Output {
        return converterViewModel.bind(ConverterViewModel.Input(trigger,
                baseChange, targetChange, baseUnitChange, targetUnitChange))
    }

    @Test
    fun shouldEmitSpinnerData() {
        val output = bind()
        val viewModel = output.converterConfigurations.test().values()[0]
        assertArrayEquals(arrayOf("USD", "VND"), viewModel.units)
        assertEquals(0, viewModel.baseIndex)
        assertEquals(1, viewModel.targetIndex)
    }

    @Test
    fun shouldEmitLoading() {
        val trigger = PublishRelay.create<Int>()
        val output = bind(trigger.map {})
        val observer = output.loading.test()
        trigger.accept(1)

        observer.assertValues(true, false)
    }

    @Test
    fun shouldEmitInitState() {
        val output = bind()
        output.baseResult.test().assertValue("0")
        output.targetResult.test().assertValue("0")
    }

    @Test
    fun shouldEmitNewTarget() {
        val output = bind()
        baseChange.accept("1")
        output.targetResult.test().assertValue("20000.0000")
    }

    @Test
    fun shouldEmitNewBase() {
        val output = bind()
        targetChange.accept("150000")
        output.baseResult.test().assertValue("7.5000")
    }

    @Test
    fun shouldEmitNewBaseWhenTargetUnitChange() {
        val output = bind()
        targetChange.accept("150000")
        targetUnitChange.accept(0)
        output.baseResult.test().assertValue("150000.0000")
    }

    @Test
    fun shouldEmitNewTargetWhenBaseUnitChange() {
        val output = bind()
        baseChange.accept("1")
        baseUnitChange.accept(1)
        output.targetResult.test().assertValue("1.0000")
    }

    @Test
    fun shouldEmitNewTargets() {
        val output = bind()
        val observer = output.targetResult.test()
        baseChange.accept("1")
        baseChange.accept("hello")
        baseChange.accept("-200")
        baseChange.accept("0.1")
        observer.assertValues("0", "20000.0000", "", "2000.0000")
    }

    @Test
    fun shouldEmitNewBases() {
        val output = bind()
        val observer = output.baseResult.test()
        targetChange.accept("1000")
        targetChange.accept("hello")
        targetChange.accept("-200")
        targetChange.accept("10000")
        observer.assertValues("0", "0.0500", "", "0.5000")
    }

    @Test
    fun shouldStoreUserCurrency() {
        bind()
        converterViewModel.unbind()
        verify(userCurrencyStore).storeCurrencies("USD", "VND")
    }
}
