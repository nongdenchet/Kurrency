package com.rain.currency.ui.menu.strategy

import android.content.ClipboardManager
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import com.rain.currency.R
import com.rain.currency.ui.menu.MenuStrategy
import com.rain.currency.ui.menu.MenuType
import java.lang.Exception
import java.text.NumberFormat

class PasteStrategy(
    private val clipboardManager: ClipboardManager,
    private val editText: EditText
) : MenuStrategy {
    private val numberFormatter = NumberFormat.getNumberInstance()

    override fun menuType() = MenuType.PASTE

    override fun execute() {
        if (!clipboardManager.hasPrimaryClip()) {
            return
        }

        val primaryClip = clipboardManager.primaryClip
        if (primaryClip == null || primaryClip.itemCount == 0 || primaryClip.getItemAt(0).text.isNullOrBlank()) {
            return
        }

        primaryClip.getItemAt(0).text?.let {
            if (it.isNotBlank()) {
                try {
                    numberFormatter.parse(it.toString())
                    editText.requestFocus()
                    editText.setText(it)
                    editText.setSelection(it.length)
                } catch (e: Exception) {
                    val context = editText.context
                    val toast = Toast.makeText(
                        context,
                        context.getString(R.string.invalid_amount),
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
        }
    }
}
