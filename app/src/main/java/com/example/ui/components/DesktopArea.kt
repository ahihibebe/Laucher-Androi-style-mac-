package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LunarDate
import com.example.model.MarketPriceReport
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.SettingsCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    onNavigateToLaunchpad: () -> Unit,
    onOpenPhotoPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val timeFormat = if (is24Hour) "HH:mm" else "hh:mm a"
    val timeFormatter = remember(is24Hour) { SimpleDateFormat(timeFormat, Locale.getDefault()) }
    val fullDateFormatter = remember { SimpleDateFormat("EEEE, d/M", Locale("vi", "VN")) }

    val formattedTime = remember(currentTime.timeInMillis, is24Hour) { timeFormatter.format(currentTime.time) }
    val formattedDate = remember(currentTime.timeInMillis) { fullDateFormatter.format(currentTime.time) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* tap background */ },
                onLongClick = { viewModel.openDesktopContextMenu() }
            )
            .testTag("desktop_background_area")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Symmetrical Cards: Left = Lịch Vạn Niên & Giờ; Right = Giá Cà Phê & Giá Vàng Nhẫn
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Widget 1: Lịch vạn niên & Giờ (Left symmetrical card)
                if (showClock || showLunar) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(180.dp)
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.08f))
                                ),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.openSettingsDialog(SettingsCategory.CLOCK_AND_LUNAR) }
                            .testTag("clock_lunar_desktop_widget")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFB74D).copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB74D),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = "LỊCH VẠN NIÊN",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Center: Clock
                            if (showClock) {
                                Column {
                                    Text(
                                        text = formattedTime,
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = formattedDate,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.5.sp
                                    )
                                }
                            }

                            // Bottom: Lunar Date
                            if (showLunar) {
                                Column {
                                    Text(
                                        text = "Âm: ${lunarDate.day}/${lunarDate.month}${if (lunarDate.isLeap) " (Nhuận)" else ""}",
                                        color = Color(0xFFFDE047),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (showCanChi) {
                                        Text(
                                            text = "Năm ${lunarDate.canChiYear}",
                                            color = Color.White.copy(alpha = 0.75f),
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Widget 2: Giá Cà Phê & Giá Vàng Nhẫn (Right symmetrical card)
                if (showMarketPrices) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(180.dp)
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.08f))
                                ),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.openSettingsDialog(SettingsCategory.MARKET_PRICES) }
                            .testTag("market_prices_desktop_widget")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399).copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = Color(0xFF34D399),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Text(
                                        text = "GIÁ THỊ TRƯỜNG",
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Cập nhật",
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { viewModel.fetchMarketPrices() }
                                )
                            }

                            // Coffee price section (giacaphe.com)
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "☕ Cà phê:",
                                        color = Color(0xFFFED7AA),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "giacaphe.com",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 9.sp
                                    )
                                }
                                Text(
                                    text = marketPrices?.averageCoffeePrice ?: "121.800 đ/kg",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Gold ring price section (Vàng nhẫn)
                            Column {
                                Text(
                                    text = "🥇 Vàng Nhẫn 9999:",
                                    color = Color(0xFFFEF08A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                val gold = marketPrices?.goldPrices?.firstOrNull()
                                Text(
                                    text = "Bán: ${gold?.sellPrice ?: "89.200.000 đ"}",
                                    color = Color(0xFF4ADE80),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mua: ${gold?.buyPrice ?: "87.900.000 đ"}",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Swipe right guide banner to Launchpad
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
                                text = "Vuốt sang phải 👉 hoặc nhấn giữ màn hình để cài đặt",
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

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
