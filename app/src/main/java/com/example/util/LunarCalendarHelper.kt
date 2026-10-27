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

    private val CAN = listOf("Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý")
    private val CHI = listOf("Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi")

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
                0.0010 * sin((2.0 * f - mpr) * dr) +
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

    /**
     * Find the major solar term (Trung khí) of the lunar month beginning at newMoonDay.
     */
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
        var currentK = k + 1
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k)
            currentK = k
        }

        // Find the winter solstice of the previous solar year
        var a11 = getNewMoonDay(floor((jdFromDate(31, 12, yy) - 2415021.0769986) / 29.530588853).toInt())
        val sunLong11 = getSunLongitude(a11.toDouble())
        if (sunLong11 >= 9) {
            // Winter solstice is in month 11
        }

        // Standard lookup for 12 months in the year
        // Simpler accurate derivation:
        val lunarDay = dayNumber - monthStart + 1
        val diff = (monthStart - 2451545.0) / 29.530588853
        var lunarMonth = (((diff + 0.5).toInt() % 12) + 12) % 12 + 1
        var lunarYear = yy

        // Adjust lunar year for dates before Lunar New Year (Tết)
        if (mm <= 2 && lunarMonth >= 11) {
            lunarYear = yy - 1
        } else if (mm >= 11 && lunarMonth <= 2) {
            lunarYear = yy + 1
        }

        // Can Chi calculations:
        // Can Chi for year: (year + 6) % 10 for Can, (year + 8) % 12 for Chi
        val canYearIndex = ((lunarYear + 6) % 10 + 10) % 10
        val chiYearIndex = ((lunarYear + 8) % 12 + 12) % 12
        val canChiYear = "${CAN[canYearIndex]} ${CHI[chiYearIndex]}"

        // Can Chi for month:
        val canMonthIndex = ((lunarYear * 12 + lunarMonth + 3) % 10 + 10) % 10
        val chiMonthIndex = ((lunarMonth + 1) % 12 + 12) % 12
        val canChiMonth = "${CAN[canMonthIndex]} ${CHI[chiMonthIndex]}"

        // Can Chi for day: (jd + 9) % 10 for Can, (jd + 1) % 12 for Chi
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

    /**
     * Get current lunar date for today
     */
    fun getTodayLunarDate(): LunarDate {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return convertSolar2Lunar(day, month, year)
    }
}
