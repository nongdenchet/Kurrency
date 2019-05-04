package com.rain.currency.data.api

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Test

class LiveCurrencyTest {
    private val moshi = Moshi.Builder()
            .build()

    @Test
    fun parseModelFromString_shouldReturnLiveCurrency() {
        val adapter = moshi.adapter(LiveCurrency::class.java)
        val liveCurrency = adapter.fromJson("""
            {
                "timestamp": 1000,
                "source": "USD",
                "quotes": {
                    "VND": 23000,
                    "SGD": 1.368
                }
            }
        """)

        assertEquals(LiveCurrency(1000, "USD", mapOf(
                "VND" to 23000.0,
                "SGD" to 1.368
        )), liveCurrency)
    }

    @Test(expected = JsonDataException::class)
    fun parseModelFromString_shouldThrowExceptionWhenMissingSource() {
        val adapter = moshi.adapter(LiveCurrency::class.java)
        adapter.fromJson("""
            {
                "timestamp": 1000,
                "quotes": {
                    "VND": 23000,
                    "SGD": 1.368
                }
            }
        """)
    }

    @Test(expected = JsonDataException::class)
    fun parseModelFromString_shouldThrowExceptionWhenSourceNull() {
        val adapter = moshi.adapter(LiveCurrency::class.java)
        adapter.fromJson("""
            {
                "timestamp": 1000,
                "source": null,
                "quotes": {
                    "VND": 23000,
                    "SGD": 1.368
                }
            }
        """)
    }
}
