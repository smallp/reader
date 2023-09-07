package com.novel.read.help

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle

object IntentHelp {
    inline fun <reified T> servicePendingIntent(
        context: Context,
        action: String,
        bundle: Bundle? = null
    ): PendingIntent? {
        val intent = Intent(context, T::class.java)
        intent.action = action
        bundle?.let {
            intent.putExtras(bundle)
        }
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    inline fun <reified T> activityPendingIntent(
        context: Context,
        action: String,
        bundle: Bundle? = null
    ): PendingIntent? {
        val intent = Intent(context, T::class.java)
        intent.action = action
        bundle?.let {
            intent.putExtras(bundle)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}