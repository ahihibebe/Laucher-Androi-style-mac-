package com.example.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("macos_launcher_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SHOW_CLOCK = "show_clock"
        private const val KEY_SHOW_LUNAR = "show_lunar"
        private const val KEY_SHOW_MARKET_PRICES = "show_market_prices"
        private const val KEY_24_HOUR = "is_24_hour"
        private const val KEY_SHOW_CAN_CHI = "show_can_chi"
        private const val KEY_WALLPAPER = "wallpaper_id"
        private const val KEY_CUSTOM_WALLPAPER_URI = "custom_wallpaper_uri"
        private const val KEY_HIDDEN_PACKAGES = "hidden_packages"
        private const val KEY_DOCK_APP_0 = "dock_app_0"
        private const val KEY_DOCK_APP_1 = "dock_app_1"
        private const val KEY_DOCK_APP_2 = "dock_app_2"
        private const val KEY_DOCK_APP_3 = "dock_app_3"
        private const val KEY_SWIPE_RIGHT_LAUNCHPAD = "swipe_right_launchpad"
        private const val KEY_SWIPE_UP_LAUNCHPAD = "swipe_up_launchpad"
        private const val KEY_SWIPE_DOWN_NOTIFICATIONS = "swipe_down_notifications"
        private const val KEY_SHOW_LAUNCHPAD_GUIDE = "show_launchpad_guide"
        private const val KEY_APP_ORDER = "app_order_list"
    }

    var showClock: Boolean
        get() = prefs.getBoolean(KEY_SHOW_CLOCK, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_CLOCK, value).apply()

    var showLunar: Boolean
        get() = prefs.getBoolean(KEY_SHOW_LUNAR, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_LUNAR, value).apply()

    var showMarketPrices: Boolean
        get() = prefs.getBoolean(KEY_SHOW_MARKET_PRICES, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_MARKET_PRICES, value).apply()

    var is24HourFormat: Boolean
        get() = prefs.getBoolean(KEY_24_HOUR, true)
        set(value) = prefs.edit().putBoolean(KEY_24_HOUR, value).apply()

    var showCanChi: Boolean
        get() = prefs.getBoolean(KEY_SHOW_CAN_CHI, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_CAN_CHI, value).apply()

    var wallpaperId: String
        get() = prefs.getString(KEY_WALLPAPER, "sequoia") ?: "sequoia"
        set(value) = prefs.edit().putString(KEY_WALLPAPER, value).apply()

    var customWallpaperUri: String?
        get() = prefs.getString(KEY_CUSTOM_WALLPAPER_URI, null)
        set(value) = prefs.edit().putString(KEY_CUSTOM_WALLPAPER_URI, value).apply()

    var hiddenPackages: Set<String>
        get() = prefs.getStringSet(KEY_HIDDEN_PACKAGES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_HIDDEN_PACKAGES, value).apply()

    var swipeRightForLaunchpad: Boolean
        get() = prefs.getBoolean(KEY_SWIPE_RIGHT_LAUNCHPAD, true)
        set(value) = prefs.edit().putBoolean(KEY_SWIPE_RIGHT_LAUNCHPAD, value).apply()

    var swipeUpForLaunchpad: Boolean
        get() = prefs.getBoolean(KEY_SWIPE_UP_LAUNCHPAD, true)
        set(value) = prefs.edit().putBoolean(KEY_SWIPE_UP_LAUNCHPAD, value).apply()

    var swipeDownForNotifications: Boolean
        get() = prefs.getBoolean(KEY_SWIPE_DOWN_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_SWIPE_DOWN_NOTIFICATIONS, value).apply()

    var showLaunchpadGuide: Boolean
        get() = prefs.getBoolean(KEY_SHOW_LAUNCHPAD_GUIDE, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_LAUNCHPAD_GUIDE, value).apply()

    var appOrder: List<String>
        get() {
            val raw = prefs.getString(KEY_APP_ORDER, "") ?: ""
            return if (raw.isBlank()) emptyList() else raw.split(",")
        }
        set(value) {
            prefs.edit().putString(KEY_APP_ORDER, value.joinToString(",")).apply()
        }

    fun hidePackage(pkg: String) {
        val current = hiddenPackages.toMutableSet()
        current.add(pkg)
        hiddenPackages = current
    }

    fun unhideAll() {
        hiddenPackages = emptySet()
    }

    fun getDockPackage(slot: Int): String {
        val defaultPkg = when (slot) {
            0 -> "action:phone"
            1 -> "action:messages"
            2 -> "action:browser"
            3 -> "action:camera"
            else -> ""
        }
        val key = when (slot) {
            0 -> KEY_DOCK_APP_0
            1 -> KEY_DOCK_APP_1
            2 -> KEY_DOCK_APP_2
            3 -> KEY_DOCK_APP_3
            else -> return defaultPkg
        }
        return prefs.getString(key, defaultPkg) ?: defaultPkg
    }

    fun setDockPackage(slot: Int, packageName: String) {
        val key = when (slot) {
            0 -> KEY_DOCK_APP_0
            1 -> KEY_DOCK_APP_1
            2 -> KEY_DOCK_APP_2
            3 -> KEY_DOCK_APP_3
            else -> return
        }
        prefs.edit().putString(key, packageName).apply()
    }
}
