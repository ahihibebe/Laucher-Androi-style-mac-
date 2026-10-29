package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.viewmodel.LauncherViewModel

@Composable
fun MacDock(
    viewModel: LauncherViewModel,
    dockApps: List<AppInfo>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 12.dp, start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        // macOS Dock Glass Shelf
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Color.White.copy(alpha = 0.22f),
            shadowElevation = 18.dp,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exactly 4 App Slots
                for (slotIndex in 0..3) {
                    val app = dockApps.getOrNull(slotIndex)
                    val badgeCount = if (app != null) viewModel.getBadgeCount(app.packageName) else 0
                    DockItem(
                        slotIndex = slotIndex,
                        app = app,
                        badgeCount = badgeCount,
                        onLaunch = { appToLaunch ->
                            viewModel.launchApp(context, appToLaunch)
                        },
                        onLongClick = {
                            viewModel.openDockSlotPicker(slotIndex)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun DockItem(
    slotIndex: Int,
    app: AppInfo?,
    badgeCount: Int,
    onLaunch: (AppInfo) -> Unit,
    onLongClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.25f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "dock_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (app != null) {
                        onLaunch(app)
                    } else {
                        onLongClick()
                    }
                },
                onLongClick = onLongClick
            )
            .testTag("dock_slot_$slotIndex")
    ) {
        val backgroundModifier = if (app?.iconBitmap != null) {
            Modifier.background(Color.Transparent)
        } else {
            Modifier.background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                )
            )
        }

        Box(contentAlignment = Alignment.TopEnd) {
            // Translucent frosted glass frame for each dock icon
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.22f),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(56.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.65f),
                                Color.White.copy(alpha = 0.18f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .then(backgroundModifier),
                    contentAlignment = Alignment.Center
                ) {
                    if (app?.iconBitmap != null) {
                        Image(
                            bitmap = app.iconBitmap,
                            contentDescription = app.label,
                            modifier = Modifier.size(48.dp)
                        )
                    } else if (app?.systemIconFallback != null) {
                        Icon(
                            imageVector = app.systemIconFallback,
                            contentDescription = app.label,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Chọn ứng dụng",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Notification Badge kèm số khi có thông báo
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-4).dp)
                        .background(Color(0xFFFF3B30), CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                        .padding(horizontal = if (badgeCount > 9) 4.5.dp else 2.5.dp, vertical = 0.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
