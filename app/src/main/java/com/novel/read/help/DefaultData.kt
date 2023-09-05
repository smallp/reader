package com.novel.read.help

import com.novel.read.App
import com.novel.read.utils.ext.GSON
import com.novel.read.utils.ext.fromJsonArray
import java.io.File

object DefaultData {
    val defaultReadConfigs by lazy {
        val json = String(
            App.INSTANCE.assets.open("defaultData${File.separator}${ReadBookConfig.configFileName}")
                .readBytes()
        )
        GSON.fromJsonArray<ReadBookConfig.Config>(json)!!
    }

    val defaultThemeConfigs by lazy {
        val json = String(
            App.INSTANCE.assets.open("defaultData${File.separator}${ThemeConfig.configFileName}")
                .readBytes()
        )
        GSON.fromJsonArray<ThemeConfig.Config>(json)!!
    }
}