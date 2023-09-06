package com.novel.read.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.novel.read.R
import com.novel.read.ui.MainActivity
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.ext.getPrefBoolean
import org.jetbrains.anko.startActivity

class WelcomeActivity : AppCompatActivity() {
    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
        } else {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        if (!flag) {
            startActivity<MainActivity>()
            if (getPrefBoolean(R.string.pk_default_read)) {
                startActivity<ReadBookActivity>()
            }
            finish()
        }
    }

    override fun onDestroy() {
        flag = true
        super.onDestroy()
    }

}