package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.viewmodel.LauncherViewModel

@Composable
fun LaunchpadScreen(
    viewModel: LauncherViewModel,
    apps: List<AppInfo>,
    searchQuery: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val isJiggleMode by viewModel.isJiggleMode.collectAsState()

    // Wiggle rotation animation for jiggle mode like iPhone
    val infiniteTransition = rememberInfiniteTransition(label = "jiggle_transition")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 130),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarTop)
        ) {
            // Header: Back button + Spotlight Search Bar + Done button if in jiggle mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = {
                        if (isJiggleMode) viewModel.setJiggleMode(false)
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("launchpad_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại Desktop",
                        tint = Color.White
                    )
                }

                // Spotlight search field
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(Color.White),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("spotlight_input_field"),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Tìm kiếm ứng dụng...",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        )

                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.setSearchQuery("") },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Xóa tìm kiếm",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // "Xong" button when in jiggle edit mode
                if (isJiggleMode) {
                    Button(
                        onClick = { viewModel.setJiggleMode(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Xong", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Subtitle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Launchpad",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isJiggleMode) "Chế độ chỉnh sửa (nhấn - để xóa/ẩn)" else "${apps.size} ứng dụng",
                    color = if (isJiggleMode) Color(0xFFF472B6) else Color.White.copy(alpha = 0.6f),
                    fontSize = 11.5.sp,
                    fontWeight = if (isJiggleMode) FontWeight.Medium else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Apps Grid with crash-proof unique keys and proper padding
            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không tìm thấy ứng dụng nào",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .testTag("launchpad_apps_grid")
                ) {
                    // Fix: Use index-based unique key to prevent key collision crashes when scrolling to bottom!
                    itemsIndexed(
                        items = apps,
                        key = { index, app -> "${app.packageName}_$index" }
                    ) { index, app ->
                        LaunchpadAppItem(
                            app = app,
                            isJiggleMode = isJiggleMode,
                            rotationAngle = if (isJiggleMode) {
                                if (index % 2 == 0) rotationAngle else -rotationAngle
                            } else 0f,
                            onLaunch = { viewModel.launchApp(context, app) },
                            onLongPress = {
                                viewModel.setJiggleMode(true)
                            },
                            onDelete = {
                                viewModel.uninstallApp(context, app)
                            },
                            onHide = {
                                viewModel.hideApp(app)
                            },
                            onPinToDockSlot = { slot ->
                                viewModel.assignAppToDockSlot(slot, app)
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
private fun LaunchpadAppItem(
    app: AppInfo,
    isJiggleMode: Boolean,
    rotationAngle: Float,
    onLaunch: () -> Unit,
    onLongPress: () -> Unit,
    onDelete: () -> Unit,
    onHide: () -> Unit,
    onPinToDockSlot: (Int) -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .rotate(rotationAngle)
            .combinedClickable(
                onClick = {
                    if (isJiggleMode) {
                        isMenuOpen = true
                    } else {
                        onLaunch()
                    }
                },
                onLongClick = {
                    onLongPress()
                    isMenuOpen = true
                }
            )
            .padding(4.dp)
    ) {
        val backgroundModifier = if (app.iconBitmap != null) {
            Modifier.background(Color.Transparent)
        } else {
            Modifier.background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF4F46E5))))
        }

        Box(contentAlignment = Alignment.TopEnd) {
            // Translucent frosted glass frame like macOS dock
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.22f),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(62.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.65f),
                                Color.White.copy(alpha = 0.18f)
                            )
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .then(backgroundModifier),
                    contentAlignment = Alignment.Center
                ) {
                    if (app.iconBitmap != null) {
                        Image(
                            bitmap = app.iconBitmap,
                            contentDescription = app.label,
                            modifier = Modifier.size(50.dp)
                        )
                    } else if (app.systemIconFallback != null) {
                        Icon(
                            imageVector = app.systemIconFallback,
                            contentDescription = app.label,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Red Delete badge when in jiggle mode (like iPhone)
            if (isJiggleMode) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .clickable { isMenuOpen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tùy chọn",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = app.label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown Menu: Di chuyển, Xóa, Ẩn như iPhone
        DropdownMenu(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false },
            modifier = Modifier.background(Color(0xFF262626))
        ) {
            DropdownMenuItem(
                text = { Text("Gỡ cài đặt (Xóa ứng dụng)", color = Color(0xFFFF5252), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp)) },
                onClick = {
                    isMenuOpen = false
                    onDelete()
                }
            )
            DropdownMenuItem(
                text = { Text("Ẩn ứng dụng khỏi màn hình", color = Color.White, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) },
                onClick = {
                    isMenuOpen = false
                    onHide()
                }
            )
            HorizontalDivider(color = Color.DarkGray)
            DropdownMenuItem(
                text = { Text("Di chuyển vào thanh Dock ô 1", color = Color.White, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp)) },
                onClick = {
                    isMenuOpen = false
                    onPinToDockSlot(0)
                }
            )
            DropdownMenuItem(
                text = { Text("Di chuyển vào thanh Dock ô 2", color = Color.White, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp)) },
                onClick = {
                    isMenuOpen = false
                    onPinToDockSlot(1)
                }
            )
            DropdownMenuItem(
                text = { Text("Di chuyển vào thanh Dock ô 3", color = Color.White, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp)) },
                onClick = {
                    isMenuOpen = false
                    onPinToDockSlot(2)
                }
            )
            DropdownMenuItem(
                text = { Text("Di chuyển vào thanh Dock ô 4", color = Color.White, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp)) },
                onClick = {
                    isMenuOpen = false
                    onPinToDockSlot(3)
                }
            )
        }
    }
}
