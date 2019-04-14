package com.rain.currency.data.api

import com.google.gson.annotations.SerializedName

data class LiveCurrency(
        @SerializedName("timestamp") val timestamp: Long,
        @SerializedName("source") val source: String,
        @SerializedName("quotes") val quotes: Map<String, Double>
)
