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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ui.components.AboutDialog
import com.example.ui.components.ControlCenterDropdown
import com.example.ui.components.DesktopArea
import com.example.ui.components.DesktopContextMenuDialog
import com.example.ui.components.DockAppPickerDialog
import com.example.ui.components.LaunchpadScreen
import com.example.ui.components.MacDock
import com.example.ui.components.MacSettingsDialog
import com.example.ui.components.TopMenuBar
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

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

    // Dialog & overlay states
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val selectedCategory by viewModel.selectedSettingsCategory.collectAsState()
    val isControlCenterOpen by viewModel.isControlCenterOpen.collectAsState()
    val isAboutOpen by viewModel.isAboutOpen.collectAsState()
    val isDesktopContextMenuOpen by viewModel.isDesktopContextMenuOpen.collectAsState()
    val dockSlotPicker by viewModel.dockSlotPicker.collectAsState()
    val isJiggleMode by viewModel.isJiggleMode.collectAsState()

    // Standard Android Photo Picker for Custom Wallpaper
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setCustomWallpaperUri(uri.toString())
        }
    }

    // Handle back button on sub-screens
    BackHandler(enabled = pagerState.currentPage == 1 || isControlCenterOpen || isSettingsOpen || isDesktopContextMenuOpen || isJiggleMode) {
        if (isJiggleMode) {
            viewModel.setJiggleMode(false)
        } else if (isDesktopContextMenuOpen) {
            viewModel.closeDesktopContextMenu()
        } else if (isControlCenterOpen) {
            viewModel.closeControlCenter()
        } else if (isSettingsOpen) {
            viewModel.closeSettingsDialog()
        } else if (pagerState.currentPage == 1) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Wallpaper: Custom device photo or macOS preset gradient
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

        // Horizontal Pager: Page 0 = Desktop; Page 1 = Launchpad
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("launcher_horizontal_pager")
        ) { page ->
            when (page) {
                0 -> {
                    // Desktop Screen
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
                            onNavigateToLaunchpad = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            onOpenPhotoPicker = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 40.dp)
                        )

                        // Top macOS Menu Bar (clean: removed finder/tệp/xem/tìm kiếm/wifi/pin, added pink apple)
                        TopMenuBar(
                            viewModel = viewModel,
                            currentTime = currentTime,
                            lunarDate = currentLunarDate,
                            showClock = showClock,
                            showLunar = showLunar,
                            is24Hour = is24Hour,
                            onOpenLaunchpad = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            modifier = Modifier.align(Alignment.TopCenter)
                        )

                        // Bottom macOS Dock (4 apps with frosted glass frame)
                        MacDock(
                            viewModel = viewModel,
                            dockApps = dockApps,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }

                1 -> {
                    // Launchpad / All Applications Screen
                    LaunchpadScreen(
                        viewModel = viewModel,
                        apps = filteredApps,
                        searchQuery = searchQuery,
                        onNavigateBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    )
                }
            }
        }

        // Control Center Dropdown
        AnimatedVisibility(
            visible = isControlCenterOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 42.dp, end = 12.dp)
        ) {
            ControlCenterDropdown(
                onDismiss = { viewModel.closeControlCenter() }
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
