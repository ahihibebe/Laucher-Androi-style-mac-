package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private const val TAG = "CoffeeOnlineDialog"
private const val COFFEE_ONLINE_URL = "https://giacaphe.com/gia-ca-phe-truc-tuyen/"
private const val PEPPER_ONLINE_URL = "https://giacaphe.com/gia-tieu-noi-dia/"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CoffeeOnlineDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasCrashed by remember { mutableStateOf(false) }
    var reloadTrigger by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Cà phê, 1 = Giá tiêu

    val currentUrl = if (selectedTab == 0) COFFEE_ONLINE_URL else PEPPER_ONLINE_URL

    // Safely cleanup WebView when dialog is dismissed
    DisposableEffect(Unit) {
        onDispose {
            try {
                webViewInstance?.apply {
                    stopLoading()
                    loadUrl("about:blank")
                    clearHistory()
                    (parent as? ViewGroup)?.removeView(this)
                    destroy()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error disposing WebView", e)
            }
            webViewInstance = null
        }
    }

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
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF1E1E1E),
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .fillMaxSize(0.90f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // prevent dismiss when clicking content
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.60f))
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .testTag("coffee_online_dialog")
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                // macOS Title Bar with Traffic Lights & Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFF282828))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Traffic lights
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5F56))
                                .clickable { onDismiss() }
                        )
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFBD2E))
                                .clickable {
                                    if (hasCrashed) {
                                        hasCrashed = false
                                        isLoading = true
                                        reloadTrigger++
                                    } else {
                                        webViewInstance?.reload()
                                    }
                                }
                        )
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF27C93F))
                        )
                    }

                    // Window Tabs (Cà phê vs Giá tiêu)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedTab == 0) Color(0xFF3B82F6).copy(alpha = 0.35f) else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (selectedTab != 0) {
                                        selectedTab = 0
                                        hasCrashed = false
                                        isLoading = true
                                        webViewInstance?.loadUrl(COFFEE_ONLINE_URL)
                                    }
                                }
                        ) {
                            Text(
                                text = "☕ Giá Cà Phê",
                                color = if (selectedTab == 0) Color(0xFF60A5FA) else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.5.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedTab == 1) Color(0xFF10B981).copy(alpha = 0.35f) else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (selectedTab != 1) {
                                        selectedTab = 1
                                        hasCrashed = false
                                        isLoading = true
                                        webViewInstance?.loadUrl(PEPPER_ONLINE_URL)
                                    }
                                }
                        ) {
                            Text(
                                text = "🌱 Giá Tiêu",
                                color = if (selectedTab == 1) Color(0xFF34D399) else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.5.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Actions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (hasCrashed) {
                                    hasCrashed = false
                                    isLoading = true
                                    reloadTrigger++
                                } else {
                                    webViewInstance?.reload()
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Làm mới",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error opening browser", e)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Mở bằng trình duyệt",
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // WebView or Error Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(if (hasCrashed) Color(0xFF1E1E1E) else Color.White)
                ) {
                    if (hasCrashed) {
                        // Crash Recovery UI
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Trang web gặp sự cố hiển thị",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bộ dựng trang WebView của hệ thống đã dừng đột ngột do bộ nhớ hoặc dữ liệu nặng. Ứng dụng đã được bảo vệ không bị đóng.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        hasCrashed = false
                                        isLoading = true
                                        reloadTrigger++
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF3B82F6)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Thử lại", fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Error opening external browser", e)
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInBrowser,
                                        contentDescription = null,
                                        tint = Color(0xFF60A5FA),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Mở bằng trình duyệt", color = Color(0xFF60A5FA), fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        key(reloadTrigger, selectedTab) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { ctx ->
                                    WebView(ctx).apply {
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                        settings.apply {
                                            javaScriptEnabled = true
                                            domStorageEnabled = true
                                            loadWithOverviewMode = true
                                            useWideViewPort = true
                                            builtInZoomControls = true
                                            displayZoomControls = false
                                            cacheMode = WebSettings.LOAD_DEFAULT
                                            mediaPlaybackRequiresUserGesture = true
                                            setSupportMultipleWindows(false)
                                            userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
                                        }
                                        webChromeClient = WebChromeClient()
                                        webViewClient = object : WebViewClient() {
                                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                                isLoading = true
                                            }

                                            override fun onPageFinished(view: WebView?, url: String?) {
                                                isLoading = false
                                            }

                                            override fun onReceivedError(
                                                view: WebView?,
                                                request: WebResourceRequest?,
                                                error: WebResourceError?
                                            ) {
                                                if (request?.isForMainFrame == true) {
                                                    isLoading = false
                                                }
                                            }

                                            // CRITICAL FIX: Return true so Android will NOT terminate the host app process
                                            override fun onRenderProcessGone(
                                                view: WebView?,
                                                detail: RenderProcessGoneDetail?
                                            ): Boolean {
                                                Log.e(TAG, "WebView renderer process gone (crashed: ${detail?.didCrash()})")
                                                try {
                                                    (view?.parent as? ViewGroup)?.removeView(view)
                                                    view?.destroy()
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Error destroying crashed WebView", e)
                                                }
                                                webViewInstance = null
                                                isLoading = false
                                                hasCrashed = true
                                                return true
                                            }

                                            override fun shouldOverrideUrlLoading(
                                                view: WebView?,
                                                request: WebResourceRequest?
                                            ): Boolean {
                                                val targetUrl = request?.url?.toString() ?: return false
                                                return handleUrlNavigation(targetUrl)
                                            }

                                            @Suppress("DEPRECATION")
                                            override fun shouldOverrideUrlLoading(
                                                view: WebView?,
                                                targetUrl: String?
                                            ): Boolean {
                                                if (targetUrl == null) return false
                                                return handleUrlNavigation(targetUrl)
                                            }

                                            private fun handleUrlNavigation(targetUrl: String): Boolean {
                                                return try {
                                                    val uri = Uri.parse(targetUrl)
                                                    val scheme = uri.scheme?.lowercase()
                                                    if (scheme == "http" || scheme == "https") {
                                                        val host = uri.host?.lowercase() ?: ""
                                                        if (host.contains("giacaphe.com")) {
                                                            false
                                                        } else {
                                                            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            }
                                                            context.startActivity(intent)
                                                            true
                                                        }
                                                    } else {
                                                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        context.startActivity(intent)
                                                        true
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Failed to navigate URL: $targetUrl", e)
                                                    true
                                                }
                                            }
                                        }
                                        loadUrl(currentUrl)
                                        webViewInstance = this
                                    }
                                },
                                update = { view ->
                                    webViewInstance = view
                                }
                            )
                        }

                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(color = Color(0xFFFBBF24))
                                    Text(
                                        text = "Đang tải dữ liệu trực tuyến...",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
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
