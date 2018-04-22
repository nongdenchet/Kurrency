package com.rain.currency.data.network

import io.reactivex.Single
import retrofit2.http.GET

interface CurrencyApi {

    @GET("latest")
    fun getLiveCurrency(): Single<LiveCurrency>
}
