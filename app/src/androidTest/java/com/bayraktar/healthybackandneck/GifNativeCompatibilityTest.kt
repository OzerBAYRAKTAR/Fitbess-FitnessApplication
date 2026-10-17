package com.bayraktar.healthybackandneck

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import pl.droidsonroids.gif.GifDrawable

@RunWith(AndroidJUnit4::class)
class GifNativeCompatibilityTest {
    @Test fun bundledExerciseLoadsNativeDecoderAndRendersDistinctFrames() {
        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
        val gif = GifDrawable(resources, R.drawable.e100_b11_stndng_bndover)
        try {
            gif.stop()
            assertTrue("Exercise must remain animated", gif.numberOfFrames > 1)
            val first = gif.seekToFrameAndGet(0)
            val next = gif.seekToFrameAndGet(1)
            val rewind = gif.seekToFrameAndGet(0)
            try {
                assertTrue(first.width > 0 && first.height > 0)
                assertFalse("Native decoding must advance the exercise image", first.sameAs(next))
                assertTrue("Rewinding must reproduce the original frame", first.sameAs(rewind))
            } finally {
                first.recycle()
                next.recycle()
                rewind.recycle()
            }
        } finally {
            gif.recycle()
        }
    }
}
