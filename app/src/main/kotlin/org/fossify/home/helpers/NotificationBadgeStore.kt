package org.fossify.home.helpers

/**
 * Holds the latest per-package notification counts computed by
 * NotificationBadgeListener, and notifies whoever's currently drawing badges
 * (MainActivity/HomeScreenGrid) when they change.
 *
 * NotificationListenerService runs in this app's own process by default (no
 * :process attribute on the service), so a plain in-memory map is enough here -
 * no IPC, no persistence needed. Counts are naturally cleared to empty whenever
 * the service isn't running (permission not granted, or user just hasn't
 * launched the launcher process yet since boot), which is also the correct
 * "no badges" state for a feature that only works with permission granted.
 */
object NotificationBadgeStore {
    @Volatile
    private var counts: Map<String, Int> = emptyMap()

    var onCountsChanged: (() -> Unit)? = null

    fun getCount(packageName: String): Int = counts[packageName] ?: 0

    fun updateCounts(newCounts: Map<String, Int>) {
        counts = newCounts
        onCountsChanged?.invoke()
    }
}
