package com.example.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector

data class AppInfo(
    val packageName: String,
    val label: String,
    val iconBitmap: ImageBitmap? = null,
    val systemIconFallback: ImageVector? = null,
    val isSystemApp: Boolean = false,
    val isPinnedToDock: Boolean = false
) {
    companion object {
        fun fromBitmap(
            packageName: String,
            label: String,
            bitmap: Bitmap?,
            isSystemApp: Boolean = false
        ): AppInfo {
            return AppInfo(
                packageName = packageName,
                label = label,
                iconBitmap = bitmap?.asImageBitmap(),
                isSystemApp = isSystemApp
            )
        }
    }
}
