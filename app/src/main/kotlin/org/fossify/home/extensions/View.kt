package org.fossify.home.extensions

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.TypedValue
import android.view.RoundedCorner.POSITION_TOP_LEFT
import android.view.RoundedCorner.POSITION_TOP_RIGHT
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toDrawable
import org.fossify.commons.R
import org.fossify.commons.extensions.applyColorFilter
import org.fossify.commons.extensions.getProperBackgroundColor
import org.fossify.commons.helpers.isSPlus

fun View.animateScale(
    from: Float,
    to: Float,
    duration: Long,
) = animate()
    .scaleX(to)
    .scaleY(to)
    .setDuration(duration)
    .setInterpolator(AccelerateDecelerateInterpolator())
    .withStartAction {
        scaleX = from
        scaleY = from
    }

// Glassmorphism style constants. This is a translucency + highlight-border treatment,
// not a real-time backdrop blur: genuinely blurring the wallpaper/home screen content
// sitting behind this view would mean capturing it into a bitmap (via WallpaperManager
// or a PixelCopy of the window) and running a blur pass on it every time it changes,
// which brings in permission and performance considerations well beyond this pass.
// Because this view is drawn on top of the home screen in the same window, the
// translucency alone still lets the real content show through - just sharp, not blurred.
private const val GLASS_BACKGROUND_ALPHA = 235
private const val GLASS_STROKE_ALPHA = 60
private const val GLASS_STROKE_WIDTH_DP = 1f
private const val GLASS_CORNER_RADIUS_DP = 24f

private fun View.dpToPx(dp: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
)

private fun View.buildGlassBackground(baseColor: Int): GradientDrawable {
    val translucentFill = ColorUtils.setAlphaComponent(baseColor, GLASS_BACKGROUND_ALPHA)
    val highlightStroke = ColorUtils.setAlphaComponent(Color.WHITE, GLASS_STROKE_ALPHA)
    return GradientDrawable().apply {
        setColor(translucentFill)
        setStroke(dpToPx(GLASS_STROKE_WIDTH_DP).toInt(), highlightStroke)
        cornerRadius = dpToPx(GLASS_CORNER_RADIUS_DP)
    }
}

fun View.setupDrawerBackground() {
    val backgroundColor = context.getProperBackgroundColor()
    val useGlassStyle = context.config.glassmorphismUi

    background = if (useGlassStyle) {
        buildGlassBackground(backgroundColor)
    } else {
        backgroundColor.toDrawable()
    }

    val insets = rootWindowInsets
    if (isSPlus() && insets != null) {
        val topRightCorner = insets.getRoundedCorner(POSITION_TOP_RIGHT)?.radius ?: 0
        val topLeftCorner = insets.getRoundedCorner(POSITION_TOP_LEFT)?.radius ?: 0
        if (topRightCorner > 0 && topLeftCorner > 0) {
            background = if (useGlassStyle) {
                buildGlassBackground(backgroundColor)
            } else {
                ResourcesCompat.getDrawable(
                    context.resources, R.drawable.bottom_sheet_bg, context.theme
                ).apply {
                    (this as LayerDrawable)
                        .findDrawableByLayerId(R.id.bottom_sheet_background)
                        .applyColorFilter(backgroundColor)
                }
            }
        }
    }
}