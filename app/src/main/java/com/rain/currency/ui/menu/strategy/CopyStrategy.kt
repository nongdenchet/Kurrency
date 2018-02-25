package com.rain.currency.ui.menu.strategy

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.EditText
import com.rain.currency.ui.menu.MenuStrategy
import com.rain.currency.ui.menu.MenuType

class CopyStrategy(private val clipboardManager: ClipboardManager, private val editText: EditText) : MenuStrategy {
    override fun menuType() = MenuType.COPY

    override fun execute() {
        clipboardManager.primaryClip = ClipData.newPlainText(MenuType.COPY.name, editText.text)
    }
}
