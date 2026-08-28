package org.fossify.home.helpers

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import org.fossify.home.R

/**
 * Resolves the typeface for the user's accessibility font choice.
 *
 * Lexend (res/font/lexend.ttf) and OpenDyslexic (res/font/open_dyslexic.xml, a
 * font-family combining opendyslexic_regular/opendyslexic_bold) are bundled,
 * OFL-licensed TrueType/OpenType fonts - license text is in
 * assets/font_licenses. FONT_DEFAULT falls back to whatever typeface the
 * caller was already using (the commons library's FontHelper.getTypeface).
 */
object AccessibilityFontHelper {
    fun resolve(context: Context, fontChoice: Int, fallback: Typeface?): Typeface? {
        return try {
            when (fontChoice) {
                FONT_LEXEND -> ResourcesCompat.getFont(context, R.font.lexend)
                FONT_OPEN_DYSLEXIC -> ResourcesCompat.getFont(context, R.font.open_dyslexic)
                else -> fallback
            }
        } catch (e: Exception) {
            fallback
        }
    }
}
