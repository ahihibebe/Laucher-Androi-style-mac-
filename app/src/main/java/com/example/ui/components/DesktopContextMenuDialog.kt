package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.SettingsCategory

@Composable
fun DesktopContextMenuDialog(
    viewModel: LauncherViewModel,
    onPickImageFromDevice: () -> Unit,
    onDismiss: () -> Unit
) {
    val showClock by viewModel.showClock.collectAsState()
    val showLunar by viewModel.showLunar.collectAsState()
    val showMarketPrices by viewModel.showMarketPrices.collectAsState()
    val wallpaperAlpha by viewModel.wallpaperAlpha.collectAsState()
    val widgetAlpha by viewModel.widgetAlpha.collectAsState()

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
                color = Color(0xFF222222).copy(alpha = 0.96f),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .width(340.dp)
                    .heightIn(max = 620.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // prevent dismiss when clicking content
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .testTag("desktop_context_menu_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tùy chọn màn hình chính",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bật/tắt Widget Tiện ích & Lịch Vạn Niên
                    val showUtilityWidget by viewModel.showUtilityWidget.collectAsState()
                    val isUtilityActive = showUtilityWidget && (showClock || showLunar)
                    ContextMenuSwitchRow(
                        icon = Icons.Default.CalendarMonth,
                        iconTint = Color(0xFFFFB74D),
                        title = "Widget Đồng hồ & Tiện ích",
                        subtitle = if (isUtilityActive) "Đang bật (hiển thị trên màn hình)" else "Đang tắt (đã ẩn khỏi màn hình)",
                        checked = isUtilityActive,
                        onCheckedChange = { newState ->
                            viewModel.toggleUtilityWidget(newState)
                            viewModel.toggleClock(newState)
                            viewModel.toggleLunar(newState)
                        }
                    )

                    HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

                    // Bật/tắt Widget Giá Thị Trường
                    ContextMenuSwitchRow(
                        icon = Icons.Default.TrendingUp,
                        iconTint = Color(0xFF34D399),
                        title = "Widget Giá Thị Trường",
                        subtitle = if (showMarketPrices) "Cà phê, Tiêu, Vàng, Xăng dầu (Đang bật)" else "Đang tắt (đã ẩn khỏi màn hình)",
                        checked = showMarketPrices,
                        onCheckedChange = { newState ->
                            viewModel.toggleMarketPrices(newState)
                        }
                    )

                    HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 6.dp))

                    // Mục Ảnh nền & Độ trong suốt ảnh nền + Widget
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2B2B2F),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Opacity, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Độ trong suốt Ảnh nền & Widget",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Độ rõ ảnh nền", color = Color.LightGray, fontSize = 11.sp)
                                Text("${(wallpaperAlpha * 100).toInt()}%", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = wallpaperAlpha,
                                onValueChange = { viewModel.setWallpaperAlpha(it) },
                                valueRange = 0.0f..1.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF60A5FA),
                                    activeTrackColor = Color(0xFF3B82F6),
                                    inactiveTrackColor = Color(0xFF4B5563)
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("context_wallpaper_alpha_slider")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Độ trong suốt các Widget", color = Color.LightGray, fontSize = 11.sp)
                                Text("${(widgetAlpha * 100).toInt()}%", color = Color(0xFFF472B6), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = widgetAlpha,
                                onValueChange = { viewModel.setWidgetAlpha(it) },
                                valueRange = 0.0f..1.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFF472B6),
                                    activeTrackColor = Color(0xFFEC4899),
                                    inactiveTrackColor = Color(0xFF4B5563)
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("context_widget_alpha_slider")
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 6.dp))

                    ContextMenuActionRow(
                        icon = Icons.Default.AddPhotoAlternate,
                        iconTint = Color(0xFFF472B6),
                        title = "Chọn ảnh trên máy làm hình nền",
                        subtitle = "Mở thư viện ảnh của thiết bị",
                        onClick = {
                            onDismiss()
                            onPickImageFromDevice()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

                    ContextMenuActionRow(
                        icon = Icons.Default.Wallpaper,
                        iconTint = Color(0xFF60A5FA),
                        title = "Ảnh nền & Độ trong suốt Widget",
                        subtitle = "Sequoia, Sonoma, tùy chỉnh độ mờ...",
                        onClick = {
                            onDismiss()
                            viewModel.openSettingsDialog(SettingsCategory.WALLPAPER)
                        }
                    )

                    HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

                    ContextMenuActionRow(
                        icon = Icons.Default.Schedule,
                        iconTint = Color(0xFF34D399),
                        title = "Kiểu giờ, Font, Chữ đậm & Cỡ",
                        subtitle = "Tùy biến giao diện đồng hồ trên màn hình chính",
                        onClick = {
                            onDismiss()
                            viewModel.openSettingsDialog(SettingsCategory.CLOCK_AND_LUNAR)
                        }
                    )

                    HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

                    ContextMenuActionRow(
                        icon = Icons.Default.Gesture,
                        iconTint = Color(0xFF38BDF8),
                        title = "Cài đặt Thao tác vuốt",
                        subtitle = "Bật/tắt vuốt sang phải, vuốt lên, vuốt xuống",
                        onClick = {
                            onDismiss()
                            viewModel.openSettingsDialog(SettingsCategory.GESTURES)
                        }
                    )

                    HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

                    ContextMenuActionRow(
                        icon = Icons.Default.QrCode2,
                        iconTint = Color(0xFFFBBF24),
                        title = "Thông tin & Mã QR ủng hộ",
                        subtitle = "MB Bank: 220011026789 • Mời Dev gói bim bim 😋",
                        onClick = {
                            onDismiss()
                            viewModel.openSettingsDialog(SettingsCategory.DONATE)
                        }
                    )

                    HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

                    ContextMenuActionRow(
                        icon = Icons.Default.Settings,
                        iconTint = Color(0xFFA78BFA),
                        title = "Cài đặt hệ thống đầy đủ",
                        subtitle = "Tùy biến toàn bộ launcher",
                        onClick = {
                            onDismiss()
                            viewModel.openSettingsDialog(SettingsCategory.GENERAL)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextMenuSwitchRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Column {
                Text(text = title, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = Color.Gray, fontSize = 10.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = iconTint
            )
        )
    }
}

@Composable
private fun ContextMenuActionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
        }

        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}
