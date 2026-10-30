package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Apps
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.remember
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
    swipeRightForLaunchpad: Boolean,
    swipeUpForLaunchpad: Boolean,
    swipeDownForNotifications: Boolean,
    showLaunchpadGuide: Boolean,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E1E1E),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.88f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // prevent dismiss when clicking content
                    )
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
                            icon = Icons.Default.Gesture,
                            label = "Thao tác vuốt",
                            isSelected = selectedCategory == SettingsCategory.GESTURES,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.GESTURES) }
                        )
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
                            icon = Icons.Default.VisibilityOff,
                            label = "Ứng dụng ẩn",
                            isSelected = selectedCategory == SettingsCategory.HIDDEN_APPS,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.HIDDEN_APPS) }
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
                            SettingsCategory.GESTURES -> {
                                GesturesSettingsPane(
                                    viewModel = viewModel,
                                    swipeRightForLaunchpad = swipeRightForLaunchpad,
                                    swipeUpForLaunchpad = swipeUpForLaunchpad,
                                    swipeDownForNotifications = swipeDownForNotifications,
                                    showLaunchpadGuide = showLaunchpadGuide
                                )
                            }
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
                            SettingsCategory.HIDDEN_APPS -> {
                                HiddenAppsSettingsPane(viewModel = viewModel)
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
private fun GesturesSettingsPane(
    viewModel: LauncherViewModel,
    swipeRightForLaunchpad: Boolean,
    swipeUpForLaunchpad: Boolean,
    swipeDownForNotifications: Boolean,
    showLaunchpadGuide: Boolean
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text("Thao tác vuốt & Cử chỉ", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text("Tùy chỉnh cử chỉ điều khiển trên màn hình chính", color = Color.Gray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF282828),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Switch 1: Swipe Right for Launchpad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Vuốt sang phải mở Launchpad", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Kéo ngang màn hình chính để mở tất cả ứng dụng", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = swipeRightForLaunchpad,
                        onCheckedChange = { viewModel.toggleSwipeRightForLaunchpad(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

                // Switch 2: Swipe Up for Launchpad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trượt lên bật tất cả ứng dụng", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Vuốt từ dưới lên trên màn hình chính để mở nhanh Launchpad", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = swipeUpForLaunchpad,
                        onCheckedChange = { viewModel.toggleSwipeUpForLaunchpad(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

                // Switch 3: Swipe Down for Notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trượt xuống mở thanh thông báo", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Vuốt từ trên xuống tại bất kỳ đâu để kéo bảng thông báo", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = swipeDownForNotifications,
                        onCheckedChange = { viewModel.toggleSwipeDownForNotifications(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

                // Switch 4: Show/Hide Launchpad Guide Banner on Desktop
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hướng dẫn mở ứng dụng", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Tắt thanh hướng dẫn ở màn hình chính để gọn gàng hơn", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = showLaunchpadGuide,
                        onCheckedChange = { viewModel.toggleLaunchpadGuide(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

                // Switch 5: 2 chạm khóa màn hình (Double Tap to Lock Screen)
                val doubleTapToLock by viewModel.doubleTapToLock.collectAsState()
                val context = androidx.compose.ui.platform.LocalContext.current
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("2 chạm khóa màn hình", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Chạm nhanh 2 lần vào màn hình chính để tắt và khóa màn hình", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = doubleTapToLock,
                        onCheckedChange = { viewModel.toggleDoubleTapToLock(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable {
                            try {
                                val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // ignore
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⚙️ Thiết lập quyền Trợ năng (Accessibility) để khóa màn hình",
                        color = Color(0xFF60A5FA),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
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
        Text("Đồng hồ & Lịch âm", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text("Tùy chỉnh bật tắt hiển thị thời gian và Âm lịch Việt Nam", color = Color.Gray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF282828),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hiển thị đồng hồ", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Bật/tắt giờ trên widget", color = Color.Gray, fontSize = 10.5.sp)
                    }
                    Switch(
                        checked = showClock,
                        onCheckedChange = { viewModel.toggleClock(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

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
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 8.dp))

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
                Text("Giá Thị Trường (Cà phê, Vàng, Xăng dầu)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Lâm Đồng & Toàn quốc (Cập nhật ngày giờ)", color = Color.Gray, fontSize = 11.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = { viewModel.openGoldPriceDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Text("Bảng Vàng", color = Color.Black, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.openCoffeeOnlineDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Text("Cà phê online", color = Color.White, fontSize = 10.5.sp)
                }

                IconButton(onClick = { viewModel.fetchMarketPrices() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Tải lại", tint = Color(0xFF34D399))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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
                    Text("Hiển thị widget Giá Thị Trường", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("Cà phê Lâm Đồng, Giá Vàng, Giá Xăng & Giá Dầu", color = Color.Gray, fontSize = 10.5.sp)
                }
                Switch(
                    checked = showMarketPrices,
                    onCheckedChange = { viewModel.toggleMarketPrices(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF34D399))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("CẬP NHẬT LÚC: ${marketPrices?.lastUpdated ?: "Hiện tại"}", color = Color.Gray, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF252D26),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("☕ GIÁ CÀ PHÊ (LÂM ĐỒNG & TÂY NGUYÊN)", color = Color(0xFFFED7AA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                marketPrices?.coffeePrices?.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (item.province == "Lâm Đồng") "Cà phê Lâm Đồng:" else "Cà phê ${item.province}:", color = if (item.province == "Lâm Đồng") Color(0xFFFDE047) else Color.LightGray, fontSize = 12.sp, fontWeight = if (item.province == "Lâm Đồng") FontWeight.Bold else FontWeight.Normal)
                        Text(item.priceText, color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF3A4D3B))
                Text("🥇 GIÁ VÀNG THEO NGÀY GIỜ HIỆN TẠI", color = Color(0xFFFEF08A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                marketPrices?.goldPrices?.take(4)?.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.brand} (Bán):", color = Color.LightGray, fontSize = 11.5.sp)
                        Text(item.sellPrice, color = Color(0xFF4ADE80), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF3A4D3B))
                Text("⛽ GIÁ XĂNG (VÙNG 1, VÙNG 2, VÙNG 3)", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                marketPrices?.petrolPrices?.forEach { fuel ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(fuel.fuelType, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vùng 1: ${fuel.region1Price}", color = Color(0xFF93C5FD), fontSize = 10.5.sp)
                            Text("Vùng 2: ${fuel.region2Price}", color = Color(0xFFFDE047), fontSize = 10.5.sp)
                            Text("Vùng 3: ${fuel.region3Price}", color = Color(0xFFFCA5A5), fontSize = 10.5.sp)
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF3A4D3B))
                Text("🛢️ GIÁ DẦU (VÙNG 1, VÙNG 2, VÙNG 3)", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                marketPrices?.oilPrices?.forEach { fuel ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(fuel.fuelType, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vùng 1: ${fuel.region1Price}", color = Color(0xFF93C5FD), fontSize = 10.5.sp)
                            Text("Vùng 2: ${fuel.region2Price}", color = Color(0xFFFDE047), fontSize = 10.5.sp)
                            Text("Vùng 3: ${fuel.region3Price}", color = Color(0xFFFCA5A5), fontSize = 10.5.sp)
                        }
                    }
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
                SpecRow("Tính năng:", "Giá Cà phê (Giá tại: Lâm Đồng), Vàng, Xăng dầu, Lịch vạn niên")

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

@Composable
private fun HiddenAppsSettingsPane(viewModel: LauncherViewModel) {
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text("Khôi phục ứng dụng đã ẩn", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Quản lý và hiện lại các ứng dụng bạn đã ẩn khỏi màn hình Launchpad", color = Color.Gray, fontSize = 11.5.sp)

        Spacer(modifier = Modifier.height(14.dp))

        if (hiddenApps.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đang ẩn ${hiddenApps.size} ứng dụng",
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Button(
                    onClick = { viewModel.unhideAllApps() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Hiện lại tất cả", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF282828),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    hiddenApps.forEachIndexed { index, app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (app.iconBitmap != null) {
                                    Image(
                                        bitmap = app.iconBitmap,
                                        contentDescription = app.label,
                                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                    )
                                } else {
                                    Icon(
                                        imageVector = app.systemIconFallback ?: Icons.Default.Apps,
                                        contentDescription = app.label,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Column(modifier = Modifier.padding(end = 6.dp)) {
                                    Text(app.label, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                    Text(app.packageName, color = Color.Gray, fontSize = 9.sp, maxLines = 1)
                                }
                            }

                            Button(
                                onClick = { viewModel.unhideApp(app.packageName) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Khôi phục", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (index < hiddenApps.size - 1) {
                            HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF282828),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Không có ứng dụng nào bị ẩn", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Tất cả các ứng dụng đang được hiển thị trên Launchpad.", color = Color.Gray, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}
