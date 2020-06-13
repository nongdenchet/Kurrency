package com.rain.currency.ui.menu

enum class MenuType {
    COPY, PASTE, CUT, CLEAR;

    override fun toString(): String {
        return super.toString()
            .toLowerCase()
            .capitalize()
    }
}
