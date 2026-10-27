package com.example.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class WallpaperTheme(
    val id: String,
    val name: String,
    val description: String,
    val backgroundBrush: Brush,
    val previewColor: Color
)

object WallpaperPresets {
    val presets = listOf(
        WallpaperTheme(
            id = "sequoia",
            name = "macOS Sequoia",
            description = "Sắc tím, cam hoàng hôn và xanh đại dương đặc trưng macOS 15",
            backgroundBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F172A),
                    Color(0xFF3B1D54),
                    Color(0xFF6B21A8),
                    Color(0xFFD946EF),
                    Color(0xFFF97316)
                )
            ),
            previewColor = Color(0xFFD946EF)
        ),
        WallpaperTheme(
            id = "sonoma",
            name = "macOS Sonoma",
            description = "Gam màu đồi cát ấm áp và ánh nắng vàng California",
            backgroundBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0C1929),
                    Color(0xFF1E3A5F),
                    Color(0xFF3B82F6),
                    Color(0xFFF59E0B),
                    Color(0xFFFBBF24)
                )
            ),
            previewColor = Color(0xFFF59E0B)
        ),
        WallpaperTheme(
            id = "ventura",
            name = "macOS Ventura",
            description = "Cánh hoa rực rỡ vàng nghệ và đỏ ruby",
            backgroundBrush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF97316),
                    Color(0xFFEA580C),
                    Color(0xFF991B1B),
                    Color(0xFF18181B)
                )
            ),
            previewColor = Color(0xFFEA580C)
        ),
        WallpaperTheme(
            id = "monterey",
            name = "macOS Monterey",
            description = "Vịnh biển Monterey huyền ảo với sắc tím neon",
            backgroundBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF111827),
                    Color(0xFF312E81),
                    Color(0xFF4C1D95),
                    Color(0xFF8B5CF6)
                )
            ),
            previewColor = Color(0xFF8B5CF6)
        ),
        WallpaperTheme(
            id = "dark_aurora",
            name = "Dark Aurora",
            description = "Cực quang xanh ngọc trên nền tối sang trọng",
            backgroundBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF050B14),
                    Color(0xFF064E3B),
                    Color(0xFF047857),
                    Color(0xFF10B981),
                    Color(0xFF022C22)
                )
            ),
            previewColor = Color(0xFF10B981)
        ),
        WallpaperTheme(
            id = "midnight_slate",
            name = "Space Black",
            description = "Màu xám kim loại tối giản chuyên nghiệp",
            backgroundBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F172A),
                    Color(0xFF1E293B),
                    Color(0xFF0A0F1D)
                )
            ),
            previewColor = Color(0xFF1E293B)
        )
    )

    fun getById(id: String): WallpaperTheme {
        return presets.find { it.id == id } ?: presets.first()
    }
}
