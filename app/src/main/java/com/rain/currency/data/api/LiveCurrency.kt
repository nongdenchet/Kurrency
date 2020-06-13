package com.rain.currency.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LiveCurrency(
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "source") val source: String,
    @Json(name = "quotes") val quotes: Map<String, Double>
)
