package com.novel.read.ui.main.my

import android.os.Bundle
import android.view.View
import com.novel.read.R
import com.novel.read.base.BaseFragment
import com.novel.read.databinding.FragmentMyBinding
import com.novel.read.ui.setting.SettingActivity
import com.novel.read.utils.ext.startActivity
import com.novel.read.utils.viewbindingdelegate.viewBinding


class MyFragment : BaseFragment(R.layout.fragment_my) {

    private val binding by viewBinding(FragmentMyBinding::bind)

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setSupportToolbar(binding.titleBar.toolbar)
        binding.tvSetting.setOnClickListener {
            startActivity<SettingActivity>()
        }
    }

}