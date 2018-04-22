package com.rain.currency.ui.menu

import com.rain.currency.ui.menu.MenuType

interface MenuStrategy {
    fun menuType(): MenuType
    fun execute()
}
