package com.rain.currency.data.model

import android.support.v4.util.ArrayMap
import java.util.Date

class Exchange(val base: String, val date: Date, val currencies: ArrayMap<String, Double>)
