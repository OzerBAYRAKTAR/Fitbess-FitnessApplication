package com.bayraktar.healthybackandneck

import android.os.Build
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.utils.fitContentToSystemBars
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WindowInsetsTest {
    @Test fun barsAreAppliedOnceAndImeInsetsRemainAvailable() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val root = View(activity)
                root.setPadding(2, 3, 4, 5)
                activity.fitContentToSystemBars(root)
                val types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                val input = WindowInsetsCompat.Builder()
                    .setInsets(types, Insets.of(12, 80, 14, 64))
                    .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, 400))
                    .build()
                val result = ViewCompat.dispatchApplyWindowInsets(root, input)
                ViewCompat.dispatchApplyWindowInsets(root, input)
                if (Build.VERSION.SDK_INT >= 35) {
                    assertEquals(14, root.paddingLeft)
                    assertEquals(83, root.paddingTop)
                    assertEquals(18, root.paddingRight)
                    assertEquals(69, root.paddingBottom)
                    assertEquals(Insets.NONE, result.getInsets(types))
                    assertEquals(400, result.getInsets(WindowInsetsCompat.Type.ime()).bottom)
                } else {
                    assertEquals(2, root.paddingLeft)
                    assertEquals(3, root.paddingTop)
                    assertEquals(4, root.paddingRight)
                    assertEquals(5, root.paddingBottom)
                }
            }
        }
    }
}
