package com.rain.currency.ui.menu

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.rain.currency.R
import com.rain.currency.ui.menu.strategy.ClearStrategy
import com.rain.currency.ui.menu.strategy.CopyStrategy
import com.rain.currency.ui.menu.strategy.CutStrategy
import com.rain.currency.ui.menu.strategy.PasteStrategy
import com.rain.currency.utils.getOverlayType

class MenuHandler(private val clipboardManager: ClipboardManager) {
    private var popupWindow: PopupWindow? = null

    @SuppressLint("InflateParams")
    private fun createView(context: Context, menus: List<MenuStrategy>): View {
        val layoutInflater = LayoutInflater.from(context)
        val contentView = layoutInflater.inflate(R.layout.popup_menu, null)
        val container = contentView.findViewById<LinearLayout>(R.id.container)

        menus.forEach { menu ->
            val menuItem = layoutInflater.inflate(R.layout.item_menu, container, false)
            val tvMenu = menuItem.findViewById<TextView>(R.id.tvMenu)

            tvMenu.text = menu.menuType().toString()
            tvMenu.setOnClickListener {
                menu.execute()
                dismiss()
            }
            container.addView(menuItem)
        }

        return contentView
    }

    fun dismiss() {
        popupWindow?.dismiss()
        popupWindow = null
    }

    fun attach(editText: EditText) {
        val context = editText.context
        val resources = context.resources
        val estimateHeight = resources.getDimensionPixelSize(R.dimen.size_normal) +
                resources.getDimensionPixelSize(R.dimen.space_small) * 2
        val estimateWidth = resources.getDimensionPixelSize(R.dimen.min_menu_width)
        val margin = resources.getDimensionPixelSize(R.dimen.space_normal)

        editText.setOnLongClickListener {
            val menus = getMenus(editText)
            if (menus.isEmpty()) {
                return@setOnLongClickListener false
            }
            dismiss()

            popupWindow = PopupWindow(context)
            popupWindow?.run {
                contentView = createView(context, menus)
                animationStyle = R.style.PopupMenu
                isOutsideTouchable = true
                isFocusable = true
                inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    windowLayoutType = getOverlayType()
                }
                setBackgroundDrawable(null)

                val locations = IntArray(2)
                editText.getLocationOnScreen(locations)
                val x = locations[0] + editText.width - estimateWidth + margin * 2
                val y = locations[1] - editText.height - estimateHeight * menus.size - margin
                showAtLocation(editText, Gravity.NO_GRAVITY, x, y)
            }

            return@setOnLongClickListener true
        }
    }

    private fun getMenus(editText: EditText): List<MenuStrategy> {
        var menu = listOf<MenuStrategy>()

        if (canPaste()) {
            menu += PasteStrategy(clipboardManager, editText)
        }

        if (editText.text.isNotBlank()) {
            menu += CopyStrategy(clipboardManager, editText)
            menu += CutStrategy(clipboardManager, editText)
            menu += ClearStrategy(editText)
        }

        return menu
    }

    private fun canPaste(): Boolean {
        if (!clipboardManager.hasPrimaryClip()) {
            return false
        }

        val primaryClip = clipboardManager.primaryClip

        return primaryClip.itemCount > 0 && !primaryClip.getItemAt(0).text.isNullOrBlank()
    }
}
