package org.fossify.home.helpers

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

/**
 * Simulation filters for the three common types of color blindness, applied to
 * icon drawables so colors that would normally be hard to distinguish are
 * remapped into a range the user can actually tell apart.
 *
 * These are the widely-used simplified simulation matrices (the same ones behind
 * the popular "Colorblindly" browser extension and most Coblis-style simulators) -
 * an approximation of how each condition affects color perception, not a
 * scientifically exact model. Matrices are cached per mode since building a
 * ColorMatrixColorFilter isn't free and this gets called on every icon draw.
 */
object ColorBlindFilters {
    private val filterCache = HashMap<Int, ColorMatrixColorFilter?>()

    fun getColorFilter(mode: Int): ColorMatrixColorFilter? {
        if (mode == COLOR_BLIND_NONE) {
            return null
        }

        return filterCache.getOrPut(mode) {
            val matrix = when (mode) {
                COLOR_BLIND_PROTANOPIA -> floatArrayOf(
                    0.567f, 0.433f, 0f, 0f, 0f,
                    0.558f, 0.442f, 0f, 0f, 0f,
                    0f, 0.242f, 0.758f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )

                COLOR_BLIND_DEUTERANOPIA -> floatArrayOf(
                    0.625f, 0.375f, 0f, 0f, 0f,
                    0.7f, 0.3f, 0f, 0f, 0f,
                    0f, 0.3f, 0.7f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )

                COLOR_BLIND_TRITANOPIA -> floatArrayOf(
                    0.95f, 0.05f, 0f, 0f, 0f,
                    0f, 0.433f, 0.567f, 0f, 0f,
                    0f, 0.475f, 0.525f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )

                else -> null
            }
            matrix?.let { ColorMatrixColorFilter(ColorMatrix(it)) }
        }
    }
}
