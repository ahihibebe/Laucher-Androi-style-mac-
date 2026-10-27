package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.model.AppInfo
import com.example.viewmodel.LauncherViewModel
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
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

    // Voice Search Speech Recognizer
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.setSearchQuery(spokenText)
            }
        }
    }

    // Wiggle animation like iPhone
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

    var dragYTotal by remember { mutableFloatStateOf(0f) }

    // Drag-and-drop reordering state
    var draggedPackageName by remember { mutableStateOf<String?>(null) }
    var draggedCurrentIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffset by remember { mutableStateOf(Offset.Zero) }
    val itemCenters = remember { mutableMapOf<Int, Offset>() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarTop)
        ) {
            // Header: Back button + Search bar with Microphone + Done button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { dragYTotal = 0f },
                            onDrag = { _, dragAmount -> dragYTotal += dragAmount.y },
                            onDragEnd = {
                                if (dragYTotal > 60f) {
                                    onNavigateBack()
                                }
                                dragYTotal = 0f
                            },
                            onDragCancel = { dragYTotal = 0f }
                        )
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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

                // Spotlight search field with Mic
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Xóa tìm kiếm",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Microphone Voice Search Button
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói tên ứng dụng bạn muốn tìm...")
                                    }
                                    speechLauncher.launch(intent)
                                } catch (e: Exception) {
                                    // Fallback if speech recognizer is unavailable
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Tìm kiếm bằng giọng nói",
                                tint = Color(0xFFF472B6),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

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
                    text = if (isJiggleMode) "Kéo thả để đổi vị trí • Nhấn X để xóa/ẩn" else "${apps.size} ứng dụng",
                    color = if (isJiggleMode) Color(0xFFF472B6) else Color.White.copy(alpha = 0.6f),
                    fontSize = 11.5.sp,
                    fontWeight = if (isJiggleMode) FontWeight.Medium else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Apps Grid
            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không tìm thấy ứng dụng nào khớp",
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
                    itemsIndexed(
                        items = apps,
                        key = { _, app -> app.packageName }
                    ) { index, app ->
                        val isDraggingThis = (app.packageName == draggedPackageName)

                        LaunchpadAppItem(
                            app = app,
                            index = index,
                            isJiggleMode = isJiggleMode,
                            isDraggingThis = isDraggingThis,
                            draggingOffset = if (isDraggingThis) draggingOffset else Offset.Zero,
                            rotationAngle = if (isJiggleMode && !isDraggingThis) {
                                if (index % 2 == 0) rotationAngle else -rotationAngle
                            } else 0f,
                            onLaunch = { viewModel.launchApp(context, app) },
                            onStartDrag = {
                                draggedPackageName = app.packageName
                                draggedCurrentIndex = index
                                draggingOffset = Offset.Zero
                                viewModel.setJiggleMode(true)
                                viewModel.triggerVibration()
                            },
                            onDrag = { dragAmount ->
                                draggingOffset += dragAmount

                                val curIdx = draggedCurrentIndex
                                if (curIdx != null && curIdx in apps.indices) {
                                    val currentSlotCenter = itemCenters[curIdx]
                                    if (currentSlotCenter != null) {
                                        val floatingCenter = currentSlotCenter + draggingOffset

                                        var closestIndex: Int = curIdx
                                        var minDistanceSq = Float.MAX_VALUE

                                        itemCenters.forEach { (targetIdx, targetCenter) ->
                                            if (targetIdx in apps.indices) {
                                                val dx = floatingCenter.x - targetCenter.x
                                                val dy = floatingCenter.y - targetCenter.y
                                                val distSq = dx * dx + dy * dy
                                                if (distSq < minDistanceSq) {
                                                    minDistanceSq = distSq
                                                    closestIndex = targetIdx
                                                }
                                            }
                                        }

                                        if (closestIndex != curIdx) {
                                            val targetCenter = itemCenters[closestIndex]
                                            if (targetCenter != null) {
                                                val delta = targetCenter - currentSlotCenter
                                                draggingOffset -= delta
                                                viewModel.moveAppToIndex(curIdx, closestIndex)
                                                draggedCurrentIndex = closestIndex
                                            }
                                        }
                                    }
                                }
                            },
                            onDragEnd = {
                                draggedPackageName = null
                                draggedCurrentIndex = null
                                draggingOffset = Offset.Zero
                                viewModel.saveCurrentAppOrder()
                            },
                            onCancelDrag = {
                                draggedPackageName = null
                                draggedCurrentIndex = null
                                draggingOffset = Offset.Zero
                            },
                            onPositioned = { center ->
                                itemCenters[index] = center
                            },
                            onReorder = {
                                viewModel.openReorderDialog(app)
                            },
                            onDelete = {
                                viewModel.uninstallApp(context, app)
                            },
                            onHide = {
                                viewModel.hideApp(app)
                            },
                            modifier = if (!isDraggingThis) {
                                Modifier.animateItemPlacement()
                            } else {
                                Modifier
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LaunchpadAppItem(
    app: AppInfo,
    index: Int,
    isJiggleMode: Boolean,
    isDraggingThis: Boolean,
    draggingOffset: Offset,
    rotationAngle: Float,
    onLaunch: () -> Unit,
    onStartDrag: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (didDragDistance: Boolean) -> Unit,
    onCancelDrag: () -> Unit,
    onPositioned: (Offset) -> Unit,
    onReorder: () -> Unit,
    onDelete: () -> Unit,
    onHide: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .zIndex(if (isDraggingThis) 100f else 1f)
            .graphicsLayer {
                if (isDraggingThis) {
                    translationX = draggingOffset.x
                    translationY = draggingOffset.y
                    scaleX = 1.15f
                    scaleY = 1.15f
                    shadowElevation = 32f
                } else {
                    rotationZ = rotationAngle
                }
            }
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInRoot()
                val size = coordinates.size
                onPositioned(Offset(pos.x + size.width / 2f, pos.y + size.height / 2f))
            }
            .pointerInput(app.packageName, isJiggleMode) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        totalDragDistance = 0f
                        onStartDrag()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragDistance += (abs(dragAmount.x) + abs(dragAmount.y))
                        onDrag(dragAmount)
                    },
                    onDragEnd = {
                        val didMove = totalDragDistance > 15f
                        onDragEnd(didMove)
                        if (!didMove && !isJiggleMode) {
                            isMenuOpen = true
                        }
                    },
                    onDragCancel = {
                        onCancelDrag()
                    }
                )
            }
            .clickable {
                if (isJiggleMode) {
                    isMenuOpen = true
                } else {
                    onLaunch()
                }
            }
            .padding(4.dp)
    ) {
        val backgroundModifier = if (app.iconBitmap != null) {
            Modifier.background(Color.Transparent)
        } else {
            Modifier.background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF4F46E5))))
        }

        Box(contentAlignment = Alignment.TopEnd) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.22f),
                shadowElevation = if (isDraggingThis) 16.dp else 8.dp,
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

            if (isJiggleMode) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
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

        // Dropdown Menu: Sắp xếp (Di chuyển vị trí), Ẩn, Gỡ cài đặt
        DropdownMenu(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false },
            modifier = Modifier.background(Color(0xFF262626))
        ) {
            // Sắp xếp / Di chuyển vị trí giữa các ứng dụng
            DropdownMenuItem(
                text = { Text("Sắp xếp (Di chuyển vị trí)", color = Color(0xFF60A5FA), fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp)) },
                onClick = {
                    isMenuOpen = false
                    onReorder()
                }
            )
            HorizontalDivider(color = Color.DarkGray)
            DropdownMenuItem(
                text = { Text("Ẩn ứng dụng khỏi màn hình", color = Color.White, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) },
                onClick = {
                    isMenuOpen = false
                    onHide()
                }
            )
            DropdownMenuItem(
                text = { Text("Gỡ cài đặt (Xóa ứng dụng)", color = Color(0xFFFF5252), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp)) },
                onClick = {
                    isMenuOpen = false
                    onDelete()
                }
            )
        }
    }
}
