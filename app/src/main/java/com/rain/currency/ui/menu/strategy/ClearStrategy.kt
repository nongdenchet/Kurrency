package com.rain.currency.ui.menu.strategy

import android.widget.EditText
import com.rain.currency.ui.menu.MenuStrategy
import com.rain.currency.ui.menu.MenuType

class ClearStrategy(private val editText: EditText) : MenuStrategy {
    override fun menuType() = MenuType.CLEAR

    override fun execute() {
        editText.setText("")
    }
}