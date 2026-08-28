package org.fossify.home.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.fossify.home.helpers.NotificationBadgeStore

/**
 * Requires the user to grant "Notification access" in system settings - the
 * most privacy-sensitive permission Android has, since it hands this launcher
 * full read access to every notification's content, not just a count. That
 * trade-off is explained to the user in Settings before this is ever requested
 * (see SettingsActivity.setupNotificationBadges()); this service only ever
 * reads notifications, never their content beyond what's needed to compute a
 * per-package count, and never stores or transmits notification content
 * anywhere.
 *
 * FLAG_ONGOING_EVENT notifications (persistent foreground-service notifications
 * like a music player or a running download) are excluded, since counting
 * those would put a permanent badge on unrelated apps. FLAG_GROUP_SUMMARY
 * notifications are excluded too, since counting both the summary and its
 * child notifications would double-count.
 */
class NotificationBadgeListener : NotificationListenerService() {
    override fun onListenerConnected() {
        super.onListenerConnected()
        recomputeCounts()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        recomputeCounts()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        recomputeCounts()
    }

    private fun recomputeCounts() {
        try {
            val counts = activeNotifications
                .filter { sbn ->
                    val flags = sbn.notification.flags
                    (flags and Notification.FLAG_ONGOING_EVENT) == 0 &&
                        (flags and Notification.FLAG_GROUP_SUMMARY) == 0
                }
                .groupingBy { it.packageName }
                .eachCount()
            NotificationBadgeStore.updateCounts(counts)
        } catch (e: Exception) {
            // getActiveNotifications() can throw if the listener connection is in a
            // bad state; leaving the previous counts in place is safer than crashing
            // the launcher's notification listener process over a badge count.
        }
    }
}
