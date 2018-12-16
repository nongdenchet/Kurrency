package com.rain.currency.ui.menu

interface MenuStrategy {
    fun menuType(): MenuType
    fun execute()
}
