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
        // 3D Liquid Frosted Glass Shelf (RoundedCornerShape(32.dp))
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White.copy(alpha = 0.25f),
            shadowElevation = 20.dp,
            modifier = Modifier
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.90f),
                            Color.White.copy(alpha = 0.20f),
                            Color.White.copy(alpha = 0.70f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Top Specular Gloss Highlight across the dock shelf
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(32.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.35f),
                                        Color.White.copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Exactly 4 App Slots
                    for (slotIndex in 0..3) {
                        val app = dockApps.getOrNull(slotIndex)
                        DockItem(
                            slotIndex = slotIndex,
                            app = app,
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
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun DockItem(
    slotIndex: Int,
    app: AppInfo?,
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
            // 3D Liquid Jelly Glass frame for each dock icon
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.28f),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .size(56.dp)
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.90f),
                                Color.White.copy(alpha = 0.20f),
                                Color.White.copy(alpha = 0.65f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Top Dome Specular Highlight
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.45f),
                                        Color.White.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(15.dp))
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
            }
        }
    }
}
