package com.bayraktar.healthybackandneck.utils

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/** One inset owner for the legacy XML screens; descendants must not apply bars twice. */
fun Activity.fitContentToSystemBars(root: View) {
    if (Build.VERSION.SDK_INT < 35) return
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val left = root.paddingLeft
    val top = root.paddingTop
    val right = root.paddingRight
    val bottom = root.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
        view.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
        WindowInsetsCompat.Builder(insets)
            .setInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(), Insets.NONE)
            .setInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(), Insets.NONE)
            .build()
    }
    WindowCompat.getInsetsController(window, root).apply {
        // These activities render light XML surfaces, including when the device is in dark mode.
        isAppearanceLightStatusBars = true
        isAppearanceLightNavigationBars = true
    }
    ViewCompat.requestApplyInsets(root)
}
