package com.example.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import com.example.model.MarketPriceReport
import com.example.model.WallpaperPresets
import com.example.model.WallpaperTheme
import com.example.util.LunarCalendarHelper
import com.example.util.MarketPriceRepository
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
    GESTURES("Thao tác vuốt & Cử chỉ"),
    CLOCK_AND_LUNAR("Đồng hồ & Lịch âm"),
    MARKET_PRICES("Giá Cà phê & Vàng"),
    DOCK("Thanh Dock (4 ứng dụng)"),
    WALLPAPER("Hình nền"),
    GENERAL("Cài đặt chung"),
    ABOUT("Giới thiệu")
}

data class LauncherMusicTrack(
    val title: String,
    val artist: String,
    val durationText: String,
    val totalSeconds: Int = 240,
    val coverGradientStart: Long = 0xFF8B5CF6,
    val coverGradientEnd: Long = 0xFFEC4899
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val marketRepo = MarketPriceRepository()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _hiddenPackages = MutableStateFlow(prefs.hiddenPackages)
    val hiddenPackages: StateFlow<Set<String>> = _hiddenPackages.asStateFlow()

    private val _customAppOrder = MutableStateFlow(prefs.appOrder)

    val filteredApps: StateFlow<List<AppInfo>> = combine(_installedApps, _searchQuery, _hiddenPackages, _customAppOrder) { apps, query, hidden, order ->
        val visibleApps = apps.filter { !hidden.contains(it.packageName) }

        // Sort: if in customAppOrder, prioritize based on custom index, else alphabetical
        val sortedApps = if (order.isNotEmpty()) {
            val orderMap = order.mapIndexed { idx, pkg -> pkg to idx }.toMap()
            visibleApps.sortedWith(compareBy({ orderMap[it.packageName] ?: Int.MAX_VALUE }, { it.label.lowercase() }))
        } else {
            visibleApps.sortedBy { it.label.lowercase() }
        }

        if (query.isBlank()) {
            sortedApps
        } else {
            sortedApps.filter {
                it.label.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Clock and Lunar state
    private val _currentTime = MutableStateFlow(Calendar.getInstance())
    val currentTime: StateFlow<Calendar> = _currentTime.asStateFlow()

    private val _currentLunarDate = MutableStateFlow(LunarCalendarHelper.getTodayLunarDate())
    val currentLunarDate: StateFlow<LunarDate> = _currentLunarDate.asStateFlow()

    // Market Prices (Giá Cà phê & Vàng nhẫn)
    private val _marketPrices = MutableStateFlow<MarketPriceReport?>(null)
    val marketPrices: StateFlow<MarketPriceReport?> = _marketPrices.asStateFlow()

    // Preferences states
    private val _showClock = MutableStateFlow(prefs.showClock)
    val showClock: StateFlow<Boolean> = _showClock.asStateFlow()

    private val _showLunar = MutableStateFlow(prefs.showLunar)
    val showLunar: StateFlow<Boolean> = _showLunar.asStateFlow()

    private val _showMarketPrices = MutableStateFlow(prefs.showMarketPrices)
    val showMarketPrices: StateFlow<Boolean> = _showMarketPrices.asStateFlow()

    private val _is24Hour = MutableStateFlow(prefs.is24HourFormat)
    val is24Hour: StateFlow<Boolean> = _is24Hour.asStateFlow()

    private val _showCanChi = MutableStateFlow(prefs.showCanChi)
    val showCanChi: StateFlow<Boolean> = _showCanChi.asStateFlow()

    private val _currentWallpaper = MutableStateFlow(WallpaperPresets.getById(prefs.wallpaperId))
    val currentWallpaper: StateFlow<WallpaperTheme> = _currentWallpaper.asStateFlow()

    private val _customWallpaperUri = MutableStateFlow(prefs.customWallpaperUri)
    val customWallpaperUri: StateFlow<String?> = _customWallpaperUri.asStateFlow()

    // Gesture preferences
    private val _swipeRightForLaunchpad = MutableStateFlow(prefs.swipeRightForLaunchpad)
    val swipeRightForLaunchpad: StateFlow<Boolean> = _swipeRightForLaunchpad.asStateFlow()

    private val _swipeUpForLaunchpad = MutableStateFlow(prefs.swipeUpForLaunchpad)
    val swipeUpForLaunchpad: StateFlow<Boolean> = _swipeUpForLaunchpad.asStateFlow()

    private val _swipeDownForNotifications = MutableStateFlow(prefs.swipeDownForNotifications)
    val swipeDownForNotifications: StateFlow<Boolean> = _swipeDownForNotifications.asStateFlow()

    private val _showLaunchpadGuide = MutableStateFlow(prefs.showLaunchpadGuide)
    val showLaunchpadGuide: StateFlow<Boolean> = _showLaunchpadGuide.asStateFlow()

    // 4 Dock apps
    private val _dockApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val dockApps: StateFlow<List<AppInfo>> = _dockApps.asStateFlow()

    // Navigation & Overlay states
    private val _isLaunchpadOpen = MutableStateFlow(false)
    val isLaunchpadOpen: StateFlow<Boolean> = _isLaunchpadOpen.asStateFlow()

    private val _isCoffeeOnlineDialogOpen = MutableStateFlow(false)
    val isCoffeeOnlineDialogOpen: StateFlow<Boolean> = _isCoffeeOnlineDialogOpen.asStateFlow()

    private val _isFullCalendarDialogOpen = MutableStateFlow(false)
    val isFullCalendarDialogOpen: StateFlow<Boolean> = _isFullCalendarDialogOpen.asStateFlow()

    private val _isGoldPriceDialogOpen = MutableStateFlow(false)
    val isGoldPriceDialogOpen: StateFlow<Boolean> = _isGoldPriceDialogOpen.asStateFlow()

    private val _reorderTargetApp = MutableStateFlow<AppInfo?>(null)
    val reorderTargetApp: StateFlow<AppInfo?> = _reorderTargetApp.asStateFlow()

    // Jiggle Mode
    private val _isJiggleMode = MutableStateFlow(false)
    val isJiggleMode: StateFlow<Boolean> = _isJiggleMode.asStateFlow()

    // UI overlays
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isAboutOpen = MutableStateFlow(false)
    val isAboutOpen: StateFlow<Boolean> = _isAboutOpen.asStateFlow()

    private val _isDesktopContextMenuOpen = MutableStateFlow(false)
    val isDesktopContextMenuOpen: StateFlow<Boolean> = _isDesktopContextMenuOpen.asStateFlow()

    private val _selectedSettingsCategory = MutableStateFlow(SettingsCategory.GESTURES)
    val selectedSettingsCategory: StateFlow<SettingsCategory> = _selectedSettingsCategory.asStateFlow()

    private val _dockSlotPicker = MutableStateFlow<Int?>(null)
    val dockSlotPicker: StateFlow<Int?> = _dockSlotPicker.asStateFlow()

    // Bank QR Code state (Ô 1: Chọn ảnh QR ngân hàng từ bộ nhớ máy)
    private val _bankQrUri = MutableStateFlow(prefs.bankQrUri)
    val bankQrUri: StateFlow<String?> = _bankQrUri.asStateFlow()

    private val _isBankQrZoomOpen = MutableStateFlow(false)
    val isBankQrZoomOpen: StateFlow<Boolean> = _isBankQrZoomOpen.asStateFlow()

    // Trình phát nhạc macOS (Ô 3: Music player widget)
    val musicPlaylist = listOf(
        LauncherMusicTrack("Diễm Xưa", "Trịnh Công Sơn", "04:15", 255, 0xFFE11D48, 0xFFFB7185),
        LauncherMusicTrack("Hạ Trắng", "Trịnh Công Sơn", "04:42", 282, 0xFF8B5CF6, 0xFFC084FC),
        LauncherMusicTrack("Nắng Thủy Tinh", "Trịnh Công Sơn", "03:58", 238, 0xFFF59E0B, 0xFFFDE047),
        LauncherMusicTrack("Biển Nhớ", "Trịnh Công Sơn", "04:20", 260, 0xFF0D9488, 0xFF2DD4BF),
        LauncherMusicTrack("macOS Lo-Fi Chill", "Apple Acoustic", "03:30", 210, 0xFF3B82F6, 0xFF60A5FA)
    )

    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _musicProgressSeconds = MutableStateFlow(45)
    val musicProgressSeconds: StateFlow<Int> = _musicProgressSeconds.asStateFlow()

    init {
        loadInstalledApps()
        startClockTicker()
        fetchMarketPrices()
    }

    private fun startClockTicker() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                _currentTime.value = Calendar.getInstance()
                _currentLunarDate.value = LunarCalendarHelper.getTodayLunarDate()

                if (_isMusicPlaying.value) {
                    val currentTrack = musicPlaylist.getOrNull(_currentTrackIndex.value)
                    val totalSec = currentTrack?.totalSeconds ?: 240
                    if (_musicProgressSeconds.value >= totalSec) {
                        _currentTrackIndex.value = (_currentTrackIndex.value + 1) % musicPlaylist.size
                        _musicProgressSeconds.value = 0
                    } else {
                        _musicProgressSeconds.value += 1
                    }
                }

                delay(1000)
            }
        }
    }

    fun fetchMarketPrices() {
        viewModelScope.launch {
            try {
                val data = marketRepo.getLatestMarketPrices()
                _marketPrices.value = data
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun openCoffeeOnlineDialog() {
        _isCoffeeOnlineDialogOpen.value = true
    }

    fun closeCoffeeOnlineDialog() {
        _isCoffeeOnlineDialogOpen.value = false
    }

    fun openFullCalendarDialog() {
        _isFullCalendarDialogOpen.value = true
    }

    fun closeFullCalendarDialog() {
        _isFullCalendarDialogOpen.value = false
    }

    fun openGoldPriceDialog() {
        _isGoldPriceDialogOpen.value = true
    }

    fun closeGoldPriceDialog() {
        _isGoldPriceDialogOpen.value = false
    }

    // Bank QR controls
    fun setBankQrUri(uri: String?) {
        _bankQrUri.value = uri
        prefs.bankQrUri = uri
        triggerVibration()
    }

    fun openBankQrZoom() {
        if (_bankQrUri.value != null) {
            _isBankQrZoomOpen.value = true
            triggerVibration()
        }
    }

    fun closeBankQrZoom() {
        _isBankQrZoomOpen.value = false
    }

    // Music Player controls
    fun toggleMusicPlayPause() {
        _isMusicPlaying.value = !_isMusicPlaying.value
        triggerVibration()
    }

    fun nextMusicTrack() {
        _currentTrackIndex.value = (_currentTrackIndex.value + 1) % musicPlaylist.size
        _musicProgressSeconds.value = 0
        _isMusicPlaying.value = true
        triggerVibration()
    }

    fun prevMusicTrack() {
        _currentTrackIndex.value = if (_currentTrackIndex.value > 0) _currentTrackIndex.value - 1 else musicPlaylist.size - 1
        _musicProgressSeconds.value = 0
        _isMusicPlaying.value = true
        triggerVibration()
    }

    fun openLaunchpad() {
        _isLaunchpadOpen.value = true
    }

    fun closeLaunchpad() {
        _isLaunchpadOpen.value = false
        _isJiggleMode.value = false
    }

    fun openReorderDialog(app: AppInfo) {
        triggerVibration()
        _reorderTargetApp.value = app
    }

    fun closeReorderDialog() {
        _reorderTargetApp.value = null
    }

    fun moveAppStep(app: AppInfo, step: Int) {
        val currentVisible = filteredApps.value.map { it.packageName }.toMutableList()
        val index = currentVisible.indexOf(app.packageName)
        if (index != -1) {
            val targetIndex = (index + step).coerceIn(0, currentVisible.size - 1)
            if (targetIndex != index) {
                currentVisible.removeAt(index)
                currentVisible.add(targetIndex, app.packageName)
                _customAppOrder.value = currentVisible
                prefs.appOrder = currentVisible
                triggerVibration()
            }
        }
    }

    fun moveAppToIndex(fromIndex: Int, toIndex: Int) {
        val currentVisible = filteredApps.value.map { it.packageName }.toMutableList()
        if (fromIndex in currentVisible.indices && toIndex in currentVisible.indices && fromIndex != toIndex) {
            val item = currentVisible.removeAt(fromIndex)
            currentVisible.add(toIndex, item)
            _customAppOrder.value = currentVisible
            prefs.appOrder = currentVisible
            triggerVibration()
        }
    }

    fun saveCurrentAppOrder() {
        val currentVisible = filteredApps.value.map { it.packageName }
        _customAppOrder.value = currentVisible
        prefs.appOrder = currentVisible
    }

    fun swapAppPositions(app1: AppInfo, app2: AppInfo) {
        val currentVisible = filteredApps.value.map { it.packageName }.toMutableList()
        val index1 = currentVisible.indexOf(app1.packageName)
        val index2 = currentVisible.indexOf(app2.packageName)
        if (index1 != -1 && index2 != -1 && index1 != index2) {
            currentVisible[index1] = app2.packageName
            currentVisible[index2] = app1.packageName
            _customAppOrder.value = currentVisible
            prefs.appOrder = currentVisible
            triggerVibration()
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
            val seenPackages = mutableSetOf<String>()

            for (ri in resolveInfos) {
                val pkgName = ri.activityInfo.packageName
                if (pkgName == getApplication<Application>().packageName) continue
                if (seenPackages.contains(pkgName)) continue
                seenPackages.add(pkgName)

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

            if (appList.isEmpty()) {
                appList.addAll(createFallbackApps())
            }

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

    fun triggerVibration() {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(50)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun setJiggleMode(active: Boolean) {
        if (active) triggerVibration()
        _isJiggleMode.value = active
    }

    fun hideApp(app: AppInfo) {
        prefs.hidePackage(app.packageName)
        _hiddenPackages.value = prefs.hiddenPackages
        triggerVibration()
    }

    fun unhideAllApps() {
        prefs.unhideAll()
        _hiddenPackages.value = emptySet()
    }

    fun uninstallApp(context: Context, app: AppInfo) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:${app.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${app.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                // Ignore
            }
        }
    }

    fun launchApp(context: Context, app: AppInfo) {
        if (_isJiggleMode.value) {
            _isJiggleMode.value = false
            return
        }
        try {
            when (app.packageName) {
                "action:phone" -> context.startActivity(Intent(Intent.ACTION_DIAL))
                "action:messages" -> context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")))
                "action:browser" -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com")))
                "action:camera" -> context.startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
                "action:photos" -> context.startActivity(Intent(Intent.ACTION_VIEW).apply { type = "image/*" })
                "action:settings" -> openSettingsDialog()
                "action:files" -> context.startActivity(Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" })
                else -> {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    } else {
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            setPackage(app.packageName)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback
        }
    }

    @SuppressLint("WrongConstant")
    fun expandNotifications(context: Context) {
        triggerVibration()
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val method = try {
                statusBarManager.getMethod("expandNotificationsPanel")
            } catch (e: NoSuchMethodException) {
                statusBarManager.getMethod("expand")
            }
            method.invoke(statusBarService)
            return
        } catch (e: Exception) {
            // Try alternative via ServiceManager
        }

        try {
            val serviceManager = Class.forName("android.os.ServiceManager")
            val getService = serviceManager.getMethod("getService", String::class.java)
            val statusBarBinder = getService.invoke(null, "statusbar")
            val asInterface = Class.forName("com.android.internal.statusbar.IStatusBarService\$Stub")
                .getMethod("asInterface", android.os.IBinder::class.java)
            val iStatusBar = asInterface.invoke(null, statusBarBinder)
            val method = iStatusBar.javaClass.getMethod("expandNotificationsPanel")
            method.invoke(iStatusBar)
        } catch (e2: Exception) {
            // Log/ignored
        }
    }

    fun openDefaultLauncherSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            context.startActivity(intent)
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

    fun toggleMarketPrices(show: Boolean) {
        _showMarketPrices.value = show
        prefs.showMarketPrices = show
    }

    fun toggle24Hour(is24: Boolean) {
        _is24Hour.value = is24
        prefs.is24HourFormat = is24
    }

    fun toggleCanChi(show: Boolean) {
        _showCanChi.value = show
        prefs.showCanChi = show
    }

    fun toggleSwipeRightForLaunchpad(enabled: Boolean) {
        _swipeRightForLaunchpad.value = enabled
        prefs.swipeRightForLaunchpad = enabled
    }

    fun toggleSwipeUpForLaunchpad(enabled: Boolean) {
        _swipeUpForLaunchpad.value = enabled
        prefs.swipeUpForLaunchpad = enabled
    }

    fun toggleSwipeDownForNotifications(enabled: Boolean) {
        _swipeDownForNotifications.value = enabled
        prefs.swipeDownForNotifications = enabled
    }

    fun toggleLaunchpadGuide(enabled: Boolean) {
        _showLaunchpadGuide.value = enabled
        prefs.showLaunchpadGuide = enabled
    }

    fun setWallpaper(wallpaper: WallpaperTheme) {
        _customWallpaperUri.value = null
        prefs.customWallpaperUri = null
        _currentWallpaper.value = wallpaper
        prefs.wallpaperId = wallpaper.id
    }

    fun setCustomWallpaperUri(uriString: String?) {
        _customWallpaperUri.value = uriString
        prefs.customWallpaperUri = uriString
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

    fun openSettingsDialog(category: SettingsCategory = SettingsCategory.GESTURES) {
        _selectedSettingsCategory.value = category
        _isSettingsOpen.value = true
    }

    fun closeSettingsDialog() {
        _isSettingsOpen.value = false
    }

    fun selectSettingsCategory(category: SettingsCategory) {
        _selectedSettingsCategory.value = category
    }

    fun openAboutDialog() {
        _isAboutOpen.value = true
    }

    fun closeAboutDialog() {
        _isAboutOpen.value = false
    }

    fun openDesktopContextMenu() {
        triggerVibration()
        _isDesktopContextMenuOpen.value = true
    }

    fun closeDesktopContextMenu() {
        _isDesktopContextMenuOpen.value = false
    }
}
