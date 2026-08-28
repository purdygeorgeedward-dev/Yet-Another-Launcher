package org.fossify.home.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import org.fossify.home.extensions.homeScreenGridItemsDB
import org.fossify.home.models.HomeScreenGridItem
import org.json.JSONArray
import org.json.JSONObject

/**
 * Exports/imports the icon and folder layout of the home screen as JSON.
 *
 * Deliberately scoped to ITEM_TYPE_ICON and ITEM_TYPE_FOLDER only. Widgets and
 * pinned shortcuts are skipped on both export and import: a widget_id only means
 * something on the device (and AppWidgetHost) that created it, and a shortcut_id
 * likewise only means something to the app that pinned it - blindly writing either
 * back on a different device (or after a factory reset) would bind to garbage, not
 * restore the widget/shortcut. Positions for icons/folders are exactly what's
 * requested in https://github.com/FossifyOrg/Launcher/issues/94 ("Layout backup
 * (import/export)", moving/damaged/corrupted-phone use case) and are safe to
 * restore blind because they're just coordinates plus a package+activity reference
 * we can validate against what's actually installed before inserting anything.
 */
object LayoutBackupHelper {
    private const val BACKUP_VERSION = 1
    const val BACKUP_MIME_TYPE = "application/json"

    data class ImportResult(
        val restoredCount: Int,
        val skippedMissingAppCount: Int
    )

    data class ExportResult(
        val json: String,
        val exportedCount: Int,
        val skippedCount: Int
    )

    fun buildBackupFilename(): String {
        val timestamp = System.currentTimeMillis()
        return "launcher-layout-backup-$timestamp.json"
    }

    fun exportLayout(context: Context): ExportResult {
        val allItems = context.homeScreenGridItemsDB.getAllItems()
        val exportable = allItems.filter { it.type == ITEM_TYPE_ICON || it.type == ITEM_TYPE_FOLDER }
        val skippedCount = allItems.size - exportable.size

        val itemsArray = JSONArray()
        exportable.forEach { item ->
            itemsArray.put(
                JSONObject().apply {
                    put("tempId", item.id ?: -1L)
                    put("parentTempId", item.parentId ?: JSONObject.NULL)
                    put("left", item.left)
                    put("top", item.top)
                    put("right", item.right)
                    put("bottom", item.bottom)
                    put("page", item.page)
                    put("packageName", item.packageName)
                    put("activityName", item.activityName)
                    put("title", item.title)
                    put("type", item.type)
                    put("className", item.className)
                    put("docked", item.docked)
                }
            )
        }

        val root = JSONObject().apply {
            put("version", BACKUP_VERSION)
            put("items", itemsArray)
        }

        return ExportResult(root.toString(2), exportable.size, skippedCount)
    }

    fun importLayout(context: Context, json: String): ImportResult {
        val root = JSONObject(json)
        val itemsArray = root.optJSONArray("items") ?: JSONArray()

        val installedIdentifiers = getInstalledLauncherIdentifiers(context)
        val dao = context.homeScreenGridItemsDB

        // old tempId (from the file) -> newly inserted row's real DB id
        val idMap = HashMap<Long, Long>()
        val parsedItems = ArrayList<Triple<Long, Long?, HomeScreenGridItem>>()

        for (i in 0 until itemsArray.length()) {
            val obj = itemsArray.getJSONObject(i)
            val tempId = obj.optLong("tempId", -1L)
            val parentTempId = if (obj.isNull("parentTempId")) null else obj.optLong("parentTempId")
            val item = HomeScreenGridItem(
                id = null,
                left = obj.optInt("left", -1),
                top = obj.optInt("top", -1),
                right = obj.optInt("right", -1),
                bottom = obj.optInt("bottom", -1),
                page = obj.optInt("page", 0),
                packageName = obj.optString("packageName"),
                activityName = obj.optString("activityName"),
                title = obj.optString("title"),
                type = obj.optInt("type", ITEM_TYPE_ICON),
                className = obj.optString("className"),
                widgetId = -1,
                shortcutId = "",
                docked = obj.optBoolean("docked", false)
            )
            parsedItems.add(Triple(tempId, parentTempId, item))
        }

        // folders first, so child icons can resolve a real parent id
        var restoredCount = 0
        var skippedMissingAppCount = 0

        parsedItems.filter { it.third.type == ITEM_TYPE_FOLDER }.forEach { (tempId, _, item) ->
            val newId = dao.insert(item)
            idMap[tempId] = newId
            restoredCount++
        }

        parsedItems.filter { it.third.type == ITEM_TYPE_ICON }.forEach { (_, parentTempId, item) ->
            val identifier = "${item.packageName}/${item.activityName}"
            if (!installedIdentifiers.contains(identifier)) {
                skippedMissingAppCount++
                return@forEach
            }

            item.parentId = parentTempId?.let { idMap[it] }
            dao.insert(item)
            restoredCount++
        }

        return ImportResult(restoredCount, skippedMissingAppCount)
    }

    // Same "$packageName/$activityName" identifier format as AppLauncher.getLauncherIdentifier()
    // and HiddenIcon.getIconIdentifier(), and the same queryIntentActivities() approach
    // MainActivity.getAllAppLaunchers() already uses to enumerate what's actually launchable.
    private fun getInstalledLauncherIdentifiers(context: Context): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return context.packageManager.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED)
            .map { "${it.activityInfo.applicationInfo.packageName}/${it.activityInfo.name}" }
            .toSet()
    }

    fun createExportIntent(filename: String) = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        type = BACKUP_MIME_TYPE
        addCategory(Intent.CATEGORY_OPENABLE)
        putExtra(Intent.EXTRA_TITLE, filename)
    }

    fun createImportIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        type = BACKUP_MIME_TYPE
        addCategory(Intent.CATEGORY_OPENABLE)
    }
}
