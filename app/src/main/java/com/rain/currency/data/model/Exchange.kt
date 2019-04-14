package com.rain.currency.data.model

import androidx.collection.ArrayMap
import java.util.Date

class Exchange(val base: String, val date: Date, val currencies: androidx.collection.ArrayMap<String, Double>)
