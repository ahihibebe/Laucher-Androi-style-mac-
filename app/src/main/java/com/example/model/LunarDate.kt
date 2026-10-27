package com.example.model

data class LunarDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val isLeap: Boolean,
    val canChiYear: String,
    val canChiMonth: String,
    val canChiDay: String,
    val solarDay: Int,
    val solarMonth: Int,
    val solarYear: Int
) {
    val formattedLunarShort: String
        get() = "${day}/${month}${if (isLeap) " (Nhuận)" else ""}"

    val formattedLunarFull: String
        get() = "Ngày $day tháng $month${if (isLeap) " (nhuận)" else ""}, năm $canChiYear"

    val formattedCanChiDetails: String
        get() = "Năm $canChiYear • Tháng $canChiMonth • Ngày $canChiDay"
}
