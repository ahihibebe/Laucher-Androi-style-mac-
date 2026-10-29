package com.example.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.service.MacOsLockAccessibilityService
import com.example.util.AudioScanner
import com.example.util.FileStorageHelper
import com.example.util.NotificationBadgeManager
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
    HIDDEN_APPS("Ứng dụng đã ẩn"),
    GENERAL("Cài đặt chung"),
    ABOUT("Giới thiệu")
}

data class LauncherMusicTrack(
    val title: String,
    val artist: String,
    val durationText: String,
    val totalSeconds: Int = 240,
    val coverGradientStart: Long = 0xFF8B5CF6,
    val coverGradientEnd: Long = 0xFFEC4899,
    val audioUri: String? = null,
    val albumArtUri: String? = null
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

    private val _desktopPinnedPackages = MutableStateFlow(prefs.desktopPinnedApps)
    val desktopPinnedApps: StateFlow<List<AppInfo>> = combine(_installedApps, _desktopPinnedPackages) { apps, pinned ->
        pinned.mapNotNull { pkg -> apps.find { it.packageName == pkg } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notificationCounts: StateFlow<Map<String, Int>> = NotificationBadgeManager.notificationCounts

    fun getBadgeCount(packageName: String): Int = NotificationBadgeManager.getBadgeCount(packageName)

    fun isAppPinnedToDesktop(packageName: String): Boolean {
        return _desktopPinnedPackages.value.contains(packageName)
    }

    fun togglePinAppToDesktop(packageName: String) {
        triggerVibration()
        val current = _desktopPinnedPackages.value.toMutableList()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _desktopPinnedPackages.value = current
        prefs.desktopPinnedApps = current
    }

    fun pinAppToDesktop(packageName: String) {
        if (!_desktopPinnedPackages.value.contains(packageName)) {
            togglePinAppToDesktop(packageName)
        }
    }

    fun unpinAppFromDesktop(packageName: String) {
        if (_desktopPinnedPackages.value.contains(packageName)) {
            togglePinAppToDesktop(packageName)
        }
    }

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

    private val _isMarketPricesRefreshing = MutableStateFlow(false)
    val isMarketPricesRefreshing: StateFlow<Boolean> = _isMarketPricesRefreshing.asStateFlow()

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

    // 2 Chạm khóa màn hình
    private val _doubleTapToLock = MutableStateFlow(prefs.doubleTapToLock)
    val doubleTapToLock: StateFlow<Boolean> = _doubleTapToLock.asStateFlow()

    private val _isAccessibilityPromptOpen = MutableStateFlow(false)
    val isAccessibilityPromptOpen: StateFlow<Boolean> = _isAccessibilityPromptOpen.asStateFlow()

    // Bank QR Resize & Scale state
    private val _bankQrScale = MutableStateFlow(prefs.bankQrScale)
    val bankQrScale: StateFlow<Float> = _bankQrScale.asStateFlow()

    private val _bankQrFitMode = MutableStateFlow(prefs.bankQrFitMode)
    val bankQrFitMode: StateFlow<String> = _bankQrFitMode.asStateFlow()

    // Gỡ cài đặt / Xóa ứng dụng confirmation state
    private val _appToDelete = MutableStateFlow<AppInfo?>(null)
    val appToDelete: StateFlow<AppInfo?> = _appToDelete.asStateFlow()

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

    // Trình phát nhạc macOS (Ô 3: Music player widget) & Quét nhạc máy/thẻ
    private val _musicPlaylist = MutableStateFlow<List<LauncherMusicTrack>>(emptyList())
    val musicPlaylist: StateFlow<List<LauncherMusicTrack>> = _musicPlaylist.asStateFlow()

    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _musicProgressSeconds = MutableStateFlow(0)
    val musicProgressSeconds: StateFlow<Int> = _musicProgressSeconds.asStateFlow()

    private val _musicScanStatus = MutableStateFlow<String?>("Chưa quét nhạc. Chạm để quét nhạc trên máy & thẻ nhớ")
    val musicScanStatus: StateFlow<String?> = _musicScanStatus.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            val packageName = intent.data?.schemeSpecificPart
            when (action) {
                Intent.ACTION_PACKAGE_REMOVED,
                Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                    val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                    if (!isReplacing && packageName != null) {
                        onPackageRemoved(packageName)
                    }
                }
                Intent.ACTION_PACKAGE_ADDED -> {
                    val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                    if (!isReplacing && packageName != null) {
                        loadInstalledApps()
                    }
                }
                Intent.ACTION_PACKAGE_REPLACED,
                Intent.ACTION_PACKAGE_CHANGED -> {
                    loadInstalledApps()
                }
            }
        }
    }

    private fun onPackageRemoved(packageName: String) {
        // Xóa ngay lập tức khỏi danh sách ứng dụng đã cài đặt
        _installedApps.value = _installedApps.value.filter { it.packageName != packageName }

        // Xóa khỏi thứ tự tùy biến
        val currentOrder = _customAppOrder.value.toMutableList()
        if (currentOrder.remove(packageName)) {
            _customAppOrder.value = currentOrder
            prefs.appOrder = currentOrder
        }

        // Xóa khỏi danh sách ghim trên màn hình chính
        val currentPinned = _desktopPinnedPackages.value.toMutableList()
        if (currentPinned.remove(packageName)) {
            _desktopPinnedPackages.value = currentPinned
            prefs.desktopPinnedApps = currentPinned
        }

        // Xóa khỏi danh sách ẩn
        val currentHidden = _hiddenPackages.value.toMutableSet()
        if (currentHidden.remove(packageName)) {
            _hiddenPackages.value = currentHidden
            prefs.hiddenPackages = currentHidden
        }

        // Cập nhật lại thanh Dock nếu ứng dụng bị xóa nằm trên Dock
        refreshDockApps(_installedApps.value)
    }

    init {
        loadInstalledApps()
        startClockTicker()
        fetchMarketPrices()
        registerPackageReceiver()
    }

    private fun registerPackageReceiver() {
        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addAction(Intent.ACTION_PACKAGE_CHANGED)
                addDataScheme("package")
            }
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                app.registerReceiver(packageReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                app.registerReceiver(packageReceiver, filter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(packageReceiver)
        } catch (e: Exception) {
            // ignore
        }
        stopMediaPlayer()
    }

    private fun startClockTicker() {
        viewModelScope.launch(Dispatchers.Default) {
            var tickerSeconds = 0
            while (true) {
                _currentTime.value = Calendar.getInstance()
                _currentLunarDate.value = LunarCalendarHelper.getTodayLunarDate()

                if (_isMusicPlaying.value) {
                    val player = mediaPlayer
                    if (player != null && player.isPlaying) {
                        try {
                            _musicProgressSeconds.value = (player.currentPosition / 1000).coerceAtLeast(0)
                        } catch (e: Exception) {
                            // ignore
                        }
                    } else {
                        val currentTrack = _musicPlaylist.value.getOrNull(_currentTrackIndex.value)
                        val totalSec = currentTrack?.totalSeconds ?: 240
                        if (_musicProgressSeconds.value >= totalSec) {
                            nextMusicTrack()
                        } else {
                            _musicProgressSeconds.value += 1
                        }
                    }
                }

                tickerSeconds++
                // Tự động cập nhật giá thị trường (cà phê Lâm Đồng, vàng, xăng dầu) mỗi 60 giây theo thời gian thực
                if (tickerSeconds % 60 == 0) {
                    fetchMarketPrices()
                }

                delay(1000)
            }
        }
    }

    fun fetchMarketPrices() {
        viewModelScope.launch(Dispatchers.IO) {
            _isMarketPricesRefreshing.value = true
            try {
                val data = marketRepo.getLatestMarketPrices()
                _marketPrices.value = data
            } catch (e: Exception) {
                // Ignore
            } finally {
                _isMarketPricesRefreshing.value = false
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

    // 2 Chạm khóa màn hình
    fun toggleDoubleTapToLock(enabled: Boolean) {
        prefs.doubleTapToLock = enabled
        _doubleTapToLock.value = enabled
        triggerVibration()
    }

    fun lockScreen(context: Context) {
        if (!_doubleTapToLock.value) return
        triggerVibration()
        val success = MacOsLockAccessibilityService.lockScreen()
        if (!success) {
            _isAccessibilityPromptOpen.value = true
        }
    }

    fun closeAccessibilityPrompt() {
        _isAccessibilityPromptOpen.value = false
    }

    // Bank QR Resize & Scale controls
    fun setBankQrScale(scale: Float) {
        val clamped = scale.coerceIn(0.6f, 2.0f)
        _bankQrScale.value = clamped
        prefs.bankQrScale = clamped
    }

    fun toggleBankQrFitMode() {
        val newMode = if (_bankQrFitMode.value == "fit") "crop" else "fit"
        _bankQrFitMode.value = newMode
        prefs.bankQrFitMode = newMode
        triggerVibration()
    }

    // Music Player controls & MediaStore Audio Scanning
    fun scanDeviceMusic(context: Context) {
        triggerVibration()
        _musicScanStatus.value = "Đang quét nhạc trên bộ nhớ máy & thẻ nhớ..."
        viewModelScope.launch {
            try {
                val scanned = AudioScanner.scanLocalAudio(context)
                if (scanned.isNotEmpty()) {
                    _musicPlaylist.value = scanned
                    _currentTrackIndex.value = 0
                    _musicProgressSeconds.value = 0
                    _musicScanStatus.value = "Đã quét ${scanned.size} bài hát từ máy & thẻ nhớ"
                    stopMediaPlayer()
                } else {
                    _musicScanStatus.value = "Không tìm thấy file nhạc (.mp3, .m4a) trên máy/thẻ nhớ"
                }
            } catch (e: Exception) {
                _musicScanStatus.value = "Lỗi khi quét nhạc: ${e.message}"
            }
        }
    }

    private fun playTrackAtIndex(index: Int) {
        val tracks = _musicPlaylist.value
        if (tracks.isEmpty()) return
        val clampedIndex = index.coerceIn(0, tracks.size - 1)
        _currentTrackIndex.value = clampedIndex
        _musicProgressSeconds.value = 0
        val track = tracks[clampedIndex]

        stopMediaPlayer()

        if (track.audioUri != null) {
            try {
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(getApplication(), Uri.parse(track.audioUri))
                    prepare()
                    start()
                    setOnCompletionListener {
                        nextMusicTrack()
                    }
                }
                mediaPlayer = player
                _isMusicPlaying.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _isMusicPlaying.value = true
            }
        } else {
            _isMusicPlaying.value = true
        }
    }

    private fun stopMediaPlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
    }

    fun toggleMusicPlayPause() {
        triggerVibration()
        val player = mediaPlayer
        if (player != null) {
            if (player.isPlaying) {
                player.pause()
                _isMusicPlaying.value = false
            } else {
                player.start()
                _isMusicPlaying.value = true
            }
        } else {
            if (!_isMusicPlaying.value) {
                playTrackAtIndex(_currentTrackIndex.value)
            } else {
                _isMusicPlaying.value = false
            }
        }
    }

    fun nextMusicTrack() {
        val listSize = _musicPlaylist.value.size
        if (listSize == 0) return
        val nextIdx = (_currentTrackIndex.value + 1) % listSize
        playTrackAtIndex(nextIdx)
        triggerVibration()
    }

    fun prevMusicTrack() {
        val listSize = _musicPlaylist.value.size
        if (listSize == 0) return
        val prevIdx = if (_currentTrackIndex.value > 0) _currentTrackIndex.value - 1 else listSize - 1
        playTrackAtIndex(prevIdx)
        triggerVibration()
    }

    // Gỡ cài đặt / Xóa ứng dụng
    fun promptDeleteApp(app: AppInfo) {
        triggerVibration()
        _appToDelete.value = app
    }

    fun dismissDeleteApp() {
        _appToDelete.value = null
    }

    fun confirmDeleteApp(context: Context) {
        val app = _appToDelete.value ?: return
        _appToDelete.value = null
        uninstallApp(context, app)
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

    val hiddenApps: StateFlow<List<AppInfo>> = combine(_installedApps, _hiddenPackages) { apps, hidden ->
        apps.filter { hidden.contains(it.packageName) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun unhideApp(packageName: String) {
        prefs.unhidePackage(packageName)
        _hiddenPackages.value = prefs.hiddenPackages
        triggerVibration()
    }

    fun unhideAllApps() {
        prefs.unhideAll()
        _hiddenPackages.value = emptySet()
        triggerVibration()
    }

    fun uninstallApp(context: Context, app: AppInfo) {
        triggerVibration()
        // If it is a virtual action app, hide/delete it immediately from list
        if (app.packageName.startsWith("action:")) {
            hideApp(app)
            return
        }

        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:${app.packageName}")
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                    data = Uri.parse("package:${app.packageName}")
                    putExtra(Intent.EXTRA_RETURN_RESULT, true)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${app.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e3: Exception) {
                    // Fallback: hide if system or cannot delete
                    hideApp(app)
                }
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

    fun saveAndSetCustomWallpaper(context: Context, uri: Uri) {
        val savedPath = FileStorageHelper.saveUriToInternalStorage(context, uri, "saved_wallpaper")
        val finalPath = savedPath ?: uri.toString()
        _customWallpaperUri.value = finalPath
        prefs.customWallpaperUri = finalPath
        triggerVibration()
    }

    fun saveAndSetBankQr(context: Context, uri: Uri) {
        val savedPath = FileStorageHelper.saveUriToInternalStorage(context, uri, "saved_bank_qr")
        val finalPath = savedPath ?: uri.toString()
        _bankQrUri.value = finalPath
        prefs.bankQrUri = finalPath
        triggerVibration()
    }

    fun deleteBankQr(context: Context) {
        FileStorageHelper.deleteInternalFile(context, "saved_bank_qr")
        _bankQrUri.value = null
        prefs.bankQrUri = null
        triggerVibration()
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
