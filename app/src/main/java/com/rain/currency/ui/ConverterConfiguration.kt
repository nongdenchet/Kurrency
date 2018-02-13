package com.rain.currency.ui

import java.util.Arrays

data class ConverterConfiguration(val units: Array<String>, val baseIndex: Int, val targetIndex: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConverterConfiguration

        if (!Arrays.equals(units, other.units)) return false
        if (baseIndex != other.baseIndex) return false
        if (targetIndex != other.targetIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(units)
        result = 31 * result + baseIndex
        result = 31 * result + targetIndex
        return result
    }
}
