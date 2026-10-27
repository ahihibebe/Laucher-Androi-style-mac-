package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Settings
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppInfo
import com.example.model.LunarDate
import com.example.model.WallpaperPresets
import com.example.model.WallpaperTheme
import com.example.util.LunarCalendarHelper
import com.example.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

enum class SettingsCategory(val title: String) {
    CLOCK_AND_LUNAR("Đồng hồ & Lịch âm"),
    DOCK("Thanh Dock (4 ứng dụng)"),
    WALLPAPER("Hình nền"),
    GENERAL("Cài đặt chung"),
    ABOUT("Giới thiệu")
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> = combine(_installedApps, _searchQuery) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            apps.filter { it.label.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Clock and Lunar state
    private val _currentTime = MutableStateFlow(Calendar.getInstance())
    val currentTime: StateFlow<Calendar> = _currentTime.asStateFlow()

    private val _currentLunarDate = MutableStateFlow(LunarCalendarHelper.getTodayLunarDate())
    val currentLunarDate: StateFlow<LunarDate> = _currentLunarDate.asStateFlow()

    // Preferences states
    private val _showClock = MutableStateFlow(prefs.showClock)
    val showClock: StateFlow<Boolean> = _showClock.asStateFlow()

    private val _showLunar = MutableStateFlow(prefs.showLunar)
    val showLunar: StateFlow<Boolean> = _showLunar.asStateFlow()

    private val _is24Hour = MutableStateFlow(prefs.is24HourFormat)
    val is24Hour: StateFlow<Boolean> = _is24Hour.asStateFlow()

    private val _showCanChi = MutableStateFlow(prefs.showCanChi)
    val showCanChi: StateFlow<Boolean> = _showCanChi.asStateFlow()

    private val _currentWallpaper = MutableStateFlow(WallpaperPresets.getById(prefs.wallpaperId))
    val currentWallpaper: StateFlow<WallpaperTheme> = _currentWallpaper.asStateFlow()

    // 4 Dock apps
    private val _dockApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val dockApps: StateFlow<List<AppInfo>> = _dockApps.asStateFlow()

    // UI overlays
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isControlCenterOpen = MutableStateFlow(false)
    val isControlCenterOpen: StateFlow<Boolean> = _isControlCenterOpen.asStateFlow()

    private val _isAboutOpen = MutableStateFlow(false)
    val isAboutOpen: StateFlow<Boolean> = _isAboutOpen.asStateFlow()

    private val _selectedSettingsCategory = MutableStateFlow(SettingsCategory.CLOCK_AND_LUNAR)
    val selectedSettingsCategory: StateFlow<SettingsCategory> = _selectedSettingsCategory.asStateFlow()

    private val _dockSlotPicker = MutableStateFlow<Int?>(null)
    val dockSlotPicker: StateFlow<Int?> = _dockSlotPicker.asStateFlow()

    init {
        loadInstalledApps()
        startClockTicker()
    }

    private fun startClockTicker() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                _currentTime.value = Calendar.getInstance()
                // Update lunar date once a minute or on day change
                _currentLunarDate.value = LunarCalendarHelper.getTodayLunarDate()
                delay(1000)
            }
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            val appList = mutableListOf<AppInfo>()

            for (ri in resolveInfos) {
                val pkgName = ri.activityInfo.packageName
                // Skip our own launcher from the drawer or keep it
                if (pkgName == getApplication<Application>().packageName) continue

                val label = ri.loadLabel(pm).toString()
                val iconDrawable = ri.loadIcon(pm)
                val bitmap = try {
                    iconDrawable.toBitmap(width = 128, height = 128)
                } catch (e: Exception) {
                    null
                }

                appList.add(
                    AppInfo.fromBitmap(
                        packageName = pkgName,
                        label = label,
                        bitmap = bitmap,
                        isSystemApp = (ri.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                    )
                )
            }

            // Fallback system apps if running on fresh device or simulator
            if (appList.isEmpty()) {
                appList.addAll(createFallbackApps())
            }

            // Sort alphabetically
            appList.sortBy { it.label.lowercase() }

            withContext(Dispatchers.Main) {
                _installedApps.value = appList
                refreshDockApps(appList)
            }
        }
    }

    private fun createFallbackApps(): List<AppInfo> {
        return listOf(
            AppInfo("action:phone", "Điện thoại", null, Icons.Default.Phone, true),
            AppInfo("action:messages", "Tin nhắn", null, Icons.Default.Chat, true),
            AppInfo("action:browser", "Safari", null, Icons.Default.Language, true),
            AppInfo("action:camera", "Máy ảnh", null, Icons.Default.CameraAlt, true),
            AppInfo("action:photos", "Ảnh", null, Icons.Default.Photo, true),
            AppInfo("action:files", "Finder", null, Icons.Default.Folder, true),
            AppInfo("action:settings", "Cài đặt", null, Icons.Default.Settings, true)
        )
    }

    private fun refreshDockApps(allApps: List<AppInfo>) {
        val dockList = mutableListOf<AppInfo>()
        for (slot in 0..3) {
            val pkg = prefs.getDockPackage(slot)
            val matchedApp = allApps.find { it.packageName == pkg }
            if (matchedApp != null) {
                dockList.add(matchedApp)
            } else {
                // Default fallback slot apps
                val fallback = when (slot) {
                    0 -> allApps.find { it.label.contains("thoại", true) || it.label.contains("phone", true) }
                        ?: AppInfo("action:phone", "Điện thoại", null, Icons.Default.Phone, true)
                    1 -> allApps.find { it.label.contains("nhắn", true) || it.label.contains("message", true) }
                        ?: AppInfo("action:messages", "Tin nhắn", null, Icons.Default.Chat, true)
                    2 -> allApps.find { it.label.contains("chrome", true) || it.label.contains("safari", true) || it.label.contains("duyệt", true) }
                        ?: AppInfo("action:browser", "Safari", null, Icons.Default.Language, true)
                    3 -> allApps.find { it.label.contains("ảnh", true) || it.label.contains("camera", true) }
                        ?: AppInfo("action:camera", "Máy ảnh", null, Icons.Default.CameraAlt, true)
                    else -> allApps.getOrNull(slot) ?: AppInfo("action:app", "Ứng dụng $slot", null, Icons.Default.Folder)
                }
                dockList.add(fallback)
            }
        }
        _dockApps.value = dockList
    }

    fun launchApp(context: Context, app: AppInfo) {
        try {
            when (app.packageName) {
                "action:phone" -> {
                    val intent = Intent(Intent.ACTION_DIAL)
                    context.startActivity(intent)
                }
                "action:messages" -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:")
                    }
                    context.startActivity(intent)
                }
                "action:browser" -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
                    context.startActivity(intent)
                }
                "action:camera" -> {
                    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
                    context.startActivity(intent)
                }
                "action:photos" -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        type = "image/*"
                    }
                    context.startActivity(intent)
                }
                "action:settings" -> {
                    openSettingsDialog()
                }
                "action:files" -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*"
                    }
                    context.startActivity(intent)
                }
                else -> {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    } else {
                        // Try viewing
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            setPackage(app.packageName)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        } catch (e: Exception) {
            // Graceful fallback
        }
    }

    fun openDefaultLauncherSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                context.startActivity(intent)
            } else {
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                context.startActivity(intent)
            } catch (e2: Exception) {
                // Ignore
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleClock(show: Boolean) {
        _showClock.value = show
        prefs.showClock = show
    }

    fun toggleLunar(show: Boolean) {
        _showLunar.value = show
        prefs.showLunar = show
    }

    fun toggle24Hour(is24: Boolean) {
        _is24Hour.value = is24
        prefs.is24HourFormat = is24
    }

    fun toggleCanChi(show: Boolean) {
        _showCanChi.value = show
        prefs.showCanChi = show
    }

    fun setWallpaper(wallpaper: WallpaperTheme) {
        _currentWallpaper.value = wallpaper
        prefs.wallpaperId = wallpaper.id
    }

    fun openDockSlotPicker(slot: Int) {
        _dockSlotPicker.value = slot
    }

    fun closeDockSlotPicker() {
        _dockSlotPicker.value = null
    }

    fun assignAppToDockSlot(slot: Int, app: AppInfo) {
        prefs.setDockPackage(slot, app.packageName)
        val current = _dockApps.value.toMutableList()
        if (slot in current.indices) {
            current[slot] = app
            _dockApps.value = current
        }
        _dockSlotPicker.value = null
    }

    fun openSettingsDialog(category: SettingsCategory = SettingsCategory.CLOCK_AND_LUNAR) {
        _selectedSettingsCategory.value = category
        _isSettingsOpen.value = true
    }

    fun closeSettingsDialog() {
        _isSettingsOpen.value = false
    }

    fun selectSettingsCategory(category: SettingsCategory) {
        _selectedSettingsCategory.value = category
    }

    fun toggleControlCenter() {
        _isControlCenterOpen.value = !_isControlCenterOpen.value
    }

    fun closeControlCenter() {
        _isControlCenterOpen.value = false
    }

    fun openAboutDialog() {
        _isAboutOpen.value = true
    }

    fun closeAboutDialog() {
        _isAboutOpen.value = false
    }
}
