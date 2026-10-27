package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LunarDate
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.SettingsCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TopMenuBar(
    viewModel: LauncherViewModel,
    currentTime: Calendar,
    lunarDate: LunarDate,
    showClock: Boolean,
    showLunar: Boolean,
    is24Hour: Boolean,
    onOpenLaunchpad: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAppleMenuOpen by remember { mutableStateOf(false) }

    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val timeFormatPattern = if (is24Hour) "HH:mm" else "hh:mm a"
    val timeFormatter = remember(is24Hour) { SimpleDateFormat(timeFormatPattern, Locale.getDefault()) }
    val dayFormatter = remember { SimpleDateFormat("EEE, d MMM", Locale("vi", "VN")) }

    val formattedTime = remember(currentTime.timeInMillis, is24Hour) {
        timeFormatter.format(currentTime.time)
    }
    val formattedDay = remember(currentTime.timeInMillis) {
        dayFormatter.format(currentTime.time)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Status bar space
        Spacer(modifier = Modifier.height(statusBarTop))

        // macOS Menu bar strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Apple logo & Menus
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Apple logo
                Box {
                    Text(
                        text = "",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { isAppleMenuOpen = true }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("apple_menu_button")
                    )

                    DropdownMenu(
                        expanded = isAppleMenuOpen,
                        onDismissRequest = { isAppleMenuOpen = false },
                        modifier = Modifier.background(Color(0xFF262626))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Giới thiệu macOS Launcher", color = Color.White, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                isAppleMenuOpen = false
                                viewModel.openAboutDialog()
                            }
                        )
                        HorizontalDivider(color = Color.DarkGray)
                        DropdownMenuItem(
                            text = { Text("Cài đặt hệ thống...", color = Color.White, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                isAppleMenuOpen = false
                                viewModel.openSettingsDialog(SettingsCategory.GENERAL)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cài đặt Đồng hồ & Lịch âm", color = Color.White, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(16.dp)) },
                            onClick = {
                                isAppleMenuOpen = false
                                viewModel.openSettingsDialog(SettingsCategory.CLOCK_AND_LUNAR)
                            }
                        )
                        HorizontalDivider(color = Color.DarkGray)
                        DropdownMenuItem(
                            text = { Text("Đặt làm màn hình chính mặc định", color = Color.White, fontSize = 13.sp) },
                            onClick = {
                                isAppleMenuOpen = false
                                viewModel.openDefaultLauncherSettings(context)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Tải lại danh sách ứng dụng", color = Color.White, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                isAppleMenuOpen = false
                                viewModel.loadInstalledApps()
                            }
                        )
                    }
                }

                // Active App Name
                Text(
                    text = "Finder",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = "Tệp",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "Xem",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Right: Status items, Clock, Lunar Calendar & Control Center
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Spotlight / Search button -> opens Launchpad
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Spotlight Search",
                    tint = Color.White,
                    modifier = Modifier
                        .size(15.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onOpenLaunchpad() }
                        .testTag("spotlight_search_button")
                )

                // Control Center toggle
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Trung tâm điều khiển",
                    tint = Color.White,
                    modifier = Modifier
                        .size(15.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { viewModel.toggleControlCenter() }
                        .testTag("control_center_toggle")
                )

                // Wi-Fi
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Wi-Fi",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )

                // Battery
                Icon(
                    imageVector = Icons.Default.BatteryFull,
                    contentDescription = "Pin",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )

                // Clock & Lunar text combo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { viewModel.openSettingsDialog(SettingsCategory.CLOCK_AND_LUNAR) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("status_clock_lunar_badge")
                ) {
                    if (showLunar) {
                        Text(
                            text = "Âm ${lunarDate.day}/${lunarDate.month} • ",
                            color = Color(0xFFFFD54F),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (showClock) {
                        Text(
                            text = "$formattedDay $formattedTime",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
