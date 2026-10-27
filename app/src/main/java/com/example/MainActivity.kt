package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import coil.compose.rememberAsyncImagePainter
import com.example.ui.components.AboutDialog
import com.example.ui.components.CoffeeOnlineDialog
import com.example.ui.components.DesktopArea
import com.example.ui.components.DesktopContextMenuDialog
import com.example.ui.components.DockAppPickerDialog
import com.example.ui.components.FullCalendarDialog
import com.example.ui.components.FullGoldPriceDialog
import com.example.ui.components.LaunchpadScreen
import com.example.ui.components.MacDock
import com.example.ui.components.MacSettingsDialog
import com.example.ui.components.ReorderAppDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MacOsLauncherApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MacOsLauncherApp(viewModel: LauncherViewModel) {
    // View model states
    val currentTime by viewModel.currentTime.collectAsState()
    val currentLunarDate by viewModel.currentLunarDate.collectAsState()
    val showClock by viewModel.showClock.collectAsState()
    val showLunar by viewModel.showLunar.collectAsState()
    val showMarketPrices by viewModel.showMarketPrices.collectAsState()
    val marketPrices by viewModel.marketPrices.collectAsState()
    val is24Hour by viewModel.is24Hour.collectAsState()
    val showCanChi by viewModel.showCanChi.collectAsState()
    val currentWallpaper by viewModel.currentWallpaper.collectAsState()
    val customWallpaperUri by viewModel.customWallpaperUri.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Gestures states
    val swipeRightForLaunchpad by viewModel.swipeRightForLaunchpad.collectAsState()
    val swipeUpForLaunchpad by viewModel.swipeUpForLaunchpad.collectAsState()
    val swipeDownForNotifications by viewModel.swipeDownForNotifications.collectAsState()
    val showLaunchpadGuide by viewModel.showLaunchpadGuide.collectAsState()

    // Navigation state
    val isLaunchpadOpen by viewModel.isLaunchpadOpen.collectAsState()
    val isCoffeeOnlineDialogOpen by viewModel.isCoffeeOnlineDialogOpen.collectAsState()
    val isFullCalendarDialogOpen by viewModel.isFullCalendarDialogOpen.collectAsState()
    val isGoldPriceDialogOpen by viewModel.isGoldPriceDialogOpen.collectAsState()
    val reorderTargetApp by viewModel.reorderTargetApp.collectAsState()

    // Dialog & overlay states
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val selectedCategory by viewModel.selectedSettingsCategory.collectAsState()
    val isAboutOpen by viewModel.isAboutOpen.collectAsState()
    val isDesktopContextMenuOpen by viewModel.isDesktopContextMenuOpen.collectAsState()
    val dockSlotPicker by viewModel.dockSlotPicker.collectAsState()
    val isJiggleMode by viewModel.isJiggleMode.collectAsState()

    // Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setCustomWallpaperUri(uri.toString())
        }
    }

    // Handle back button on sub-screens
    BackHandler(
        enabled = isLaunchpadOpen || isSettingsOpen || isDesktopContextMenuOpen ||
                isJiggleMode || isCoffeeOnlineDialogOpen || isFullCalendarDialogOpen ||
                isGoldPriceDialogOpen || reorderTargetApp != null
    ) {
        if (reorderTargetApp != null) {
            viewModel.closeReorderDialog()
        } else if (isFullCalendarDialogOpen) {
            viewModel.closeFullCalendarDialog()
        } else if (isGoldPriceDialogOpen) {
            viewModel.closeGoldPriceDialog()
        } else if (isCoffeeOnlineDialogOpen) {
            viewModel.closeCoffeeOnlineDialog()
        } else if (isJiggleMode) {
            viewModel.setJiggleMode(false)
        } else if (isDesktopContextMenuOpen) {
            viewModel.closeDesktopContextMenu()
        } else if (isSettingsOpen) {
            viewModel.closeSettingsDialog()
        } else if (isLaunchpadOpen) {
            viewModel.closeLaunchpad()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Wallpaper: Custom device photo or macOS preset
        if (customWallpaperUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = customWallpaperUri),
                contentDescription = "Hình nền thiết bị",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(currentWallpaper.backgroundBrush)
            )
        }

        // Screen transition: Desktop <-> Launchpad
        AnimatedContent(
            targetState = isLaunchpadOpen,
            transitionSpec = {
                if (targetState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width / 3 } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width / 3 } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                }
            },
            label = "screen_transition",
            modifier = Modifier.fillMaxSize()
        ) { open ->
            if (open) {
                // Launchpad / All Applications Screen
                LaunchpadScreen(
                    viewModel = viewModel,
                    apps = filteredApps,
                    searchQuery = searchQuery,
                    onNavigateBack = { viewModel.closeLaunchpad() }
                )
            } else {
                // Desktop Screen (Top Menu Bar and Control Center removed as requested)
                Box(modifier = Modifier.fillMaxSize()) {
                    DesktopArea(
                        viewModel = viewModel,
                        currentTime = currentTime,
                        lunarDate = currentLunarDate,
                        showClock = showClock,
                        showLunar = showLunar,
                        showMarketPrices = showMarketPrices,
                        marketPrices = marketPrices,
                        is24Hour = is24Hour,
                        showCanChi = showCanChi,
                        showLaunchpadGuide = showLaunchpadGuide,
                        swipeUpForLaunchpad = swipeUpForLaunchpad,
                        swipeDownForNotifications = swipeDownForNotifications,
                        swipeRightForLaunchpad = swipeRightForLaunchpad,
                        onNavigateToLaunchpad = { viewModel.openLaunchpad() },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Bottom macOS Dock (4 apps without dots)
                    MacDock(
                        viewModel = viewModel,
                        dockApps = dockApps,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }

        // Live Coffee Online Dialog (giacaphe.com/gia-ca-phe-truc-tuyen/)
        if (isCoffeeOnlineDialogOpen) {
            CoffeeOnlineDialog(
                onDismiss = { viewModel.closeCoffeeOnlineDialog() }
            )
        }

        // Full Perpetual Calendar Dialog
        if (isFullCalendarDialogOpen) {
            FullCalendarDialog(
                onDismiss = { viewModel.closeFullCalendarDialog() }
            )
        }

        // Full Gold Price Dialog (theo ngày giờ hiện tại)
        if (isGoldPriceDialogOpen) {
            FullGoldPriceDialog(
                marketPrices = marketPrices,
                onRefresh = { viewModel.fetchMarketPrices() },
                onDismiss = { viewModel.closeGoldPriceDialog() }
            )
        }

        // Reorder App Dialog
        if (reorderTargetApp != null) {
            ReorderAppDialog(
                targetApp = reorderTargetApp!!,
                allApps = filteredApps,
                onMoveStep = { step -> viewModel.moveAppStep(reorderTargetApp!!, step) },
                onSwapWith = { otherApp -> viewModel.swapAppPositions(reorderTargetApp!!, otherApp) },
                onDismiss = { viewModel.closeReorderDialog() }
            )
        }

        // macOS System Settings Window Modal
        if (isSettingsOpen) {
            MacSettingsDialog(
                viewModel = viewModel,
                selectedCategory = selectedCategory,
                showClock = showClock,
                showLunar = showLunar,
                showMarketPrices = showMarketPrices,
                swipeRightForLaunchpad = swipeRightForLaunchpad,
                swipeUpForLaunchpad = swipeUpForLaunchpad,
                swipeDownForNotifications = swipeDownForNotifications,
                showLaunchpadGuide = showLaunchpadGuide,
                marketPrices = marketPrices,
                is24Hour = is24Hour,
                showCanChi = showCanChi,
                currentWallpaper = currentWallpaper,
                customWallpaperUri = customWallpaperUri,
                dockApps = dockApps,
                lunarDate = currentLunarDate,
                onPickImageFromDevice = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onDismiss = { viewModel.closeSettingsDialog() }
            )
        }

        // Desktop Context Menu (on long press)
        if (isDesktopContextMenuOpen) {
            DesktopContextMenuDialog(
                viewModel = viewModel,
                onPickImageFromDevice = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onDismiss = { viewModel.closeDesktopContextMenu() }
            )
        }

        // Dock Slot App Picker
        if (dockSlotPicker != null) {
            DockAppPickerDialog(
                slotIndex = dockSlotPicker!!,
                allApps = installedApps,
                onSelectApp = { app ->
                    viewModel.assignAppToDockSlot(dockSlotPicker!!, app)
                },
                onDismiss = { viewModel.closeDockSlotPicker() }
            )
        }

        // About Mac Dialog
        if (isAboutOpen) {
            AboutDialog(
                onDismiss = { viewModel.closeAboutDialog() }
            )
        }
    }
}
