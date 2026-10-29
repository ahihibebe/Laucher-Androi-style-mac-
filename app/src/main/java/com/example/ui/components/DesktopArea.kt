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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
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
                .padding(top = statusBarTop + 14.dp, start = 14.dp, end = 14.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pagerScope = rememberCoroutineScope()
            val leftPagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
            val marketPagerState = rememberPagerState(pageCount = { 3 })

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

            // Symmetrical Cards: Left = QR / Lịch Vạn Niên / Trình Phát Nhạc; Right = Cà phê & Vàng / Giá Xăng / Giá Dầu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Widget 1: Left Card (3 pages: 0 = QR Ngân Hàng, 1 = Lịch Vạn Niên, 2 = Trình Phát Nhạc)
                if (showClock || showLunar) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.38f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(196.dp)
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.08f))
                                ),
                                RoundedCornerShape(20.dp)
                            )
                            .testTag("clock_lunar_desktop_widget")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Bar inside Left Card: Title + Page Indicator (3 dots)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.clickable {
                                        pagerScope.launch {
                                            val next = (leftPagerState.currentPage + 1) % 3
                                            leftPagerState.animateScrollToPage(next)
                                        }
                                    }
                                ) {
                                    val headerIcon = when (leftPagerState.currentPage) {
                                        0 -> Icons.Default.QrCode2
                                        2 -> Icons.Default.MusicNote
                                        else -> Icons.Default.CalendarMonth
                                    }
                                    val headerTint = when (leftPagerState.currentPage) {
                                        0 -> Color(0xFF38BDF8)
                                        2 -> Color(0xFFF43F5E)
                                        else -> Color(0xFFFFB74D)
                                    }
                                    val headerTitle = when (leftPagerState.currentPage) {
                                        0 -> "QR NGÂN HÀNG"
                                        2 -> "TRÌNH PHÁT NHẠC"
                                        else -> "LỊCH VẠN NIÊN"
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(headerTint.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = headerIcon,
                                            contentDescription = null,
                                            tint = headerTint,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = headerTitle,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        // Chữ xem đầy đủ di chuyển dưới chữ lịch vạn niên
                                        if (leftPagerState.currentPage == 1) {
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .clickable { viewModel.openFullCalendarDialog() },
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Text("Xem đầy đủ", color = Color(0xFFFBBF24), fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(8.5.dp))
                                            }
                                        }
                                    }
                                }

                                // 3 dots for Left Card
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(3) { idx ->
                                        Box(
                                            modifier = Modifier
                                                .size(if (leftPagerState.currentPage == idx) 6.dp else 4.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (leftPagerState.currentPage == idx) {
                                                        when (idx) {
                                                            0 -> Color(0xFF38BDF8)
                                                            1 -> Color(0xFFFFB74D)
                                                            else -> Color(0xFFF43F5E)
                                                        }
                                                    } else Color.White.copy(alpha = 0.35f)
                                                )
                                                .clickable {
                                                    pagerScope.launch { leftPagerState.animateScrollToPage(idx) }
                                                }
                                        )
                                    }
                                }
                            }

                            // 3-page HorizontalPager for Left Card
                            HorizontalPager(
                                state = leftPagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) { page ->
                                when (page) {
                                    0 -> {
                                        // Ô 1: Chọn ảnh QR ngân hàng từ bộ nhớ máy & hiển thị
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = 2.dp),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            if (bankQrUri != null) {
                                                // Container QR với khả năng zoom/resize và chuyển chế độ vừa khung
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color.White,
                                                    shadowElevation = 4.dp,
                                                    modifier = Modifier
                                                        .size(86.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable { viewModel.openBankQrZoom() }
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .padding(2.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Image(
                                                            painter = rememberAsyncImagePainter(model = bankQrUri),
                                                            contentDescription = "Mã QR ngân hàng",
                                                            contentScale = if (bankQrFitMode == "fit") ContentScale.Fit else ContentScale.Crop,
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .graphicsLayer {
                                                                    scaleX = bankQrScale
                                                                    scaleY = bankQrScale
                                                                }
                                                        )
                                                    }
                                                }

                                                // Thanh công cụ Resize & Căn chỉnh ảnh QR vừa khung
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                    ) {
                                                        Text(
                                                            text = "−",
                                                            color = Color.White,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .background(Color.White.copy(alpha = 0.15f))
                                                                .clickable { viewModel.setBankQrScale(bankQrScale - 0.1f) }
                                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                                        )
                                                        Text(
                                                            text = "${(bankQrScale * 100).toInt()}%",
                                                            color = Color(0xFF67E8F9),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier
                                                                .clickable { viewModel.setBankQrScale(1.0f) }
                                                                .padding(horizontal = 2.dp)
                                                        )
                                                        Text(
                                                            text = "+",
                                                            color = Color.White,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .background(Color.White.copy(alpha = 0.15f))
                                                                .clickable { viewModel.setBankQrScale(bankQrScale + 0.1f) }
                                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                                        )
                                                    }

                                                    Text(
                                                        text = if (bankQrFitMode == "fit") "Vừa khung" else "Lấp đầy",
                                                        color = Color(0xFFFBBF24),
                                                        fontSize = 7.5.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color.White.copy(alpha = 0.12f))
                                                            .clickable { viewModel.toggleBankQrFitMode() }
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "🔍 Phóng to",
                                                        color = Color(0xFF60A5FA),
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .clickable { viewModel.openBankQrZoom() }
                                                            .padding(horizontal = 4.dp, vertical = 1.5.dp)
                                                    )
                                                    Text(
                                                        text = "🔄 Đổi ảnh",
                                                        color = Color(0xFFFDE047),
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .clickable { onPickBankQr() }
                                                            .padding(horizontal = 4.dp, vertical = 1.5.dp)
                                                    )
                                                }
                                            } else {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(Color.White.copy(alpha = 0.06f))
                                                        .clickable { onPickBankQr() }
                                                        .padding(6.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AddPhotoAlternate,
                                                        contentDescription = null,
                                                        tint = Color(0xFF38BDF8),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Text(
                                                        text = "Chọn ảnh QR ngân hàng",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "Chạm để tải từ bộ nhớ máy",
                                                        color = Color.White.copy(alpha = 0.6f),
                                                        fontSize = 7.5.sp
                                                    )
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color(0xFF0284C7).copy(alpha = 0.4f),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { onPickBankQr() }
                                                ) {
                                                    Text(
                                                        text = "Tải ảnh mã QR",
                                                        color = Color(0xFF7DD3FC),
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(vertical = 3.dp),
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "👉 Cuộn sang xem Lịch Vạn Niên",
                                                color = Color.White.copy(alpha = 0.45f),
                                                fontSize = 7.5.sp,
                                                modifier = Modifier.clickable {
                                                    pagerScope.launch { leftPagerState.animateScrollToPage(1) }
                                                }
                                            )
                                        }
                                    }
                                    1 -> {
                                        // Ô 2: Lịch Vạn Niên (Xem đầy đủ ở dưới tiêu đề, âm lịch dưới cùng)
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable { viewModel.openFullCalendarDialog() },
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Center: Đồng hồ giờ & ngày dương
                                            if (showClock) {
                                                Column(modifier = Modifier.padding(top = 2.dp)) {
                                                    Text(
                                                        text = formattedTime,
                                                        color = Color.White,
                                                        fontSize = 25.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = formattedDate,
                                                        color = Color.White.copy(alpha = 0.85f),
                                                        fontSize = 10.5.sp
                                                    )
                                                }
                                            }

                                            // Thêm âm lịch vào dưới cùng
                                            if (showLunar) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "Âm lịch: Ngày ${lunarDate.day}/${lunarDate.month}${if (lunarDate.isLeap) " (Nhuận)" else ""}",
                                                        color = Color(0xFFFDE047),
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    if (showCanChi) {
                                                        Text(
                                                            text = "Năm ${lunarDate.canChiYear} • Giờ ${lunarDate.canChiDay}",
                                                            color = Color.White.copy(alpha = 0.75f),
                                                            fontSize = 8.5.sp
                                                        )
                                                    }
                                                    Text(
                                                        text = "Chạm để xem lịch vạn niên đầy đủ",
                                                        color = Color.White.copy(alpha = 0.45f),
                                                        fontSize = 7.5.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    2 -> {
                                        // Ô 3: Trình phát nhạc
                                        if (currentTrack == null) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier.padding(top = 10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(46.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFFF43F5E).copy(alpha = 0.2f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.MusicNote,
                                                            contentDescription = null,
                                                            tint = Color(0xFFF43F5E),
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = "Chưa có nhạc",
                                                        color = Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "Chạm quét để tìm nhạc thẻ nhớ / máy",
                                                        color = Color.White.copy(alpha = 0.65f),
                                                        fontSize = 8.sp,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                    )
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color(0xFFE11D48).copy(alpha = 0.85f),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable {
                                                            val hasPerm = ContextCompat.checkSelfPermission(context, audioPermission) == PackageManager.PERMISSION_GRANTED
                                                            if (hasPerm) {
                                                                viewModel.scanDeviceMusic(context)
                                                            } else {
                                                                audioPermissionLauncher.launch(audioPermission)
                                                            }
                                                        }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(Icons.Default.Refresh, contentDescription = "Quét lại", tint = Color.White, modifier = Modifier.size(13.dp))
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(
                                                            text = musicScanStatus ?: "Quét nhạc",
                                                            color = Color.White,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(top = 2.dp),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                // Row: Cover art + Song details
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Cover Art (từ thẻ nhớ/máy hoặc gradient)
                                                    Box(
                                                        modifier = Modifier
                                                            .size(42.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(
                                                                Brush.linearGradient(
                                                                    listOf(
                                                                        Color(currentTrack.coverGradientStart),
                                                                        Color(currentTrack.coverGradientEnd)
                                                                    )
                                                                )
                                                            )
                                                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (currentTrack.albumArtUri != null) {
                                                            Image(
                                                                painter = rememberAsyncImagePainter(model = currentTrack.albumArtUri),
                                                                contentDescription = null,
                                                                contentScale = ContentScale.Crop,
                                                                modifier = Modifier.fillMaxSize()
                                                            )
                                                        } else {
                                                            Icon(
                                                                imageVector = Icons.Default.MusicNote,
                                                                contentDescription = null,
                                                                tint = Color.White,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                        }
                                                    }

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = currentTrack.title,
                                                            color = Color.White,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1
                                                        )
                                                        Text(
                                                            text = currentTrack.artist,
                                                            color = Color.White.copy(alpha = 0.7f),
                                                            fontSize = 8.5.sp,
                                                            maxLines = 1
                                                        )
                                                        Text(
                                                            text = if (isMusicPlaying) "● Đang phát" else "○ Tạm dừng",
                                                            color = if (isMusicPlaying) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.5f),
                                                            fontSize = 7.5.sp
                                                        )
                                                    }
                                                }

                                                // Progress bar
                                                val playedMin = musicProgressSeconds / 60
                                                val playedSec = musicProgressSeconds % 60
                                                val progressPercent = (musicProgressSeconds.toFloat() / currentTrack.totalSeconds.toFloat()).coerceIn(0f, 1f)

                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = String.format(Locale.US, "%02d:%02d", playedMin, playedSec),
                                                            color = Color.White.copy(alpha = 0.7f),
                                                            fontSize = 7.5.sp
                                                        )
                                                        Text(
                                                            text = currentTrack.durationText,
                                                            color = Color.White.copy(alpha = 0.7f),
                                                            fontSize = 7.5.sp
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    LinearProgressIndicator(
                                                        progress = { progressPercent },
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(3.dp)
                                                            .clip(CircleShape),
                                                        color = Color(0xFFF43F5E),
                                                        trackColor = Color.White.copy(alpha = 0.15f)
                                                    )
                                                }

                                                // Playback buttons: Previous, Play/Pause, Next
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Prev
                                                    IconButton(
                                                        onClick = { viewModel.prevMusicTrack() },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.SkipPrevious,
                                                            contentDescription = "Bài trước",
                                                            tint = Color.White.copy(alpha = 0.9f),
                                                            modifier = Modifier.size(17.dp)
                                                        )
                                                    }

                                                    // Play / Pause toggle
                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                Brush.linearGradient(
                                                                    listOf(Color(0xFFE11D48), Color(0xFFFB7185))
                                                                )
                                                            )
                                                            .clickable { viewModel.toggleMusicPlayPause() },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                            contentDescription = if (isMusicPlaying) "Dừng phát" else "Tiếp tục phát",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }

                                                    // Next
                                                    IconButton(
                                                        onClick = { viewModel.nextMusicTrack() },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.SkipNext,
                                                            contentDescription = "Bài kế tiếp",
                                                            tint = Color.White.copy(alpha = 0.9f),
                                                            modifier = Modifier.size(17.dp)
                                                        )
                                                    }
                                                }

                                                // Nút quét nhạc trên máy & thẻ nhớ
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color.White.copy(alpha = 0.08f))
                                                        .clickable {
                                                            val hasPerm = ContextCompat.checkSelfPermission(context, audioPermission) == PackageManager.PERMISSION_GRANTED
                                                            if (hasPerm) {
                                                                viewModel.scanDeviceMusic(context)
                                                            } else {
                                                                audioPermissionLauncher.launch(audioPermission)
                                                            }
                                                        }
                                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = musicScanStatus ?: "Quét nhạc trên máy & thẻ nhớ",
                                                        color = Color(0xFF38BDF8),
                                                        fontSize = 7.5.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        maxLines = 1
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.Refresh,
                                                        contentDescription = "Quét lại",
                                                        tint = Color(0xFF38BDF8),
                                                        modifier = Modifier.size(9.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                // Widget 2: Giá Thị Trường (3 trang: 0 = Cà Phê & Vàng, 1 = Giá Xăng, 2 = Giá Dầu)
                if (showMarketPrices) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.38f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(196.dp)
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.08f))
                                ),
                                RoundedCornerShape(20.dp)
                            )
                            .testTag("market_prices_desktop_widget")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Bar inside Right Card: Title + 3 Page Dots + Refresh
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.clickable {
                                        pagerScope.launch {
                                            val next = (marketPagerState.currentPage + 1) % 3
                                            marketPagerState.animateScrollToPage(next)
                                        }
                                    }
                                ) {
                                    val headerIcon = when (marketPagerState.currentPage) {
                                        0 -> Icons.Default.TrendingUp
                                        1 -> Icons.Default.LocalGasStation
                                        else -> Icons.Default.WaterDrop
                                    }
                                    val headerTint = when (marketPagerState.currentPage) {
                                        0 -> Color(0xFF34D399)
                                        1 -> Color(0xFFF97316)
                                        else -> Color(0xFF38BDF8)
                                    }
                                    val headerTitle = when (marketPagerState.currentPage) {
                                        0 -> "GIÁ CÀ PHÊ & VÀNG"
                                        1 -> "GIÁ XĂNG"
                                        else -> "GIÁ DẦU"
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(headerTint.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = headerIcon,
                                            contentDescription = null,
                                            tint = headerTint,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Text(
                                        text = headerTitle,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // 3 Page switcher dots
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(3) { idx ->
                                            Box(
                                                modifier = Modifier
                                                    .size(if (marketPagerState.currentPage == idx) 6.dp else 4.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (marketPagerState.currentPage == idx) {
                                                            when (idx) {
                                                                0 -> Color(0xFF34D399)
                                                                1 -> Color(0xFFF97316)
                                                                else -> Color(0xFF38BDF8)
                                                            }
                                                        } else Color.White.copy(alpha = 0.35f)
                                                    )
                                                    .clickable {
                                                        pagerScope.launch { marketPagerState.animateScrollToPage(idx) }
                                                    }
                                            )
                                        }
                                    }

                                    val isMarketRefreshing by viewModel.isMarketPricesRefreshing.collectAsState()
                                    val refreshTransition = rememberInfiniteTransition(label = "market_spin")
                                    val spinAngle by refreshTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 360f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(800, easing = LinearEasing),
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "market_rotation"
                                    )

                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Cập nhật",
                                        tint = if (isMarketRefreshing) Color(0xFF34D399) else Color.White.copy(alpha = 0.65f),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .graphicsLayer {
                                                if (isMarketRefreshing) rotationZ = spinAngle
                                            }
                                            .clickable {
                                                viewModel.triggerVibration()
                                                viewModel.fetchMarketPrices()
                                            }
                                    )
                                }
                            }

                            // Horizontal Pager: Page 0 = Cà phê (Lâm Đồng) & Giá Vàng; Page 1 = Giá Xăng; Page 2 = Giá Dầu
                            HorizontalPager(
                                state = marketPagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) { page ->
                                when (page) {
                                    0 -> {
                                        // Page 0: Coffee (Bỏ chữ 'giá tại :') & Gold (Click to open full gold dialog)
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = 4.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Coffee & Pepper section
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { viewModel.openCoffeeOnlineDialog() }
                                                    .padding(vertical = 1.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Cà phê
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                        ) {
                                                            Text(
                                                                text = "☕ Cà phê",
                                                                color = Color(0xFFFED7AA),
                                                                fontSize = 9.5.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                            Text(
                                                                text = "Lâm Đồng",
                                                                color = Color(0xFF60A5FA),
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                        val liveLamDongPrice = marketPrices?.lamDongCoffeePrice ?: "88.500 đ/kg"
                                                        Text(
                                                            text = liveLamDongPrice,
                                                            color = Color(0xFFFBBF24),
                                                            fontSize = 12.5.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    // Giá tiêu
                                                    Column(
                                                        horizontalAlignment = Alignment.End,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(
                                                            text = "🌱 Giá tiêu",
                                                            color = Color(0xFFA7F3D0),
                                                            fontSize = 9.5.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        val pepperPrice = marketPrices?.pepperPrice ?: "140.000 đ/kg"
                                                        Text(
                                                            text = pepperPrice,
                                                            color = Color(0xFF34D399),
                                                            fontSize = 12.5.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth().padding(top = 1.dp)
                                                ) {
                                                    val cleanNum = (marketPrices?.lamDongCoffeePrice ?: "88.500").replace(" đ/kg", "").trim()
                                                    Text(
                                                        text = "Di Linh/Bảo Lộc: $cleanNum đ",
                                                        color = Color.White.copy(alpha = 0.65f),
                                                        fontSize = 7.5.sp
                                                    )
                                                    Text(
                                                        text = "Tiêu: ${marketPrices?.pepperChange ?: "0 đ"}",
                                                        color = if ((marketPrices?.pepperChange ?: "").contains("-")) Color(0xFFF87171) else Color(0xFF34D399),
                                                        fontSize = 7.5.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                    modifier = Modifier.padding(top = 1.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFF34D399))
                                                    )
                                                    Text(
                                                        text = "Cập nhật: ${marketPrices?.lastUpdated ?: "Thời gian thực"}",
                                                        color = Color(0xFF34D399),
                                                        fontSize = 7.sp,
                                                        maxLines = 1
                                                    )
                                                }
                                            }

                                            // Gold section - click to open full gold prices as requested
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { viewModel.openGoldPriceDialog() }
                                                    .padding(vertical = 2.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "🥇 Giá Vàng",
                                                        color = Color(0xFFFEF08A),
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "Xem đầy đủ >",
                                                        color = Color(0xFF34D399),
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                val sjcGold = marketPrices?.goldPrices?.find { it.brand.contains("SJC") }
                                                    ?: marketPrices?.goldPrices?.firstOrNull()
                                                Text(
                                                    text = "SJC: ${sjcGold?.sellPrice ?: "142.000.000 đ"}",
                                                    color = Color(0xFF4ADE80),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                val ringGold = marketPrices?.goldPrices?.find { it.brand.contains("Nhẫn") }
                                                Text(
                                                    text = "Nhẫn 9999: ${ringGold?.sellPrice ?: "139.000.000 đ"}",
                                                    color = Color(0xFFFDE047),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            // Swipe right hint to Gasoline
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        pagerScope.launch { marketPagerState.animateScrollToPage(1) }
                                                    },
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Cuộn sang phải xem Giá Xăng 👉",
                                                    color = Color(0xFFF97316),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                    1 -> {
                                        // Page 1: Petrol prices (Giá Xăng) for Region 1, Region 2, Region 3
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = 2.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Loại xăng", color = Color.Gray, fontSize = 7.5.sp, modifier = Modifier.weight(1.1f))
                                                Text("Vùng 1", color = Color(0xFF60A5FA), fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                                                Text("Vùng 2", color = Color(0xFFFBBF24), fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                                                Text("Vùng 3", color = Color(0xFFF87171), fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                                            }

                                            val gasolineItems = marketPrices?.petrolPrices ?: listOf(
                                                com.example.model.PetrolPrice("RON 95-V", "21.490", "21.910", "22.340"),
                                                com.example.model.PetrolPrice("RON 95-III", "21.010", "21.430", "21.850"),
                                                com.example.model.PetrolPrice("E5 RON 92", "19.980", "20.370", "20.770")
                                            )

                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                gasolineItems.take(3).forEach { fuel ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = fuel.fuelType.replace("Xăng ", ""),
                                                            color = Color.White,
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier.weight(1.1f)
                                                        )
                                                        Text(
                                                            text = fuel.region1Price.replace(" đ/lít", ""),
                                                            color = Color(0xFF93C5FD),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.weight(0.9f)
                                                        )
                                                        Text(
                                                            text = fuel.region2Price.replace(" đ/lít", ""),
                                                            color = Color(0xFFFDE047),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.weight(0.9f)
                                                        )
                                                        Text(
                                                            text = fuel.region3Price.replace(" đ/lít", ""),
                                                            color = Color(0xFFFCA5A5),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.weight(0.9f)
                                                        )
                                                    }
                                                }
                                            }

                                            // Swipe hint to Oil
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        pagerScope.launch { marketPagerState.animateScrollToPage(2) }
                                                    },
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Cuộn sang phải xem Giá Dầu 👉",
                                                    color = Color(0xFF38BDF8),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                    2 -> {
                                        // Page 2: Oil prices (Giá Dầu) for Region 1, Region 2, Region 3
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = 2.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Loại dầu", color = Color.Gray, fontSize = 7.5.sp, modifier = Modifier.weight(1.1f))
                                                Text("Vùng 1", color = Color(0xFF60A5FA), fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                                                Text("Vùng 2", color = Color(0xFFFBBF24), fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                                                Text("Vùng 3", color = Color(0xFFF87171), fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                                            }

                                            val oilItems = marketPrices?.oilPrices ?: listOf(
                                                com.example.model.PetrolPrice("DO 0.001S", "19.380", "19.760", "20.150"),
                                                com.example.model.PetrolPrice("DO 0.05S", "18.980", "19.350", "19.730"),
                                                com.example.model.PetrolPrice("Dầu Hỏa", "19.120", "19.500", "19.880"),
                                                com.example.model.PetrolPrice("Mazút 180", "15.820", "16.130", "16.450")
                                            )

                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                oilItems.take(4).forEach { fuel ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = fuel.fuelType.replace("Dầu ", ""),
                                                            color = Color.White,
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier.weight(1.1f)
                                                        )
                                                        Text(
                                                            text = fuel.region1Price.replace(" đ/lít", "").replace(" đ/kg", ""),
                                                            color = Color(0xFF93C5FD),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.weight(0.9f)
                                                        )
                                                        Text(
                                                            text = fuel.region2Price.replace(" đ/lít", "").replace(" đ/kg", ""),
                                                            color = Color(0xFFFDE047),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.weight(0.9f)
                                                        )
                                                        Text(
                                                            text = fuel.region3Price.replace(" đ/lít", "").replace(" đ/kg", ""),
                                                            color = Color(0xFFFCA5A5),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.weight(0.9f)
                                                        )
                                                    }
                                                }
                                            }

                                            // Swipe back hint
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        pagerScope.launch { marketPagerState.animateScrollToPage(0) }
                                                    },
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "👈 Cuộn trái xem Cà phê & Vàng",
                                                    color = Color(0xFF34D399),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
                            badgeCount = viewModel.getBadgeCount(app.packageName),
                            onLaunch = { viewModel.launchApp(context, app) },
                            onUnpin = { viewModel.unpinAppFromDesktop(app.packageName) }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Swipe right guide banner to Launchpad (optional for clean desktop)
            if (showLaunchpadGuide) {
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.14f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLaunchpad() }
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(18.dp)
                        )
                        .testTag("launchpad_swipe_guide_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFF472B6), Color(0xFFEC4899))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = "Launchpad",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Tất cả ứng dụng (Launchpad)",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Vuốt sang phải / vuốt lên / chạm để mở",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
    badgeCount: Int,
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
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.22f),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(60.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.65f),
                                    Color.White.copy(alpha = 0.18f)
                                )
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (app.iconBitmap != null) {
                            Image(
                                bitmap = app.iconBitmap,
                                contentDescription = app.label,
                                modifier = Modifier.size(48.dp)
                            )
                        } else if (app.systemIconFallback != null) {
                            Icon(
                                imageVector = app.systemIconFallback,
                                contentDescription = app.label,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                // Notification Badge kèm số khi có thông báo
                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = 6.dp, y = (-4).dp)
                            .background(Color(0xFFFF3B30), CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .padding(horizontal = if (badgeCount > 9) 4.5.dp else 2.5.dp, vertical = 0.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
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
