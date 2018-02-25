package com.rain.currency.ui.menu.strategy

import android.content.ClipboardManager
import android.widget.EditText
import com.rain.currency.ui.menu.MenuStrategy
import com.rain.currency.ui.menu.MenuType

class PasteStrategy(private val clipboardManager: ClipboardManager, private val editText: EditText) : MenuStrategy {
    override fun menuType() = MenuType.PASTE

    override fun execute() {
        if (!clipboardManager.hasPrimaryClip()) {
            return
        }

        val primaryClip = clipboardManager.primaryClip

        if (primaryClip.itemCount == 0 || primaryClip.getItemAt(0).text.isNullOrBlank()) {
            return
        }

        primaryClip.getItemAt(0).text?.let {
            if (it.isNotBlank()) {
                editText.setText(it)
                editText.setSelection(it.length)
            }
        }
    }
}
