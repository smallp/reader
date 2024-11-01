package com.novel.read.help

import android.annotation.SuppressLint
import android.content.Context
import com.novel.read.App
import com.novel.read.R
import com.novel.read.constant.PreferKey
import com.novel.read.utils.ext.*

object AppConfig {

    var isEInkMode: Boolean = false

    fun isNightTheme(context: Context): Boolean {
        return when (context.getPrefString(PreferKey.themeMode, "0")) {
            "1" -> false
            "2" -> true
            "3" -> false
            else -> context.sysIsDarkMode()
        }
    }

    var isNightTheme: Boolean
        get() = isNightTheme(App.INSTANCE)
        set(value) {
            if (isNightTheme != value) {
                if (value) {
                    App.INSTANCE.putPrefString(PreferKey.themeMode, "2")
                } else {
                    App.INSTANCE.putPrefString(PreferKey.themeMode, "1")
                }
            }
        }

    fun upEInkMode() {
        isEInkMode = App.INSTANCE.getPrefString(PreferKey.themeMode) == "3"
    }

    var isTransparentStatusBar: Boolean
        get() = App.INSTANCE.getPrefBoolean(PreferKey.transparentStatusBar,true)
        set(value) {
            App.INSTANCE.putPrefBoolean(PreferKey.transparentStatusBar, value)
        }

    val requestedDirection: String?
        get() = App.INSTANCE.getPrefString(R.string.pk_requested_direction)

    var ttsSpeechRate: Int
        get() = App.INSTANCE.getPrefInt(PreferKey.ttsSpeechRate, 5)
        set(value) {
            App.INSTANCE.putPrefInt(PreferKey.ttsSpeechRate, value)
        }

    var chineseConverterType: Int
        get() = App.INSTANCE.getPrefInt(PreferKey.chineseConverterType,1)
        set(value) {
            App.INSTANCE.putPrefInt(PreferKey.chineseConverterType, value)
        }

    var systemTypefaces: Int
        get() = App.INSTANCE.getPrefInt(PreferKey.systemTypefaces)
        set(value) {
            App.INSTANCE.putPrefInt(PreferKey.systemTypefaces, value)
        }

    var elevation: Int
        @SuppressLint("PrivateResource")
        get() = App.INSTANCE.getPrefInt(
            PreferKey.barElevation,
//            App.INSTANCE.resources.getDimension(R.dimen.design_appbar_elevation).toInt()
           0.toInt()
        )
        set(value) {
            App.INSTANCE.putPrefInt(PreferKey.barElevation, value)
        }


    val readBodyToLh: Boolean get() = App.INSTANCE.getPrefBoolean(PreferKey.readBodyToLh, true)

}

