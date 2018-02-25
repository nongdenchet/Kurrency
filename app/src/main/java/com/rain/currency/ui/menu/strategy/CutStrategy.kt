package com.rain.currency.ui.menu.strategy

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.EditText
import com.rain.currency.ui.menu.MenuStrategy
import com.rain.currency.ui.menu.MenuType

class CutStrategy(private val clipboardManager: ClipboardManager, private val editText: EditText) : MenuStrategy {
    override fun menuType() = MenuType.CUT

    override fun execute() {
        clipboardManager.primaryClip = ClipData.newPlainText(MenuType.CUT.name, editText.text)
        editText.setText("")
    }
}