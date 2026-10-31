package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.model.LunarDate
import com.example.model.MarketPriceReport
import com.example.model.AppInfo
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.SettingsCategory
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesktopArea(
    viewModel: LauncherViewModel,
    currentTime: Calendar,
    lunarDate: LunarDate,
    showClock: Boolean,
    showLunar: Boolean,
    showMarketPrices: Boolean,
    marketPrices: MarketPriceReport?,
    is24Hour: Boolean,
    showCanChi: Boolean,
    showLaunchpadGuide: Boolean,
    swipeUpForLaunchpad: Boolean,
    swipeDownForNotifications: Boolean,
    swipeRightForLaunchpad: Boolean,
    onNavigateToLaunchpad: () -> Unit,
    onPickBankQr: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val timeFormat = if (is24Hour) "HH:mm" else "hh:mm a"
    val timeFormatter = remember(is24Hour) { SimpleDateFormat(timeFormat, Locale.getDefault()) }
    val fullDateFormatter = remember { SimpleDateFormat("EEEE, d/M", Locale("vi", "VN")) }

    val formattedTime = remember(currentTime.timeInMillis, is24Hour) { timeFormatter.format(currentTime.time) }
    val formattedDate = remember(currentTime.timeInMillis) { fullDateFormatter.format(currentTime.time) }

    var accumulatedDragX by remember { mutableFloatStateOf(0f) }
    var accumulatedDragY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            // 1. Swipe Gestures (Up = Tất cả ứng dụng, Down = Thông báo, Right = Launchpad)
            .pointerInput(swipeUpForLaunchpad, swipeDownForNotifications, swipeRightForLaunchpad) {
                detectDragGestures(
                    onDragStart = {
                        accumulatedDragX = 0f
                        accumulatedDragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        accumulatedDragX += dragAmount.x
                        accumulatedDragY += dragAmount.y
                    },
                    onDragEnd = {
                        val dx = accumulatedDragX
                        val dy = accumulatedDragY
                        val absX = abs(dx)
                        val absY = abs(dy)

                        if (absY > absX && absY > 50f) {
                            if (dy < -50f && swipeUpForLaunchpad) {
                                onNavigateToLaunchpad()
                            } else if (dy > 50f && swipeDownForNotifications) {
                                viewModel.expandNotifications(context)
                            }
                        } else if (absX > absY && absX > 50f) {
                            if (swipeRightForLaunchpad) {
                                onNavigateToLaunchpad()
                            }
                        }
                        accumulatedDragX = 0f
                        accumulatedDragY = 0f
                    },
                    onDragCancel = {
                        accumulatedDragX = 0f
                        accumulatedDragY = 0f
                    }
                )
            }
            // 2. Chạm 2 lần (Double Tap) Khóa màn hình & Nhấn giữ mở Cài đặt nền
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        viewModel.lockScreen(context)
                    },
                    onLongPress = {
                        viewModel.openDesktopContextMenu()
                    }
                )
            }
            .testTag("desktop_background_area")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(top = statusBarTop + 14.dp, start = 14.dp, end = 14.dp, bottom = 104.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pagerScope = rememberCoroutineScope()
            val leftPagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
            val marketPagerState = rememberPagerState(pageCount = { 3 })
            val currentPrices = marketPrices ?: remember { com.example.model.MarketPriceReport() }

            val bankQrUri by viewModel.bankQrUri.collectAsState()
            val bankQrScale by viewModel.bankQrScale.collectAsState()
            val bankQrFitMode by viewModel.bankQrFitMode.collectAsState()
            val musicPlaylist by viewModel.musicPlaylist.collectAsState()
            val isMusicPlaying by viewModel.isMusicPlaying.collectAsState()
            val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()
            val musicProgressSeconds by viewModel.musicProgressSeconds.collectAsState()
            val musicScanStatus by viewModel.musicScanStatus.collectAsState()
            val currentTrack = musicPlaylist.getOrNull(currentTrackIndex) ?: musicPlaylist.firstOrNull()

            // Audio permission launcher for scanning device and SD card music
            val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            val audioPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    viewModel.scanDeviceMusic(context)
                }
            }

            val showUtilityWidget by viewModel.showUtilityWidget.collectAsState()
            val isWidget1Visible = showUtilityWidget && (showClock || showLunar)
            val isWidget2Visible = showMarketPrices

            // Widget 1: Banner Card (Lịch Vạn Niên & Đồng Hồ, QR Ngân Hàng, Trình Phát Nhạc)
            if (isWidget1Visible) {
                DesktopUtilityBannerCard(
                    leftPagerState = leftPagerState,
                    formattedTime = formattedTime,
                    formattedDate = formattedDate,
                    lunarDate = lunarDate,
                    showClock = showClock,
                    showLunar = showLunar,
                    showCanChi = showCanChi,
                    bankQrUri = bankQrUri,
                    bankQrFitMode = bankQrFitMode,
                    bankQrScale = bankQrScale,
                    onPickBankQr = onPickBankQr,
                    currentTrack = currentTrack,
                    isMusicPlaying = isMusicPlaying,
                    musicProgressSeconds = musicProgressSeconds,
                    musicScanStatus = musicScanStatus,
                    audioPermission = audioPermission,
                    audioPermissionLauncher = audioPermissionLauncher,
                    viewModel = viewModel
                )
            }

            if (isWidget1Visible && isWidget2Visible) {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Widget 2: Banner Card Giá Thị Trường (Cà Phê & Vàng, Giá Xăng, Giá Dầu)
            if (isWidget2Visible) {
                DesktopMarketBannerCard(
                    marketPagerState = marketPagerState,
                    marketPrices = currentPrices,
                    viewModel = viewModel
                )
            }

            // Desktop Pinned Apps Grid (Apps pinned to home screen from Launchpad)
            val desktopPinnedApps by viewModel.desktopPinnedApps.collectAsState()

            if (desktopPinnedApps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("desktop_pinned_apps_grid")
                ) {
                    items(
                        items = desktopPinnedApps,
                        key = { it.packageName }
                    ) { app ->
                        DesktopAppItem(
                            app = app,
                            onLaunch = { viewModel.launchApp(context, app) },
                            onUnpin = { viewModel.unpinAppFromDesktop(app.packageName) }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Thanh hướng dẫn thao tác kích thước nhỏ gọn hơn, hiển thị cách thanh dock một khoảng
            if (showLaunchpadGuide) {
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.20f),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLaunchpad() }
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    Color.White.copy(alpha = 0.20f),
                                    Color.White.copy(alpha = 0.50f)
                                )
                            ),
                            RoundedCornerShape(18.dp)
                        )
                        .testTag("launchpad_swipe_guide_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFF472B6), Color(0xFFEC4899))
                                        )
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = "Launchpad",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Tất cả ứng dụng (Launchpad)",
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Vuốt sang phải / vuốt lên / chạm để mở",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopAppItem(
    app: AppInfo,
    onLaunch: () -> Unit,
    onUnpin: () -> Unit
) {
    val context = LocalContext.current
    var isMenuOpen by remember { mutableStateOf(false) }

    val shortcuts: List<com.example.util.AppShortcutItem> = remember(app.packageName, isMenuOpen) {
        if (isMenuOpen) {
            com.example.util.AppShortcutHelper.getAppShortcuts(context, app.packageName)
        } else {
            emptyList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onLaunch,
                onLongClick = { isMenuOpen = true }
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                // 3D Liquid Jelly Glass Icon Frame
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White.copy(alpha = 0.28f),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .size(62.dp)
                        .border(
                            width = 1.2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.90f),
                                    Color.White.copy(alpha = 0.20f),
                                    Color.White.copy(alpha = 0.65f)
                                )
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Top Specular Gloss Highlight (Liquid Dome Shine)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .align(Alignment.TopCenter)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.45f),
                                            Color.White.copy(alpha = 0.08f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (app.iconBitmap != null) {
                                Image(
                                    bitmap = app.iconBitmap,
                                    contentDescription = app.label,
                                    modifier = Modifier.size(50.dp)
                                )
                            } else if (app.systemIconFallback != null) {
                                Icon(
                                    imageVector = app.systemIconFallback,
                                    contentDescription = app.label,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            // App Label with subtle text
            Text(
                text = app.label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        // Long press context menu for pinned app
        DropdownMenu(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false },
            modifier = Modifier
                .background(Color(0xFF262626))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
        ) {
            // App name header
            DropdownMenuItem(
                text = {
                    Text(
                        app.label,
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                onClick = {
                    isMenuOpen = false
                    onLaunch()
                }
            )

            HorizontalDivider(color = Color.DarkGray)

            // App Shortcuts if available
            if (shortcuts.isNotEmpty()) {
                for (shortcut in shortcuts) {
                    DropdownMenuItem(
                        text = {
                            Text(shortcut.shortLabel, color = Color(0xFFF3F4F6), fontSize = 12.5.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            isMenuOpen = false
                            com.example.util.AppShortcutHelper.launchShortcut(context, shortcut)
                        }
                    )
                }
                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
            }

            // Xóa khỏi màn hình chính (Unpin)
            DropdownMenuItem(
                text = {
                    Text(
                        "Xóa khỏi màn hình chính",
                        color = Color(0xFFF87171),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.BookmarkRemove,
                        contentDescription = null,
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(17.dp)
                    )
                },
                onClick = {
                    isMenuOpen = false
                    onUnpin()
                }
            )

            // Thông tin ứng dụng (App Info)
            DropdownMenuItem(
                text = { Text("Thông tin ứng dụng", color = Color.White, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(17.dp)
                    )
                },
                onClick = {
                    isMenuOpen = false
                    try {
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = android.net.Uri.parse("package:" + app.packageName)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            )
        }
    }
}
