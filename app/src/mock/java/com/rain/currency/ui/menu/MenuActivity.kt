package com.rain.currency.ui.menu

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.rain.currency.R

class MenuActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val edtBase = EditText(this)
        edtBase.id = R.id.edtBase
        edtBase.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

        val edtTarget = EditText(this)
        edtTarget.id = R.id.edtTarget
        edtTarget.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(edtBase)
        linearLayout.addView(edtTarget)
        linearLayout.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

        setContentView(linearLayout)

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val menuHandler = MenuHandler(clipboardManager)
        menuHandler.attach(edtBase)
        menuHandler.attach(edtTarget)
    }
}
