package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarketPriceReport
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

@Composable
fun DesktopMarketBannerCard(
    marketPagerState: PagerState,
    marketPrices: MarketPriceReport,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val pagerScope = rememberCoroutineScope()
    val isMarketRefreshing by viewModel.isMarketPricesRefreshing.collectAsState()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.28f),
                        Color.White.copy(alpha = 0.12f),
                        Color.Black.copy(alpha = 0.35f)
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.90f),
                        Color.White.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.65f)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .testTag("desktop_market_banner_widget")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Top Specular Gloss Highlight (3D Liquid Glass Dome Shine)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.05f),
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
                // Header Bar: Icon + Page Title + 3 Page Switcher Dots + Refresh Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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
                        0 -> "GIÁ THỊ TRƯỜNG • CÀ PHÊ & VÀNG"
                        1 -> "GIÁ XĂNG DẦU • BẢNG GIÁ PETROLIMEX"
                        else -> "GIÁ DẦU • BẢNG GIÁ PETROLIMEX"
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable {
                            pagerScope.launch {
                                val next = (marketPagerState.currentPage + 1) % 3
                                marketPagerState.animateScrollToPage(next)
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

                        // Live green pulse dot
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399))
                        )
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

                        // Refresh button with rotation animation
                        val refreshTransition = rememberInfiniteTransition(label = "market_refresh_spin")
                        val spinAngle by refreshTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "spin_angle"
                        )

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Cập nhật giá",
                            tint = if (isMarketRefreshing) Color(0xFF34D399) else Color.White.copy(alpha = 0.75f),
                            modifier = Modifier
                                .size(16.dp)
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

                // Horizontal Pager Content
                HorizontalPager(
                    state = marketPagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    when (page) {
                        0 -> {
                            // Page 0: 3 horizontal columns across full screen width (Cà phê, Hồ tiêu, Vàng SJC)
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Column 1: Cà phê (Lâm Đồng)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.08f),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(
                                            0.8.dp,
                                            Color.White.copy(alpha = 0.20f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.openCoffeeOnlineDialog() }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 6.dp, vertical = 5.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "☕ Cà phê",
                                                color = Color(0xFFFED7AA),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "L.Đồng",
                                                color = Color(0xFF93C5FD),
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        val liveLamDongPrice = marketPrices.lamDongCoffeePrice
                                        Text(
                                            text = liveLamDongPrice,
                                            color = Color(0xFFFBBF24),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        val cleanNum = liveLamDongPrice.replace(" đ/kg", "").trim()
                                        Text(
                                            text = "Di Linh: $cleanNum đ",
                                            color = Color.White.copy(alpha = 0.65f),
                                            fontSize = 7.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Column 2: Giá Hồ tiêu
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.08f),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(
                                            0.8.dp,
                                            Color.White.copy(alpha = 0.20f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.openCoffeeOnlineDialog() }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 6.dp, vertical = 5.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "🌱 Hồ tiêu",
                                                color = Color(0xFFA7F3D0),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            val rawPepperChange = marketPrices.pepperChange.replace(" đ/kg", "").replace(" đ", "").trim()
                                            val displayChange = if (rawPepperChange.startsWith("-") || rawPepperChange.startsWith("+") || rawPepperChange == "0") rawPepperChange else "+$rawPepperChange"
                                            Text(
                                                text = displayChange,
                                                color = if (displayChange.contains("-")) Color(0xFFF87171) else Color(0xFF34D399),
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        val pepperPrice = marketPrices.pepperPrice
                                        Text(
                                            text = pepperPrice,
                                            color = Color(0xFF34D399),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "Đắk Lắk • Gia Lai",
                                            color = Color.White.copy(alpha = 0.65f),
                                            fontSize = 7.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Column 3: Giá Vàng SJC
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.08f),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(
                                            0.8.dp,
                                            Color.White.copy(alpha = 0.20f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.openGoldPriceDialog() }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 6.dp, vertical = 5.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "👑 Vàng SJC",
                                                color = Color(0xFFFDE047),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Bảng vàng ↗",
                                                color = Color(0xFFFDE047).copy(alpha = 0.8f),
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        val sjcSell = marketPrices.goldPrices.firstOrNull { it.brand.contains("SJC") }?.sellPrice ?: "142.000.000 đ"
                                        Text(
                                            text = sjcSell,
                                            color = Color(0xFFFDE047),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        val nhanSell = marketPrices.goldPrices.firstOrNull { it.brand.contains("Nhẫn") }?.sellPrice ?: "139.000.000 đ"
                                        Text(
                                            text = "Nhẫn: $nhanSell",
                                            color = Color.White.copy(alpha = 0.70f),
                                            fontSize = 7.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Page 1: Giá Xăng (RON 95-III, E5 RON 92-II, RON 95-V arranged horizontally)
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val gasItems = listOf(
                                    Triple("RON 95-III", "19.820 đ", "Vùng 2: 20.210 đ"),
                                    Triple("E5 RON 92-II", "19.340 đ", "Vùng 2: 19.720 đ"),
                                    Triple("RON 95-V", "20.650 đ", "Vùng 2: 21.060 đ")
                                )

                                gasItems.forEach { item ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White.copy(alpha = 0.08f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .border(0.8.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 6.dp, vertical = 5.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = item.first,
                                                color = Color(0xFFFED7AA),
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = item.second,
                                                color = Color(0xFFF97316),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = item.third,
                                                color = Color.White.copy(alpha = 0.65f),
                                                fontSize = 7.5.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Page 2: Giá Dầu (Dầu DO 0.05S-II, Dầu Hỏa, Dầu Mazut arranged horizontally)
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val oilItems = listOf(
                                    Triple("Dầu DO 0.05S", "18.460 đ", "Vùng 2: 18.820 đ"),
                                    Triple("Dầu Hỏa", "18.820 đ", "Vùng 2: 19.190 đ"),
                                    Triple("Dầu Mazut", "16.020 đ", "Vùng 2: 16.340 đ")
                                )

                                oilItems.forEach { item ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White.copy(alpha = 0.08f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .border(0.8.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 6.dp, vertical = 5.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = item.first,
                                                color = Color(0xFFBAE6FD),
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = item.second,
                                                color = Color(0xFF38BDF8),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = item.third,
                                                color = Color.White.copy(alpha = 0.65f),
                                                fontSize = 7.5.sp,
                                                maxLines = 1
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
