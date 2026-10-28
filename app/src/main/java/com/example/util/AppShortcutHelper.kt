package com.example.util

import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.Bitmap
import android.os.Build
import android.os.Process
import androidx.core.graphics.drawable.toBitmap

data class AppShortcutItem(
    val id: String,
    val packageName: String,
    val shortLabel: String,
    val iconBitmap: Bitmap? = null
)

object AppShortcutHelper {

    fun getAppShortcuts(context: Context, packageName: String): List<AppShortcutItem> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return emptyList()
        if (packageName.startsWith("action:")) return emptyList()

        return try {
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps ?: return emptyList()
            val query = LauncherApps.ShortcutQuery().apply {
                setQueryFlags(
                    LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                    LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                    LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
                )
                setPackage(packageName)
            }
            val userHandle = Process.myUserHandle()
            val shortcuts = launcherApps.getShortcuts(query, userHandle) ?: emptyList()

            shortcuts.take(5).map { shortcut ->
                val label = shortcut.shortLabel?.toString()
                    ?: shortcut.longLabel?.toString()
                    ?: shortcut.id

                val bitmap = try {
                    val drawable = launcherApps.getShortcutIconDrawable(
                        shortcut,
                        context.resources.displayMetrics.densityDpi
                    )
                    drawable?.toBitmap()
                } catch (e: Exception) {
                    null
                }

                AppShortcutItem(
                    id = shortcut.id,
                    packageName = packageName,
                    shortLabel = label,
                    iconBitmap = bitmap
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun launchShortcut(context: Context, shortcut: AppShortcutItem): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return false
        return try {
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps ?: return false
            launcherApps.startShortcut(
                shortcut.packageName,
                shortcut.id,
                null,
                null,
                Process.myUserHandle()
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
