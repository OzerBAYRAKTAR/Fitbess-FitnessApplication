package com.bayraktar.healthybackandneck.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bayraktar.healthybackandneck.R

object ProfileIntents {
    fun store(context: Context, web: Boolean = false): Intent {
        val url = if (web) "https://play.google.com/store/apps/details?id=" else "market://details?id="
        return Intent(Intent.ACTION_VIEW, Uri.parse(url + context.packageName))
    }
    fun share(context: Context) = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message, context.getString(R.string.app_name), "https://play.google.com/store/apps/details?id=${context.packageName}"))
    }
}
