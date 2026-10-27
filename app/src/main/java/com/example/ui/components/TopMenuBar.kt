package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(statusBarTop))

        // Clean macOS Menu Bar: Removed Finder, tệp, xem, tìm kiếm, wifi, pin as requested
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(Color.Black.copy(alpha = 0.32f))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Pink Apple logo (icon hình táo màu hồng)
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { isAppleMenuOpen = true }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("apple_menu_button")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pink_apple),
                        contentDescription = "macOs",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "macOs",
                        color = Color(0xFFF472B6),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                DropdownMenu(
                    expanded = isAppleMenuOpen,
                    onDismissRequest = { isAppleMenuOpen = false },
                    modifier = Modifier.background(Color(0xFF262626))
                ) {
                    DropdownMenuItem(
                        text = { Text("Giới thiệu: Launcher Giao diện macOs", color = Color.White, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFF472B6), modifier = Modifier.size(16.dp)) },
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
                        text = { Text("Đồng hồ & Lịch âm", color = Color.White, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(16.dp)) },
                        onClick = {
                            isAppleMenuOpen = false
                            viewModel.openSettingsDialog(SettingsCategory.CLOCK_AND_LUNAR)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Giá Cà phê & Vàng nhẫn", color = Color.White, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp)) },
                        onClick = {
                            isAppleMenuOpen = false
                            viewModel.openSettingsDialog(SettingsCategory.MARKET_PRICES)
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

            // Right: Control Center toggle (removed text row opposite macOs as requested)
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Trung tâm điều khiển",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { viewModel.toggleControlCenter() }
                    .testTag("control_center_toggle")
            )
        }
    }
}
