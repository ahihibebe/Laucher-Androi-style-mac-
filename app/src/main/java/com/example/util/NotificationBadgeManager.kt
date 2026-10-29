package com.example.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationBadgeManager {

    private val _notificationCounts = MutableStateFlow<Map<String, Int>>(
        mapOf(
            "action:phone" to 1,
            "action:messages" to 2
        )
    )
    val notificationCounts: StateFlow<Map<String, Int>> = _notificationCounts.asStateFlow()

    fun updateCounts(counts: Map<String, Int>) {
        val merged = _notificationCounts.value.toMutableMap()
        merged.putAll(counts)
        _notificationCounts.value = merged
    }

    fun setAllCounts(counts: Map<String, Int>) {
        _notificationCounts.value = counts
    }

    fun getBadgeCount(packageName: String): Int {
        val map = _notificationCounts.value
        val direct = map[packageName]
        if (direct != null && direct > 0) return direct

        // Common package mappings
        if (packageName == "action:messages" || packageName.contains("message", true) || packageName.contains("sms", true)) {
            return map["action:messages"] ?: map.entries.find { it.key.contains("message", true) || it.key.contains("sms", true) }?.value ?: 0
        }
        if (packageName == "action:phone" || packageName.contains("dialer", true) || packageName.contains("phone", true)) {
            return map["action:phone"] ?: map.entries.find { it.key.contains("dialer", true) || it.key.contains("phone", true) }?.value ?: 0
        }
        return 0
    }
}
