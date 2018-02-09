package com.rain.currency.data.model

data class Currency(
        val base: String = "0",
        val target: String = "0",
        val baseUnit: String,
        val targetUnit: String
)
