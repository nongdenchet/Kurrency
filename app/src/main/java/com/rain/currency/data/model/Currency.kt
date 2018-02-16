package com.rain.currency.data.model

data class Currency(
        val base: String = "",
        val target: String = "",
        val baseUnit: String,
        val targetUnit: String
)
