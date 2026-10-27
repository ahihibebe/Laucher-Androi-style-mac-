package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.SettingsCategory

@Composable
fun DesktopContextMenuDialog(
    viewModel: LauncherViewModel,
    onPickImageFromDevice: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF222222).copy(alpha = 0.95f),
            shadowElevation = 24.dp,
            modifier = Modifier
                .width(320.dp)
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .testTag("desktop_context_menu_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                    title = "Bộ sưu tập hình nền macOs",
                    subtitle = "Sequoia, Sonoma, Ventura...",
                    onClick = {
                        onDismiss()
                        viewModel.openSettingsDialog(SettingsCategory.WALLPAPER)
                    }
                )

                HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

                ContextMenuActionRow(
                    icon = Icons.Default.TrendingUp,
                    iconTint = Color(0xFF34D399),
                    title = "Giá Cà phê & Vàng nhẫn",
                    subtitle = "Cập nhật trực tiếp từ giacaphe.com",
                    onClick = {
                        onDismiss()
                        viewModel.openSettingsDialog(SettingsCategory.MARKET_PRICES)
                    }
                )

                HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

                ContextMenuActionRow(
                    icon = Icons.Default.Settings,
                    iconTint = Color(0xFFA78BFA),
                    title = "Cài đặt macOs đầy đủ",
                    subtitle = "Tùy biến toàn bộ hệ thống launcher",
                    onClick = {
                        onDismiss()
                        viewModel.openSettingsDialog(SettingsCategory.GENERAL)
                    }
                )
            }
        }
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
            .padding(vertical = 8.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}
