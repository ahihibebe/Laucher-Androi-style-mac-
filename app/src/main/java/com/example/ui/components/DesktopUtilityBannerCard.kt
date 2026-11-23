package com.example.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.model.LunarDate
import com.example.viewmodel.LauncherMusicTrack
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

internal fun widgetGlassBrush(widgetAlpha: Float): Brush {
    val a = widgetAlpha.coerceIn(0f, 1f)
    if (a <= 0.02f) {
        return Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
    }
    return if (a <= 0.50f) {
        val ratio = a / 0.40f
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = (0.40f * ratio).coerceIn(0f, 0.45f)),
                Color.White.copy(alpha = (0.22f * ratio).coerceIn(0f, 0.25f)),
                Color.White.copy(alpha = (0.32f * ratio).coerceIn(0f, 0.36f))
            )
        )
    } else {
        val t = (a - 0.50f) / 0.50f
        val topAlpha = (0.45f + 0.42f * t).coerceIn(0f, 0.88f)
        val midAlpha = (0.30f + 0.48f * t).coerceIn(0f, 0.80f)
        val botAlpha = (0.38f + 0.44f * t).coerceIn(0f, 0.84f)
        Brush.verticalGradient(
            listOf(
                Color(0xFF1E293B).copy(alpha = topAlpha),
                Color(0xFF0F172A).copy(alpha = midAlpha),
                Color(0xFF1E293B).copy(alpha = botAlpha)
            )
        )
    }
}

internal fun widgetBorderBrush(widgetAlpha: Float): Brush {
    val a = widgetAlpha.coerceIn(0f, 1f)
    if (a <= 0.02f) {
        return Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
    }
    val factor = if (a <= 0.50f) (a / 0.40f).coerceIn(0f, 1f) else 0.70f + 0.30f * ((a - 0.50f) / 0.50f)
    return Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.85f * factor),
            Color.White.copy(alpha = 0.32f * factor),
            Color.White.copy(alpha = 0.65f * factor)
        )
    )
}

internal fun widgetGlossAlpha(widgetAlpha: Float): Float {
    val a = widgetAlpha.coerceIn(0f, 1f)
    if (a <= 0.02f) return 0f
    return if (a <= 0.50f) {
        (a / 0.40f).coerceIn(0f, 1f)
    } else {
        (1f - (a - 0.50f) / 0.50f * 0.65f).coerceIn(0.2f, 1f)
    }
}

internal fun widgetSubCardAlpha(widgetAlpha: Float): Float {
    val a = widgetAlpha.coerceIn(0f, 1f)
    if (a <= 0.02f) return 0f
    return if (a <= 0.50f) {
        (0.12f * (a / 0.40f)).coerceIn(0f, 0.16f)
    } else {
        (0.14f + 0.10f * ((a - 0.50f) / 0.50f)).coerceIn(0.14f, 0.24f)
    }
}

@Composable
fun DesktopUtilityBannerCard(
    leftPagerState: PagerState,
    formattedTime: String,
    formattedDate: String,
    lunarDate: LunarDate,
    showClock: Boolean = true,
    showLunar: Boolean = true,
    showCanChi: Boolean,
    bankQrUri: String?,
    bankQrFitMode: String,
    bankQrScale: Float,
    onPickBankQr: () -> Unit,
    currentTrack: LauncherMusicTrack?,
    isMusicPlaying: Boolean,
    musicProgressSeconds: Int,
    musicScanStatus: String?,
    audioPermission: String,
    audioPermissionLauncher: ActivityResultLauncher<String>,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pagerScope = rememberCoroutineScope()
    val widgetAlpha by viewModel.widgetAlpha.collectAsState()

    val widgetShape = RoundedCornerShape(26.dp)
    val glassBrush = widgetGlassBrush(widgetAlpha)
    val borderBrush = widgetBorderBrush(widgetAlpha)
    val glossFactor = widgetGlossAlpha(widgetAlpha)
    val subCardAlpha = widgetSubCardAlpha(widgetAlpha)

    // Outer Liquid Glass Container - strictly clipped to 26.dp so background fills 100% full-bleed
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(widgetShape)
            .background(glassBrush)
            .border(
                width = if (widgetAlpha <= 0.02f) 0.dp else 1.2.dp,
                brush = borderBrush,
                shape = widgetShape
            )
            .testTag("desktop_utility_banner_widget")
    ) {
        // Top Specular Gloss Highlight (3D Liquid Glass Dome Shine)
        if (glossFactor > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.35f * glossFactor),
                                Color.White.copy(alpha = 0.08f * glossFactor),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 11.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Bar: Icon + Page Title + 3 Page Switcher Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val headerIcon = when (leftPagerState.currentPage) {
                        0 -> Icons.Default.CalendarMonth
                        1 -> Icons.Default.QrCode2
                        else -> Icons.Default.MusicNote
                    }
                    val headerTint = when (leftPagerState.currentPage) {
                        0 -> Color(0xFFFFB74D)
                        1 -> Color(0xFF38BDF8)
                        else -> Color(0xFFF43F5E)
                    }
                    val headerTitle = when (leftPagerState.currentPage) {
                        0 -> "LỊCH VẠN NIÊN & ĐỒNG HỒ"
                        1 -> "MÃ QR NGÂN HÀNG"
                        else -> "TRÌNH PHÁT NHẠC"
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable {
                            pagerScope.launch {
                                val next = (leftPagerState.currentPage + 1) % 3
                                leftPagerState.animateScrollToPage(next)
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(headerTint.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = headerIcon,
                                contentDescription = null,
                                tint = headerTint,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        Text(
                            text = headerTitle,
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (leftPagerState.currentPage == 0) {
                            Text(
                                text = "• Chi tiết ↗",
                                color = Color(0xFF60A5FA),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { viewModel.openFullCalendarDialog() }
                                    .padding(horizontal = 3.dp)
                            )
                        } else if (leftPagerState.currentPage == 2) {
                            Text(
                                text = "• Mở trình phát ↗",
                                color = Color(0xFFFDA4AF),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { viewModel.openSupportedMusicPlayer(context) }
                                    .padding(horizontal = 3.dp)
                                    .testTag("open_supported_music_player_header")
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 3 Page Switcher Dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                                                    0 -> Color(0xFFFFB74D)
                                                    1 -> Color(0xFF38BDF8)
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
            }

                // Horizontal Pager Content
                HorizontalPager(
                    state = leftPagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    when (page) {
                        0 -> {
                            // Page 0: Lịch Vạn Niên & Đồng Hồ
                            if (showClock && showLunar) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { viewModel.openFullCalendarDialog() },
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left Column: Large Digital Clock & Solar Date
                                    Column(
                                        modifier = Modifier.weight(0.95f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = formattedTime,
                                            color = Color.White,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 29.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = formattedDate,
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Dấu gạch đứng ngăn cách giờ và ngày âm lịch
                                    Box(
                                        modifier = Modifier
                                            .width(1.5.dp)
                                            .height(48.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.45f),
                                                shape = RoundedCornerShape(1.dp)
                                            )
                                    )

                                    // Right Column: Lunar Date & Can Chi (không có viền bo)
                                    Column(
                                        modifier = Modifier
                                            .weight(1.35f)
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Âm lịch: Ngày ${lunarDate.day}/${lunarDate.month}${if (lunarDate.isLeap) " (Nhuận)" else ""}",
                                                color = Color(0xFFFDE047),
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Xem lịch ↗",
                                                color = Color(0xFF93C5FD),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))

                                        if (showCanChi) {
                                            Text(
                                                text = "Năm ${lunarDate.canChiYear} • Giờ ${lunarDate.canChiDay}",
                                                color = Color.White.copy(alpha = 0.85f),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                        }

                                        Text(
                                            text = "Tiết khí: ${com.example.util.LunarCalendarHelper.getTietKhi(lunarDate.solarMonth, lunarDate.solarDay)}",
                                            color = Color.White.copy(alpha = 0.65f),
                                            fontSize = 8.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            } else if (showClock) {
                                // Only Clock visible
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { viewModel.openFullCalendarDialog() },
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = formattedTime,
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = formattedDate,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                // Only Lunar Calendar visible (không có viền bo)
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { viewModel.openFullCalendarDialog() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalArrangement = Arrangement.SpaceAround
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Âm lịch: Ngày ${lunarDate.day}/${lunarDate.month}${if (lunarDate.isLeap) " (Nhuận)" else ""}",
                                            color = Color(0xFFFDE047),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Xem lịch đầy đủ ↗",
                                            color = Color(0xFF93C5FD),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    if (showCanChi) {
                                        Text(
                                            text = "Năm ${lunarDate.canChiYear} • Giờ ${lunarDate.canChiDay}",
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Text(
                                        text = "Tiết khí: ${com.example.util.LunarCalendarHelper.getTietKhi(lunarDate.solarMonth, lunarDate.solarDay)}",
                                        color = Color.White.copy(alpha = 0.70f),
                                        fontSize = 9.5.sp
                                    )
                                }
                            }
                        }
                        1 -> {
                            // Page 1: Mã QR Ngân Hàng (Horizontal Layout)
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (bankQrUri != null) {
                                    // QR Thumbnail (clickable to zoom)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        shadowElevation = 6.dp,
                                        modifier = Modifier
                                            .size(76.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { viewModel.openBankQrZoom() }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(3.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    ImageRequest.Builder(context)
                                                        .data(bankQrUri)
                                                        .crossfade(true)
                                                        .build()
                                                ),
                                                contentDescription = "Mã QR Ngân hàng",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .graphicsLayer {
                                                        scaleX = bankQrScale
                                                        scaleY = bankQrScale
                                                    },
                                                contentScale = if (bankQrFitMode == "fit") ContentScale.Fit else ContentScale.Crop
                                            )
                                        }
                                    }

                                    // QR Controls & Info
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Text(
                                            text = "Mã QR chuyển khoản ngân hàng",
                                            color = Color.White,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Chạm mã để phóng to quét thanh toán",
                                            color = Color.White.copy(alpha = 0.70f),
                                            fontSize = 9.sp,
                                            maxLines = 1
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFF0284C7).copy(alpha = 0.5f),
                                                modifier = Modifier.clickable { viewModel.openBankQrZoom() }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Icon(Icons.Default.ZoomIn, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                                    Text("Phóng to", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.White.copy(alpha = 0.15f),
                                                modifier = Modifier.clickable { onPickBankQr() }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFFDE047), modifier = Modifier.size(11.dp))
                                                    Text("Đổi ảnh QR", color = Color(0xFFFDE047), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Empty State: Click directly to select QR image
                                    Surface(
                                        shape = RoundedCornerShape(15.dp),
                                        color = Color.White.copy(alpha = 0.12f),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            brush = Brush.verticalGradient(
                                                listOf(
                                                    Color.White.copy(alpha = 0.50f),
                                                    Color.White.copy(alpha = 0.15f)
                                                )
                                            )
                                        ),
                                        shadowElevation = 0.dp,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable { onPickBankQr() }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xFF38BDF8).copy(alpha = 0.25f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AddPhotoAlternate,
                                                    contentDescription = null,
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }

                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "Chọn ảnh mã QR Ngân Hàng",
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Chạm vào đây để tải ảnh QR từ máy / thẻ nhớ",
                                                    color = Color(0xFF7DD3FC),
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Page 2: Trình Phát Nhạc
                            if (currentTrack == null) {
                                // Khi chưa có nhạc: Nhấn trực tiếp vào chữ / thẻ để quét nhạc (không bị tràn chữ xuống dưới)
                                val triggerScan = {
                                    val hasPerm = ContextCompat.checkSelfPermission(context, audioPermission) == PackageManager.PERMISSION_GRANTED
                                    if (hasPerm) {
                                        viewModel.scanDeviceMusic(context)
                                    } else {
                                        audioPermissionLauncher.launch(audioPermission)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(15.dp),
                                    color = Color.White.copy(alpha = subCardAlpha),
                                    border = if (widgetAlpha <= 0.02f) null else BorderStroke(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.50f),
                                                Color.White.copy(alpha = 0.15f)
                                            )
                                        )
                                    ),
                                    shadowElevation = 0.dp,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { viewModel.openSupportedMusicPlayer(context) }
                                        .testTag("music_widget_empty_open_player")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFFF43F5E), Color(0xFFBE123C))
                                                    )
                                                )
                                                .clickable { viewModel.openSupportedMusicPlayer(context) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MusicNote,
                                                contentDescription = "Mở trình phát nhạc hỗ trợ",
                                                tint = Color.White,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { viewModel.openSupportedMusicPlayer(context) },
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Chạm để mở trình phát nhạc",
                                                color = Color.White,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = musicScanStatus ?: "Hoặc nhấn Quét để phát nhạc trên máy",
                                                color = Color(0xFFFDA4AF),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFE11D48).copy(alpha = 0.85f),
                                            modifier = Modifier.clickable { triggerScan() }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                                Text("Quét", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Track playing / ready: Horizontal controls with seek bar & open external player
                                val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotate")
                                val vinylRotation by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 360f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(4000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Restart
                                    ),
                                    label = "vinyl_angle"
                                )

                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Rotating vinyl disc (tap to open supported music player)
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(Color(0xFF334155), Color(0xFF0F172A))
                                                )
                                            )
                                            .border(1.5.dp, Color(0xFFF43F5E).copy(alpha = 0.6f), CircleShape)
                                            .clickable { viewModel.openSupportedMusicPlayer(context) }
                                            .graphicsLayer {
                                                if (isMusicPlaying) rotationZ = vinylRotation
                                            }
                                            .testTag("music_widget_disc_open_player"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF43F5E)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.MusicNote,
                                                contentDescription = "Mở trình phát nhạc",
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }

                                    // Song info & interactive seek + media controls
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.openSupportedMusicPlayer(context) }
                                                .testTag("music_widget_title_open_player"),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = currentTrack.title,
                                                    color = Color.White,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = currentTrack.artist,
                                                    color = Color.White.copy(alpha = 0.75f),
                                                    fontSize = 9.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Icon(
                                                imageVector = Icons.Default.OpenInNew,
                                                contentDescription = "Mở trình phát nhạc hỗ trợ",
                                                tint = Color(0xFFFDA4AF),
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }

                                        // Interactive Seek Bar (chạm hoặc kéo để tua nhạc)
                                        val durationSec = currentTrack.totalSeconds.coerceAtLeast(1)
                                        val progressFraction = (musicProgressSeconds.toFloat() / durationSec.toFloat()).coerceIn(0f, 1f)
                                        var isSeeking by remember { mutableStateOf(false) }
                                        var seekFraction by remember { mutableFloatStateOf(progressFraction) }
                                        val displayFraction = if (isSeeking) seekFraction else progressFraction

                                        BoxWithConstraints(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(14.dp)
                                                .pointerInput(durationSec) {
                                                    detectTapGestures { offset ->
                                                        val fraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                                                        viewModel.seekMusicTo((fraction * durationSec).roundToInt())
                                                    }
                                                }
                                                .pointerInput(durationSec) {
                                                    detectHorizontalDragGestures(
                                                        onDragStart = { offset ->
                                                            isSeeking = true
                                                            seekFraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                                                        },
                                                        onHorizontalDrag = { change, _ ->
                                                            change.consume()
                                                            seekFraction = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                                                        },
                                                        onDragEnd = {
                                                            viewModel.seekMusicTo((seekFraction * durationSec).roundToInt())
                                                            isSeeking = false
                                                        },
                                                        onDragCancel = {
                                                            isSeeking = false
                                                        }
                                                    )
                                                }
                                                .testTag("music_seek_bar"),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            val density = LocalDensity.current
                                            val trackWidthPx = with(density) { maxWidth.toPx() }
                                            val thumbSizePx = with(density) { 10.dp.toPx() }
                                            val thumbOffsetPx = ((trackWidthPx - thumbSizePx) * displayFraction).roundToInt().coerceAtLeast(0)

                                            // Track background
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(Color.White.copy(alpha = 0.22f))
                                            ) {
                                                // Active progress fill
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(displayFraction)
                                                        .fillMaxHeight()
                                                        .background(Color(0xFFF43F5E))
                                                )
                                            }

                                            // Thumb dot
                                            Box(
                                                modifier = Modifier
                                                    .offset { IntOffset(thumbOffsetPx, 0) }
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White)
                                                    .border(1.5.dp, Color(0xFFF43F5E), CircleShape)
                                            )
                                        }

                                        // Controls row: Time + (-10s, Prev, Play/Pause, Next, +10s)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val displayedSec = if (isSeeking) (seekFraction * durationSec).roundToInt() else musicProgressSeconds
                                            val posMin = displayedSec / 60
                                            val posSec = displayedSec % 60
                                            val durMin = durationSec / 60
                                            val durSec = durationSec % 60
                                            Text(
                                                text = String.format("%02d:%02d / %02d:%02d", posMin, posSec, durMin, durSec),
                                                color = Color.White.copy(alpha = 0.70f),
                                                fontSize = 8.sp
                                            )

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Replay10,
                                                    contentDescription = "Tua lại 10 giây",
                                                    tint = Color.White.copy(alpha = 0.9f),
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable { viewModel.seekMusicBy(-10) }
                                                        .testTag("music_rewind_10s")
                                                )

                                                Icon(
                                                    Icons.Default.SkipPrevious,
                                                    contentDescription = "Bài trước",
                                                    tint = Color.White,
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clickable { viewModel.prevMusicTrack() }
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFF43F5E))
                                                        .clickable { viewModel.toggleMusicPlayPause() },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = if (isMusicPlaying) "Tạm dừng" else "Phát",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }

                                                Icon(
                                                    Icons.Default.SkipNext,
                                                    contentDescription = "Bài kế tiếp",
                                                    tint = Color.White,
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clickable { viewModel.nextMusicTrack() }
                                                )

                                                Icon(
                                                    Icons.Default.Forward10,
                                                    contentDescription = "Tua tới 10 giây",
                                                    tint = Color.White.copy(alpha = 0.9f),
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable { viewModel.seekMusicBy(10) }
                                                        .testTag("music_forward_10s")
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
        }
    }
