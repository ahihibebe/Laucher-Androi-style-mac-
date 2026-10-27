package com.example.util

import com.example.model.LunarDate
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * Accurate Vietnamese Lunar Calendar calculation (Hồ Ngọc Đức algorithm for UTC+7).
 */
object LunarCalendarHelper {

    val CAN = listOf("Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý")
    val CHI = listOf("Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi")

    private const val TIMEZONE = 7.0

    /**
     * Convert Solar date (day, month, year) to Julian Day number.
     */
    private fun jdFromDate(d: Int, m: Int, y: Int): Int {
        val a = floor((14.0 - m) / 12.0).toInt()
        val yy = y + 4800 - a
        val mm = m + 12 * a - 3
        var jd = d + floor((153.0 * mm + 2.0) / 5.0).toInt() + 365 * yy + floor(yy / 4.0).toInt() -
                floor(yy / 100.0).toInt() + floor(yy / 400.0).toInt() - 32045
        if (jd < 2299161) {
            jd = d + floor((153.0 * mm + 2.0) / 5.0).toInt() + 365 * yy + floor(yy / 4.0).toInt() - 32083
        }
        return jd
    }

    /**
     * Compute the time of the k-th new moon after the epoch (approximate JDE).
     */
    private fun newMoon(k: Int): Double {
        val t = k / 1236.85
        val t2 = t * t
        val t3 = t2 * t
        val dr = PI / 180.0
        var jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * t2 - 0.000000155 * t3
        jd1 += 0.00033 * sin((166.56 + 132.87 * t - 0.009173 * t2) * dr) // planetary perturbations

        val m = 359.2242 + 29.10535608 * k - 0.0000333 * t2 - 0.00000347 * t3
        val mpr = 306.0253 + 385.81691806 * k + 0.0107306 * t2 + 0.00001236 * t3
        val f = 21.2964 + 390.67050646 * k - 0.0016528 * t2 - 0.00000239 * t3

        val pt = (0.1734 - 0.000393 * t) * sin(m * dr) +
                0.0021 * sin(2.0 * m * dr) -
                0.4068 * sin(mpr * dr) +
                0.0161 * sin(2.0 * mpr * dr) -
                0.0004 * sin(3.0 * mpr * dr) +
                0.0104 * sin(2.0 * f * dr) -
                0.0051 * sin((m + mpr) * dr) -
                0.0074 * sin((m - mpr) * dr) +
                0.0004 * sin((2.0 * f + m) * dr) -
                0.0004 * sin((2.0 * f - m) * dr) -
                0.0006 * sin((2.0 * f + mpr) * dr) +
                0.0100 * sin((2.0 * f - mpr) * dr) +
                0.0005 * sin((m + 2.0 * mpr) * dr)

        return jd1 + pt
    }

    /**
     * Compute longitude of the sun at a given Julian day.
     */
    private fun sunLongitude(jdn: Double): Double {
        val t = (jdn - 2451545.0) / 36525.0
        val t2 = t * t
        val dr = PI / 180.0
        val l0 = 280.46645 + 36000.76983 * t + 0.0003032 * t2
        val m = 357.52910 + 35999.05030 * t - 0.0001559 * t2 - 0.00000048 * t * t2
        val c = (1.914600 - 0.004817 * t - 0.000014 * t2) * sin(m * dr) +
                (0.019993 - 0.000101 * t) * sin(2.0 * m * dr) +
                0.000290 * sin(3.0 * m * dr)
        var theta = l0 + c
        theta *= dr
        theta -= 2.0 * PI * floor(theta / (2.0 * PI))
        return theta
    }

    private fun getSunLongitude(dayNumber: Double): Int {
        return floor(sunLongitude(dayNumber) / (PI / 6.0)).toInt()
    }

    private fun getNewMoonDay(k: Int): Int {
        return floor(newMoon(k) + 0.5 + TIMEZONE / 24.0).toInt()
    }

    /**
     * Converts a solar date to a Vietnamese Lunar date.
     */
    fun convertSolar2Lunar(dd: Int, mm: Int, yy: Int): LunarDate {
        val dayNumber = jdFromDate(dd, mm, yy)
        val k = floor((dayNumber - 2415021.0769986) / 29.530588853).toInt()
        var monthStart = getNewMoonDay(k + 1)
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k)
        }

        val lunarDay = dayNumber - monthStart + 1
        val diff = (monthStart - 2451545.0) / 29.530588853
        var lunarMonth = (((diff + 0.5).toInt() % 12) + 12) % 12 + 1
        var lunarYear = yy

        if (mm <= 2 && lunarMonth >= 11) {
            lunarYear = yy - 1
        } else if (mm >= 11 && lunarMonth <= 2) {
            lunarYear = yy + 1
        }

        val canYearIndex = ((lunarYear + 6) % 10 + 10) % 10
        val chiYearIndex = ((lunarYear + 8) % 12 + 12) % 12
        val canChiYear = "${CAN[canYearIndex]} ${CHI[chiYearIndex]}"

        val canMonthIndex = ((lunarYear * 12 + lunarMonth + 3) % 10 + 10) % 10
        val chiMonthIndex = ((lunarMonth + 1) % 12 + 12) % 12
        val canChiMonth = "${CAN[canMonthIndex]} ${CHI[chiMonthIndex]}"

        val canDayIndex = ((dayNumber + 9) % 10 + 10) % 10
        val chiDayIndex = ((dayNumber + 1) % 12 + 12) % 12
        val canChiDay = "${CAN[canDayIndex]} ${CHI[chiDayIndex]}"

        return LunarDate(
            day = lunarDay.coerceIn(1, 30),
            month = lunarMonth.coerceIn(1, 12),
            year = lunarYear,
            isLeap = false,
            canChiYear = canChiYear,
            canChiMonth = canChiMonth,
            canChiDay = canChiDay,
            solarDay = dd,
            solarMonth = mm,
            solarYear = yy
        )
    }

    fun getTodayLunarDate(): LunarDate {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return convertSolar2Lunar(day, month, year)
    }

    fun getDaysInSolarMonth(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * Get day of week for 1st of month: 1 = Mon, 2 = Tue, ..., 7 = Sun
     */
    fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val dow = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon ... 7 = Sat
        return if (dow == Calendar.SUNDAY) 7 else dow - 1
    }

    fun getGioHoangDao(canChiDay: String): List<String> {
        val chiDay = canChiDay.split(" ").lastOrNull() ?: "Tý"
        return when (chiDay) {
            "Dần", "Thân" -> listOf("Tý (23-1h)", "Sửu (1-3h)", "Thìn (7-9h)", "Tỵ (9-11h)", "Mùi (13-15h)", "Tuất (19-21h)")
            "Mão", "Dậu" -> listOf("Tý (23-1h)", "Dần (3-5h)", "Mão (5-7h)", "Ngọ (11-13h)", "Mùi (13-15h)", "Dậu (17-19h)")
            "Thìn", "Tuất" -> listOf("Dần (3-5h)", "Thìn (7-9h)", "Tỵ (9-11h)", "Thân (15-17h)", "Dậu (17-19h)", "Hợi (21-23h)")
            "Tỵ", "Hợi" -> listOf("Sửu (1-3h)", "Thìn (7-9h)", "Ngọ (11-13h)", "Mùi (13-15h)", "Tuất (19-21h)", "Hợi (21-23h)")
            "Tý", "Ngọ" -> listOf("Tý (23-1h)", "Sửu (1-3h)", "Mão (5-7h)", "Ngọ (11-13h)", "Thân (15-17h)", "Dậu (17-19h)")
            "Sửu", "Mùi" -> listOf("Dần (3-5h)", "Mão (5-7h)", "Tỵ (9-11h)", "Thân (15-17h)", "Tuất (19-21h)", "Hợi (21-23h)")
            else -> listOf("Tý (23-1h)", "Sửu (1-3h)", "Thìn (7-9h)", "Tỵ (9-11h)", "Mùi (13-15h)", "Tuất (19-21h)")
        }
    }

    fun getTietKhi(month: Int, day: Int): String {
        return when (month) {
            1 -> if (day < 6) "Đông chí" else if (day < 21) "Tiểu hàn" else "Đại hàn"
            2 -> if (day < 4) "Đại hàn" else if (day < 19) "Lập xuân" else "Vũ thủy"
            3 -> if (day < 6) "Vũ thủy" else if (day < 21) "Kinh trập" else "Xuân phân"
            4 -> if (day < 5) "Xuân phân" else if (day < 20) "Thanh minh" else "Cốc vũ"
            5 -> if (day < 6) "Cốc vũ" else if (day < 21) "Lập hạ" else "Tiểu mãn"
            6 -> if (day < 6) "Tiểu mãn" else if (day < 22) "Mang chủng" else "Hạ chí"
            7 -> if (day < 7) "Hạ chí" else if (day < 23) "Tiểu thử" else "Đại thử"
            8 -> if (day < 8) "Đại thử" else if (day < 23) "Lập thu" else "Xử thử"
            9 -> if (day < 8) "Xử thử" else if (day < 23) "Bạch lộ" else "Thu phân"
            10 -> if (day < 8) "Thu phân" else if (day < 24) "Hàn lộ" else "Sương giáng"
            11 -> if (day < 8) "Sương giáng" else if (day < 22) "Lập đông" else "Tiểu tuyết"
            else -> if (day < 7) "Tiểu tuyết" else if (day < 22) "Đại tuyết" else "Đông chí"
        }
    }

    fun getHuongXuatHanh(canChiDay: String): Pair<String, String> {
        val can = canChiDay.split(" ").firstOrNull() ?: "Giáp"
        return when (can) {
            "Giáp", "Ất" -> Pair("Đông Nam (Hỷ Thần)", "Đông Bắc (Tài Thần)")
            "Bính", "Đinh" -> Pair("Chính Nam (Hỷ Thần)", "Chính Đông (Tài Thần)")
            "Mậu", "Kỷ" -> Pair("Đông Nam (Hỷ Thần)", "Chính Bắc (Tài Thần)")
            "Canh", "Tân" -> Pair("Tây Bắc (Hỷ Thần)", "Tây Nam (Tài Thần)")
            else -> Pair("Tây Nam (Hỷ Thần)", "Chính Tây (Tài Thần)")
        }
    }
}
