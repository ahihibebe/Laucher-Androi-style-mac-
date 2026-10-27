package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dock
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.AppInfo
import com.example.model.LunarDate
import com.example.model.MarketPriceReport
import com.example.model.WallpaperPresets
import com.example.model.WallpaperTheme
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.SettingsCategory

@Composable
fun MacSettingsDialog(
    viewModel: LauncherViewModel,
    selectedCategory: SettingsCategory,
    showClock: Boolean,
    showLunar: Boolean,
    showMarketPrices: Boolean,
    marketPrices: MarketPriceReport?,
    is24Hour: Boolean,
    showCanChi: Boolean,
    currentWallpaper: WallpaperTheme,
    customWallpaperUri: String?,
    dockApps: List<AppInfo>,
    lunarDate: LunarDate,
    onPickImageFromDevice: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E1E1E),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .shadow(24.dp, RoundedCornerShape(20.dp))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .testTag("macos_settings_window")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Window Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(Color(0xFF2B2B2B))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Traffic lights
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5F56))
                                .clickable { onDismiss() }
                                .testTag("window_traffic_red_close")
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFBD2E))
                                .clickable { onDismiss() }
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF27C93F))
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Cài đặt hệ thống macOs",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Window Body
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Sidebar
                    Column(
                        modifier = Modifier
                            .width(125.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF252525))
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                    ) {
                        SettingsSidebarItem(
                            icon = Icons.Default.Schedule,
                            label = "Đồng hồ & Lịch âm",
                            isSelected = selectedCategory == SettingsCategory.CLOCK_AND_LUNAR,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.CLOCK_AND_LUNAR) }
                        )
                        SettingsSidebarItem(
                            icon = Icons.Default.TrendingUp,
                            label = "Giá Cà phê & Vàng",
                            isSelected = selectedCategory == SettingsCategory.MARKET_PRICES,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.MARKET_PRICES) }
                        )
                        SettingsSidebarItem(
                            icon = Icons.Default.Dock,
                            label = "Thanh Dock",
                            isSelected = selectedCategory == SettingsCategory.DOCK,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.DOCK) }
                        )
                        SettingsSidebarItem(
                            icon = Icons.Default.Wallpaper,
                            label = "Hình nền",
                            isSelected = selectedCategory == SettingsCategory.WALLPAPER,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.WALLPAPER) }
                        )
                        SettingsSidebarItem(
                            icon = Icons.Default.Settings,
                            label = "Cài đặt chung",
                            isSelected = selectedCategory == SettingsCategory.GENERAL,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.GENERAL) }
                        )
                        SettingsSidebarItem(
                            icon = Icons.Default.Info,
                            label = "Giới thiệu",
                            isSelected = selectedCategory == SettingsCategory.ABOUT,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.ABOUT) }
                        )
                    }

                    // Right Content Area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFF1E1E1E))
                            .padding(14.dp)
                    ) {
                        when (selectedCategory) {
                            SettingsCategory.CLOCK_AND_LUNAR -> {
                                ClockAndLunarSettingsPane(
                                    viewModel = viewModel,
                                    showClock = showClock,
                                    showLunar = showLunar,
                                    is24Hour = is24Hour,
                                    showCanChi = showCanChi,
                                    lunarDate = lunarDate
                                )
                            }
                            SettingsCategory.MARKET_PRICES -> {
                                MarketPricesSettingsPane(
                                    viewModel = viewModel,
                                    showMarketPrices = showMarketPrices,
                                    marketPrices = marketPrices
                                )
                            }
                            SettingsCategory.DOCK -> {
                                DockSettingsPane(
                                    viewModel = viewModel,
                                    dockApps = dockApps
                                )
                            }
                            SettingsCategory.WALLPAPER -> {
                                WallpaperSettingsPane(
                                    viewModel = viewModel,
                                    currentWallpaper = currentWallpaper,
                                    customWallpaperUri = customWallpaperUri,
                                    onPickImage = onPickImageFromDevice
                                )
                            }
                            SettingsCategory.GENERAL -> {
                                GeneralSettingsPane(
                                    viewModel = viewModel
                                )
                            }
                            SettingsCategory.ABOUT -> {
                                AboutSettingsPane()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSidebarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF007AFF) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else Color.LightGray,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.LightGray,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 2,
            lineHeight = 13.sp
        )
    }
    Spacer(modifier = Modifier.height(3.dp))
}

@Composable
private fun ClockAndLunarSettingsPane(
    viewModel: LauncherViewModel,
    showClock: Boolean,
    showLunar: Boolean,
    is24Hour: Boolean,
    showCanChi: Boolean,
    lunarDate: LunarDate
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Đồng hồ & Lịch âm",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tùy chỉnh bật tắt hiển thị thời gian và Âm lịch Việt Nam",
            color = Color.Gray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF282828),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Switch 1: Toggle Clock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hiển thị đồng hồ", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Bật/tắt giờ trên widget và thanh menu", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = showClock,
                        onCheckedChange = { viewModel.toggleClock(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF)),
                        modifier = Modifier.testTag("toggle_clock_switch")
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

                // Switch 2: Toggle Lunar Calendar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hiển thị lịch âm (Việt Nam)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Chuẩn thiên văn học múi giờ UTC+7", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = showLunar,
                        onCheckedChange = { viewModel.toggleLunar(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF)),
                        modifier = Modifier.testTag("toggle_lunar_switch")
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

                // Switch 3: Can Chi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hiển thị Can Chi", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Can Chi Năm, Tháng và Ngày", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = showCanChi,
                        onCheckedChange = { viewModel.toggleCanChi(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

                // Switch 4: 24h format
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Định dạng giờ 24h", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("14:30 thay vì 02:30 PM", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = is24Hour,
                        onCheckedChange = { viewModel.toggle24Hour(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketPricesSettingsPane(
    viewModel: LauncherViewModel,
    showMarketPrices: Boolean,
    marketPrices: MarketPriceReport?
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Giá Cà phê & Vàng",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dữ liệu cập nhật từ trang giacaphe.com",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = { viewModel.fetchMarketPrices() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Tải lại", tint = Color(0xFF34D399))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Toggle Switch
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF282828),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hiển thị widget Giá Cà phê & Vàng",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Nằm đối xứng với widget Lịch vạn niên & giờ",
                        color = Color.Gray,
                        fontSize = 10.5.sp
                    )
                }
                Switch(
                    checked = showMarketPrices,
                    onCheckedChange = { viewModel.toggleMarketPrices(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF34D399))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Price details
        Text("CHI TIẾT GIÁ THỊ TRƯỜNG", color = Color.Gray, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF252D26),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cà phê Đắk Lắk:", color = Color.LightGray, fontSize = 12.sp)
                    Text("122.000 đ/kg", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cà phê Lâm Đồng:", color = Color.LightGray, fontSize = 12.sp)
                    Text("121.200 đ/kg", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(color = Color(0xFF3A4D3B))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Vàng Nhẫn 9999 SJC (Bán):", color = Color.LightGray, fontSize = 12.sp)
                    Text("89.200.000 đ", color = Color(0xFF4ADE80), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Vàng Nhẫn 9999 SJC (Mua):", color = Color.LightGray, fontSize = 12.sp)
                    Text("87.900.000 đ", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun DockSettingsPane(
    viewModel: LauncherViewModel,
    dockApps: List<AppInfo>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Thanh Dock (4 ứng dụng)", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text("Tùy chỉnh 4 ứng dụng ghim cố định phía dưới", color = Color.Gray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(12.dp))

        for (slotIndex in 0..3) {
            val app = dockApps.getOrNull(slotIndex)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF282828),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Ô ${slotIndex + 1}", color = Color(0xFF007AFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (app?.iconBitmap != null) {
                                    Image(bitmap = app.iconBitmap, contentDescription = null, modifier = Modifier.size(26.dp))
                                } else if (app?.systemIconFallback != null) {
                                    Icon(app.systemIconFallback, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Text(
                            text = app?.label ?: "Chưa chọn",
                            color = Color.White,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = { viewModel.openDockSlotPicker(slotIndex) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF383838)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Đổi", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WallpaperSettingsPane(
    viewModel: LauncherViewModel,
    currentWallpaper: WallpaperTheme,
    customWallpaperUri: String?,
    onPickImage: () -> Unit
) {
    val presets = WallpaperPresets.presets

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Hình nền Desktop", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text("Chọn hình nền macOS hoặc chọn ảnh từ máy", color = Color.Gray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(10.dp))

        // Custom device wallpaper button
        Button(
            onClick = onPickImage,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF472B6)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chọn ảnh trên máy làm hình nền", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(presets) { wp ->
                val isSelected = wp.id == currentWallpaper.id && customWallpaperUri == null
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF282828),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF007AFF)) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setWallpaper(wp) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(wp.backgroundBrush)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(wp.name, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                            Text(wp.description, color = Color.Gray, fontSize = 10.sp, maxLines = 1)
                        }

                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GeneralSettingsPane(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val hiddenCount by viewModel.hiddenPackages.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Cài đặt chung", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text("Thiết lập hệ thống launcher macOs", color = Color.Gray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { viewModel.openDefaultLauncherSettings(context) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đặt macOs làm màn hình chính mặc định", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (hiddenCount.isNotEmpty()) {
            Button(
                onClick = { viewModel.unhideAllApps() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hiển thị lại ${hiddenCount.size} ứng dụng đã ẩn", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.loadInstalledApps() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF383838)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quét lại tất cả ứng dụng", fontSize = 12.sp)
        }
    }
}

@Composable
private fun AboutSettingsPane() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_pink_apple),
            contentDescription = "macOs",
            modifier = Modifier.size(50.dp)
        )

        Text("Launcher Giao diện macOs", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Phiên bản 1.0 (Pink Apple)", color = Color(0xFFF472B6), fontSize = 11.5.sp)

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF282828),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SpecRow("Nhà phát triển:", "Dev Trường Thanh")
                SpecRow("Nền tảng:", "Android Jetpack Compose")
                SpecRow("Tính năng:", "Giá Cà phê, Vàng nhẫn, Lịch âm")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://fb.com/TruongAlone")))
                            } catch (e: Exception) {
                                // Ignore
                            }
                        }
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Facebook:", color = Color.Gray, fontSize = 11.5.sp)
                    Text("fb.com/TruongAlone", color = Color(0xFF60A5FA), fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 11.5.sp)
        Text(text = value, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
    }
}
