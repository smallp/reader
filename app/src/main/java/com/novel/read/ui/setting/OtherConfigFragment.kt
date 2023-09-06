package com.novel.read.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.BasePreferenceFragment
import com.novel.read.constant.EventBus
import com.novel.read.constant.PreferKey
import com.novel.read.help.AppConfig
import com.novel.read.lib.ATH
import com.novel.read.utils.LanguageUtils
import com.novel.read.utils.ext.postEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


class OtherConfigFragment : BasePreferenceFragment(), CoroutineScope by MainScope(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_config_other)
        findPreference<Preference>("check_update")?.summary =
            "${getString(R.string.version)} ${App.versionName}"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
        ATH.applyEdgeEffectColor(listView)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferKey.language -> {
                val lg = sharedPreferences?.all?.get("language")
                if (lg == "zh") {
                    AppConfig.chineseConverterType = 1
                } else if (lg == "tw") {
                    AppConfig.chineseConverterType = 2
                }
                LanguageUtils.setConfiguration(App.INSTANCE)
                postEvent(EventBus.RECREATE, "")
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

}