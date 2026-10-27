package com.example.ui.components

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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dock
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppInfo
import com.example.model.LunarDate
import com.example.model.WallpaperPresets
import com.example.model.WallpaperTheme
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.SettingsCategory
import java.util.Calendar

@Composable
fun MacSettingsDialog(
    viewModel: LauncherViewModel,
    selectedCategory: SettingsCategory,
    showClock: Boolean,
    showLunar: Boolean,
    is24Hour: Boolean,
    showCanChi: Boolean,
    currentWallpaper: WallpaperTheme,
    dockApps: List<AppInfo>,
    lunarDate: LunarDate,
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
                // Window Header (macOS Traffic Light Buttons + Title)
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
                        // Close (Red)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5F56))
                                .clickable { onDismiss() }
                                .testTag("window_traffic_red_close")
                        )
                        // Minimize (Yellow)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFBD2E))
                                .clickable { onDismiss() }
                        )
                        // Maximize (Green)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF27C93F))
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Cài đặt hệ thống",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Window Body: Split into Left Sidebar and Right Detail Pane
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Sidebar
                    Column(
                        modifier = Modifier
                            .width(120.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF252525))
                            .padding(vertical = 12.dp, horizontal = 6.dp)
                    ) {
                        SettingsSidebarItem(
                            icon = Icons.Default.Schedule,
                            label = "Đồng hồ & Lịch âm",
                            isSelected = selectedCategory == SettingsCategory.CLOCK_AND_LUNAR,
                            onClick = { viewModel.selectSettingsCategory(SettingsCategory.CLOCK_AND_LUNAR) }
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
                            .padding(16.dp)
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
                            SettingsCategory.DOCK -> {
                                DockSettingsPane(
                                    viewModel = viewModel,
                                    dockApps = dockApps
                                )
                            }
                            SettingsCategory.WALLPAPER -> {
                                WallpaperSettingsPane(
                                    viewModel = viewModel,
                                    currentWallpaper = currentWallpaper
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
            .padding(horizontal = 8.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tùy chỉnh hiển thị thời gian và Âm lịch Việt Nam trên thanh menu và màn hình chính",
            color = Color.Gray,
            fontSize = 11.5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Switches Group
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF282828),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Switch 1: Toggle Clock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hiển thị đồng hồ",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Bật hoặc tắt đồng hồ số trên thanh trạng thái và widget",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = showClock,
                        onCheckedChange = { viewModel.toggleClock(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF007AFF)
                        ),
                        modifier = Modifier.testTag("toggle_clock_switch")
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 10.dp))

                // Switch 2: Toggle Lunar Calendar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hiển thị lịch âm (Việt Nam)",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Xem ngày, tháng âm lịch theo chuẩn thiên văn múi giờ GMT+7",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = showLunar,
                        onCheckedChange = { viewModel.toggleLunar(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF007AFF)
                        ),
                        modifier = Modifier.testTag("toggle_lunar_switch")
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 10.dp))

                // Switch 3: Can Chi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hiển thị Can Chi",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Hiển thị Can Chi năm, tháng và ngày (Ví dụ: Năm Bính Ngọ)",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = showCanChi,
                        onCheckedChange = { viewModel.toggleCanChi(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF007AFF)
                        ),
                        modifier = Modifier.testTag("toggle_canchi_switch")
                    )
                }

                HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 10.dp))

                // Switch 4: 24-Hour Format
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Định dạng giờ 24h",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Hiển thị 24 giờ (14:30) thay vì 12 giờ (02:30 PM)",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = is24Hour,
                        onCheckedChange = { viewModel.toggle24Hour(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF007AFF)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Preview Card
        Text(
            text = "XEM TRƯỚC TRỰC TIẾP",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF172033),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${lunarDate.day}",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "Hôm nay: Ngày ${lunarDate.day} tháng ${lunarDate.month} (Âm lịch)",
                            color = Color(0xFFFDE047),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = lunarDate.formattedCanChiDetails,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.5.sp
                        )
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
        Text(
            text = "Thanh Dock (4 ứng dụng)",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tùy chỉnh 4 ứng dụng xuất hiện cố định trên thanh bar phía dưới màn hình",
            color = Color.Gray,
            fontSize = 11.5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        for (slotIndex in 0..3) {
            val app = dockApps.getOrNull(slotIndex)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF282828),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Ô ${slotIndex + 1}",
                            color = Color(0xFF007AFF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier
                                .size(36.dp)
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(
                                        listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.15f))
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(2.dp), contentAlignment = Alignment.Center) {
                                if (app?.iconBitmap != null) {
                                    Image(
                                        bitmap = app.iconBitmap,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(RoundedCornerShape(7.dp))
                                    )
                                } else if (app?.systemIconFallback != null) {
                                    Icon(
                                        imageVector = app.systemIconFallback,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = app?.label ?: "Chưa chọn ứng dụng",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = { viewModel.openDockSlotPicker(slotIndex) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF383838)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("change_dock_slot_$slotIndex")
                    ) {
                        Text("Thay đổi", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WallpaperSettingsPane(
    viewModel: LauncherViewModel,
    currentWallpaper: WallpaperTheme
) {
    val presets = WallpaperPresets.presets

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Hình nền Desktop",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Chọn hình nền phong cách macOS chính hãng cho màn hình chính",
            color = Color.Gray,
            fontSize = 11.5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(presets) { wp ->
                val isSelected = wp.id == currentWallpaper.id
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF282828),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF007AFF)) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setWallpaper(wp) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(wp.backgroundBrush)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = wp.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = wp.description,
                                color = Color.Gray,
                                fontSize = 10.5.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Đã chọn",
                                tint = Color(0xFF007AFF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GeneralSettingsPane(
    viewModel: LauncherViewModel
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Cài đặt chung",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Thiết lập mặc định và hệ thống của macOS Launcher",
            color = Color.Gray,
            fontSize = 11.5.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.openDefaultLauncherSettings(context) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đặt macOS Launcher làm màn hình chính mặc định", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { viewModel.loadInstalledApps() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF383838)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quét lại tất cả ứng dụng trên thiết bị", fontSize = 12.sp)
        }
    }
}

@Composable
private fun AboutSettingsPane() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "",
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "macOS Launcher",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Phiên bản 1.0 (Sequoia Edition)",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF282828),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SpecRow("Thiết kế:", "macOS Desktop & Launchpad")
                SpecRow("Thanh Dock:", "4 ứng dụng tùy chỉnh")
                SpecRow("Lịch âm:", "Thiên văn học UTC+7 Việt Nam")
                SpecRow("Nền tảng:", "Android Jetpack Compose")
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
