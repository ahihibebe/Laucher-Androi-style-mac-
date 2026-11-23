package com.example.model

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

enum class ClockStyle(
    val id: String,
    val displayName: String,
    val subtitle: String
) {
    CLASSIC("classic", "Cơ bản", "HH:mm"),
    WITH_SECONDS("seconds", "Hiện giây", "HH:mm:ss"),
    STACKED("stacked", "Xếp dọc", "2 dòng"),
    ANALOG("analog", "Đồng hồ kim", "Kim + Số"),
    PILL_BADGE("pill", "Khung nổi", "Viền sáng");

    companion object {
        fun fromId(id: String?): ClockStyle {
            return entries.find { it.id == id } ?: CLASSIC
        }
    }
}

enum class ClockFontOption(
    val id: String,
    val displayName: String,
    val subtitle: String
) {
    DEFAULT("default", "Mặc định", "Hệ thống"),
    INTER("inter", "Inter", "macOS"),
    OUTFIT("outfit", "Outfit", "Bo tròn"),
    OSWALD("oswald", "Oswald", "Cao nét"),
    JETBRAINS_MONO("jetbrains_mono", "Mono", "Kỹ thuật"),
    PLAYFAIR("playfair", "Playfair", "Cổ điển"),
    ORBITRON("orbitron", "Orbitron", "Điện tử");

    fun toFontFamily(): FontFamily {
        return when (this) {
            DEFAULT -> FontFamily.Default
            INTER -> FontFamily(Font(R.font.inter, FontWeight.Normal))
            OUTFIT -> FontFamily(Font(R.font.outfit, FontWeight.Normal))
            OSWALD -> FontFamily(Font(R.font.oswald, FontWeight.Normal))
            JETBRAINS_MONO -> FontFamily(Font(R.font.jetbrains_mono, FontWeight.Normal))
            PLAYFAIR -> FontFamily(Font(R.font.playfair_display, FontWeight.Normal))
            ORBITRON -> FontFamily(Font(R.font.orbitron, FontWeight.Normal))
        }
    }

    companion object {
        fun fromId(id: String?): ClockFontOption {
            return entries.find { it.id == id } ?: DEFAULT
        }
    }
}

enum class ClockWeightOption(
    val id: String,
    val displayName: String,
    val fontWeight: FontWeight
) {
    NORMAL("normal", "Thường", FontWeight.Normal),
    MEDIUM("medium", "Vừa", FontWeight.Medium),
    BOLD("bold", "Đậm", FontWeight.Bold),
    EXTRA_BOLD("extra_bold", "Rất đậm", FontWeight.ExtraBold);

    companion object {
        fun fromId(id: String?): ClockWeightOption {
            return entries.find { it.id == id } ?: BOLD
        }
    }
}
