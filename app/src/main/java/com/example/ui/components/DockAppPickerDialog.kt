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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppInfo

@Composable
fun DockAppPickerDialog(
    slotIndex: Int,
    allApps: List<AppInfo>,
    onSelectApp: (AppInfo) -> Unit,
    onDismiss: () -> Unit
) {
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
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF222222),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.75f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // prevent dismiss when clicking content
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                    .testTag("dock_app_picker_dialog")
            ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2C2C2C))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chọn ứng dụng cho Ô ${slotIndex + 1}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Chạm để ghim vào thanh Dock (tối đa 4 ứng dụng)",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                    }
                }

                // List of apps
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allApps) { app ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF2D2D2D),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectApp(app) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val backgroundModifier = if (app.iconBitmap != null) {
                                    Modifier.background(Color.Transparent)
                                } else {
                                    Modifier.background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))))
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .size(44.dp)
                                        .border(
                                            1.dp,
                                            Brush.verticalGradient(
                                                listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.15f))
                                            ),
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(3.dp)
                                            .clip(RoundedCornerShape(9.dp))
                                            .then(backgroundModifier),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (app.iconBitmap != null) {
                                            Image(
                                                bitmap = app.iconBitmap,
                                                contentDescription = null,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        } else if (app.systemIconFallback != null) {
                                            Icon(
                                                imageVector = app.systemIconFallback,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }

                                Column {
                                    Text(
                                        text = app.label,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = app.packageName,
                                        color = Color.Gray,
                                        fontSize = 10.sp
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
