package org.fossify.home.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import org.xmlpull.v1.XmlPullParser

data class InstalledIconPack(val packageName: String, val label: String)

/**
 * Support for third-party icon pack apps (Lawnicons, Delta Icons, Whicons,
 * etc.) that implement the de facto "org.adw.launcher.THEMES" convention: an
 * appfilter.xml resource mapping app components to the pack's own drawable
 * names. This launcher never bundles or generates icon artwork itself - it
 * only reads an icon pack the user has separately installed. Samsung's own
 * Galaxy Themes icons are a different, proprietary system tied to One UI and
 * aren't exposed through this or any standard Android API.
 *
 * Every lookup here is wrapped so a missing pack, a malformed appfilter.xml,
 * or an app that isn't mapped in the pack all fail safe to null - callers are
 * expected to fall back to their existing default icon logic whenever this
 * returns nothing, which is the normal case for most apps even with a pack
 * selected (most packs don't cover every app on a device).
 */
object IconPackHelper {

    private const val ICON_PACK_INTENT_ACTION = "org.adw.launcher.THEMES"
    private const val APPFILTER_RES_NAME = "appfilter"

    // Parsing an icon pack's appfilter.xml on every single icon draw would be
    // wasteful - it's a few hundred <item> entries read from another app's
    // resources. Cache the parsed map for whichever pack is currently
    // selected; re-parses only when the user switches packs.
    @Volatile
    private var cachedPackPackage: String? = null

    @Volatile
    private var cachedMap: Map<String, String> = emptyMap()

    /** Icon pack apps installed on the device, sorted by display name. */
    fun getInstalledIconPacks(context: Context): List<InstalledIconPack> {
        return try {
            val packageManager = context.packageManager
            val intent = Intent(ICON_PACK_INTENT_ACTION)
            packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)
                .mapNotNull { resolveInfo ->
                    val packageName = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
                    val label = resolveInfo.loadLabel(packageManager)?.toString() ?: packageName
                    InstalledIconPack(packageName, label)
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * The icon pack's replacement drawable for the given app, or null if no
     * pack is selected, the pack's appfilter.xml couldn't be read, or this
     * specific app isn't mapped by the pack. [activityName], when available,
     * gives an exact component match; without it, lookup falls back to the
     * first drawable the pack maps for that package.
     */
    fun getIconPackDrawable(
        context: Context,
        packPackage: String,
        appPackageName: String,
        activityName: String? = null
    ): Drawable? {
        if (packPackage.isEmpty()) {
            return null
        }

        val map = getOrParseMap(context, packPackage) ?: return null
        val drawableName = (
            if (activityName != null) {
                map["ComponentInfo{$appPackageName/$activityName}"]
            } else {
                null
            }
            ) ?: map[appPackageName] ?: return null

        return try {
            val packResources = context.packageManager.getResourcesForApplication(packPackage)
            val resId = packResources.getIdentifier(drawableName, "drawable", packPackage)
            if (resId == 0) {
                null
            } else {
                ResourcesCompat.getDrawable(packResources, resId, null)
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Call when the user switches or clears the selected icon pack. */
    fun clearCache() {
        synchronized(this) {
            cachedPackPackage = null
            cachedMap = emptyMap()
        }
    }

    private fun getOrParseMap(context: Context, packPackage: String): Map<String, String>? {
        if (cachedPackPackage == packPackage) {
            return cachedMap
        }

        val parsed = parseAppFilter(context, packPackage) ?: return null
        synchronized(this) {
            cachedPackPackage = packPackage
            cachedMap = parsed
        }
        return parsed
    }

    private fun parseAppFilter(context: Context, packPackage: String): Map<String, String>? {
        return try {
            val packResources = context.packageManager.getResourcesForApplication(packPackage)
            val xmlResId = packResources.getIdentifier(APPFILTER_RES_NAME, "xml", packPackage)
            if (xmlResId == 0) {
                return null
            }

            val parser = packResources.getXml(xmlResId)
            val map = mutableMapOf<String, String>()
            val componentRegex = Regex("ComponentInfo\\{([^/]+)/")

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                    val component = parser.getAttributeValue(null, "component")
                    val drawable = parser.getAttributeValue(null, "drawable")
                    if (component != null && drawable != null) {
                        map[component] = drawable

                        // Also index by bare package name, first match wins,
                        // for lookups that don't have an activity name to
                        // match exactly against.
                        val matchedPackage = componentRegex.find(component)?.groupValues?.get(1)
                        if (matchedPackage != null && !map.containsKey(matchedPackage)) {
                            map[matchedPackage] = drawable
                        }
                    }
                }
                eventType = parser.next()
            }
            map
        } catch (e: Exception) {
            null
        }
    }
}
