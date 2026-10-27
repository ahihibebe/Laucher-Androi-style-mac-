package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ControlCenterDropdown(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isWifiOn by remember { mutableStateOf(true) }
    var isBluetoothOn by remember { mutableStateOf(true) }
    var isDarkMode by remember { mutableStateOf(true) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var volume by remember { mutableFloatStateOf(0.7f) }
    var brightness by remember { mutableFloatStateOf(0.85f) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF202020).copy(alpha = 0.95f),
        shadowElevation = 24.dp,
        modifier = modifier
            .width(320.dp)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .testTag("macos_control_center_panel")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Quick Toggles (Wi-Fi, Bluetooth, Dark Mode, Flashlight)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left 2x2 group
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF2C2C2C),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ControlCenterToggle(
                            icon = Icons.Default.Wifi,
                            title = "Wi-Fi",
                            isActive = isWifiOn,
                            onClick = { isWifiOn = !isWifiOn }
                        )
                        ControlCenterToggle(
                            icon = Icons.Default.Bluetooth,
                            title = "Bluetooth",
                            isActive = isBluetoothOn,
                            onClick = { isBluetoothOn = !isBluetoothOn }
                        )
                    }
                }

                // Right 2x2 group
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF2C2C2C),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ControlCenterToggle(
                            icon = Icons.Default.DarkMode,
                            title = "Giao diện tối",
                            isActive = isDarkMode,
                            onClick = { isDarkMode = !isDarkMode }
                        )
                        ControlCenterToggle(
                            icon = Icons.Default.FlashlightOn,
                            title = "Đèn pin",
                            isActive = isFlashlightOn,
                            onClick = { isFlashlightOn = !isFlashlightOn }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sliders: Brightness & Volume
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF2C2C2C),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Độ sáng màn hình",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = brightness,
                        onValueChange = { brightness = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF007AFF),
                            inactiveTrackColor = Color(0xFF444444)
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Âm lượng âm thanh",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF007AFF),
                            inactiveTrackColor = Color(0xFF444444)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlCenterToggle(
    icon: ImageVector,
    title: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isActive) Color(0xFF007AFF) else Color(0xFF404040)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }

        Text(
            text = title,
            color = Color.White,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
