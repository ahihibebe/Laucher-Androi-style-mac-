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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.model.LunarDate
import com.example.viewmodel.LauncherMusicTrack
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

@Composable
fun DesktopUtilityBannerCard(
    leftPagerState: PagerState,
    formattedTime: String,
    formattedDate: String,
    lunarDate: LunarDate,
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

    // Outer Liquid Glass Container - Surface paints background & border seamlessly edge-to-edge
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = Color.White.copy(alpha = 0.18f),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.90f),
                    Color.White.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.65f)
                )
            )
        ),
        shadowElevation = 14.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(134.dp)
            .testTag("desktop_utility_banner_widget")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.White.copy(alpha = 0.08f),
                            Color.Black.copy(alpha = 0.30f)
                        )
                    )
                )
        ) {
            // Top Specular Gloss Highlight (3D Liquid Glass Dome Shine)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.38f),
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
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
                        }
                    }

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

                // Horizontal Pager Content
                HorizontalPager(
                    state = leftPagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    when (page) {
                        0 -> {
                            // Page 0: Lịch Vạn Niên & Đồng Hồ (Horizontal 2 columns)
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
                                        fontSize = 27.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 28.sp
                                    )
                                    Text(
                                        text = formattedDate,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Right Column: Optimized 3D Frosted Glass Subcard for Lunar Date & Can Chi
                                Surface(
                                    shape = RoundedCornerShape(15.dp),
                                    color = Color.White.copy(alpha = 0.14f),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.55f),
                                                Color.White.copy(alpha = 0.15f)
                                            )
                                        )
                                    ),
                                    shadowElevation = 3.dp,
                                    modifier = Modifier
                                        .weight(1.35f)
                                        .fillMaxHeight()
                                        .clickable { viewModel.openFullCalendarDialog() }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 9.dp, vertical = 6.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
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

                                        if (showCanChi) {
                                            Text(
                                                text = "Năm ${lunarDate.canChiYear} • Giờ ${lunarDate.canChiDay}",
                                                color = Color.White.copy(alpha = 0.85f),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
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
                                        shadowElevation = 3.dp,
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
                                    shadowElevation = 3.dp,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { triggerScan() }
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
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFFF43F5E), Color(0xFFBE123C))
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Chưa có nhạc • Chạm để quét ngay",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = musicScanStatus ?: "Chạm vào chữ này để quét bài hát từ máy hoặc thẻ nhớ",
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
                                // Track playing / ready: Horizontal controls
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
                                    // Rotating vinyl disc or album art
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
                                            .graphicsLayer {
                                                if (isMusicPlaying) rotationZ = vinylRotation
                                            },
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
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }

                                    // Song info & media controls
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
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

                                        // Progress bar
                                        val durationSec = currentTrack.totalSeconds.coerceAtLeast(1)
                                        val progressFraction = (musicProgressSeconds.toFloat() / durationSec.toFloat()).coerceIn(0f, 1f)
                                        LinearProgressIndicator(
                                            progress = { progressFraction },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(3.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = Color(0xFFF43F5E),
                                            trackColor = Color.White.copy(alpha = 0.2f)
                                        )

                                        // Controls row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val posMin = musicProgressSeconds / 60
                                            val posSec = musicProgressSeconds % 60
                                            val durMin = durationSec / 60
                                            val durSec = durationSec % 60
                                            Text(
                                                text = String.format("%02d:%02d / %02d:%02d", posMin, posSec, durMin, durSec),
                                                color = Color.White.copy(alpha = 0.65f),
                                                fontSize = 8.sp
                                            )

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
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
}
