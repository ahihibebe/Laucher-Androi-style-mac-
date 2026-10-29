package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.util.NotificationBadgeManager

class LauncherNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        refreshActiveNotifications()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        refreshActiveNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        refreshActiveNotifications()
    }

    private fun refreshActiveNotifications() {
        try {
            val active = activeNotifications ?: return
            val counts = mutableMapOf<String, Int>()
            for (sbn in active) {
                if (sbn.isOngoing) continue
                val pkg = sbn.packageName ?: continue
                counts[pkg] = (counts[pkg] ?: 0) + 1
            }
            if (counts.isNotEmpty()) {
                NotificationBadgeManager.setAllCounts(counts)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
