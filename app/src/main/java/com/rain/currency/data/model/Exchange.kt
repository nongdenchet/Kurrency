package com.rain.currency.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Exchange(@Json(name = "currencies") val currencies: Map<String, Double>)
