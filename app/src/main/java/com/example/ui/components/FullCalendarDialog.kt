package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.LunarDate
import com.example.util.LunarCalendarHelper
import java.util.Calendar

private data class CalendarDayItem(
    val solarDay: Int,
    val solarMonth: Int,
    val solarYear: Int,
    val isCurrentMonth: Boolean,
    val lunarDate: LunarDate,
    val isToday: Boolean
)

@Composable
fun FullCalendarDialog(
    onDismiss: () -> Unit
) {
    val todayCal = remember { Calendar.getInstance() }
    val todayYear = todayCal.get(Calendar.YEAR)
    val todayMonth = todayCal.get(Calendar.MONTH) + 1
    val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)

    var currentYear by remember { mutableIntStateOf(todayYear) }
    var currentMonth by remember { mutableIntStateOf(todayMonth) }
    var selectedDay by remember {
        mutableStateOf(
            LunarCalendarHelper.convertSolar2Lunar(todayDay, todayMonth, todayYear)
        )
    }

    val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    // Generate days for grid
    val calendarDays = remember(currentYear, currentMonth) {
        val list = mutableListOf<CalendarDayItem>()
        val totalDaysInMonth = LunarCalendarHelper.getDaysInSolarMonth(currentYear, currentMonth)
        val firstDayOfWeek = LunarCalendarHelper.getFirstDayOfWeek(currentYear, currentMonth) // 1 = T2 ... 7 = CN

        // Previous month days to fill padding
        val prevMonth = if (currentMonth == 1) 12 else currentMonth - 1
        val prevYear = if (currentMonth == 1) currentYear - 1 else currentYear
        val prevMonthTotalDays = LunarCalendarHelper.getDaysInSolarMonth(prevYear, prevMonth)
        val leadingEmptyCount = firstDayOfWeek - 1

        for (i in leadingEmptyCount downTo 1) {
            val d = prevMonthTotalDays - i + 1
            val l = LunarCalendarHelper.convertSolar2Lunar(d, prevMonth, prevYear)
            list.add(CalendarDayItem(d, prevMonth, prevYear, false, l, false))
        }

        // Current month days
        for (d in 1..totalDaysInMonth) {
            val l = LunarCalendarHelper.convertSolar2Lunar(d, currentMonth, currentYear)
            val isToday = (d == todayDay && currentMonth == todayMonth && currentYear == todayYear)
            list.add(CalendarDayItem(d, currentMonth, currentYear, true, l, isToday))
        }

        // Next month trailing days to complete full grid (multiple of 7)
        val trailingCount = (7 - (list.size % 7)) % 7
        val nextMonth = if (currentMonth == 12) 1 else currentMonth + 1
        val nextYear = if (currentMonth == 12) currentYear + 1 else currentYear
        for (d in 1..trailingCount) {
            val l = LunarCalendarHelper.convertSolar2Lunar(d, nextMonth, nextYear)
            list.add(CalendarDayItem(d, nextMonth, nextYear, false, l, false))
        }

        list
    }

    val tietKhi = remember(selectedDay.solarDay, selectedDay.solarMonth) {
        LunarCalendarHelper.getTietKhi(selectedDay.solarMonth, selectedDay.solarDay)
    }
    val gioHoangDao = remember(selectedDay.canChiDay) {
        LunarCalendarHelper.getGioHoangDao(selectedDay.canChiDay)
    }
    val huongXuatHanh = remember(selectedDay.canChiDay) {
        LunarCalendarHelper.getHuongXuatHanh(selectedDay.canChiDay)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFF1E1E1E),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .shadow(28.dp, RoundedCornerShape(26.dp))
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.60f))
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .testTag("full_calendar_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // macOS Title Bar with Traffic Lights
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
                                .clickable { onDismiss() }
                        )
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF27C93F))
                        )
                    }

                    // Window Title
                    Text(
                        text = "Lịch Vạn Niên Đầy Đủ (Âm - Dương Lịch)",
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )

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

                // Month Navigation Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF242424))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (currentMonth == 1) {
                                    currentMonth = 12
                                    currentYear -= 1
                                } else {
                                    currentMonth -= 1
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tháng trước", tint = Color.White)
                        }

                        Text(
                            text = "Tháng $currentMonth Năm $currentYear",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = {
                                if (currentMonth == 12) {
                                    currentMonth = 1
                                    currentYear += 1
                                } else {
                                    currentMonth += 1
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Tháng sau", tint = Color.White)
                        }
                    }

                    Button(
                        onClick = {
                            currentYear = todayYear
                            currentMonth = todayMonth
                            selectedDay = LunarCalendarHelper.convertSolar2Lunar(todayDay, todayMonth, todayYear)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hôm nay", fontSize = 11.sp, color = Color.White)
                    }
                }

                // Days of week header (T2 -> CN)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    daysOfWeek.forEachIndexed { idx, day ->
                        val textColor = when (idx) {
                            6 -> Color(0xFFFF5F56) // CN đỏ
                            5 -> Color(0xFF60A5FA) // T7 xanh
                            else -> Color.Gray
                        }
                        Text(
                            text = day,
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF333333))

                // Calendar Grid Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(calendarDays) { item ->
                            val isSelected = (item.solarDay == selectedDay.solarDay &&
                                    item.solarMonth == selectedDay.solarMonth &&
                                    item.solarYear == selectedDay.solarYear)

                            val bgModifier = when {
                                isSelected -> Modifier.background(Color(0xFF007AFF), RoundedCornerShape(10.dp))
                                item.isToday -> Modifier
                                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                    .border(1.5.dp, Color(0xFFFBBF24), RoundedCornerShape(10.dp))
                                else -> Modifier.background(
                                    if (item.isCurrentMonth) Color(0xFF282828) else Color(0xFF202020),
                                    RoundedCornerShape(10.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1.05f)
                                    .then(bgModifier)
                                    .clickable {
                                        selectedDay = item.lunarDate
                                        if (!item.isCurrentMonth) {
                                            currentMonth = item.solarMonth
                                            currentYear = item.solarYear
                                        }
                                    }
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Solar Day
                                    Text(
                                        text = "${item.solarDay}",
                                        color = when {
                                            isSelected -> Color.White
                                            !item.isCurrentMonth -> Color.DarkGray
                                            item.isToday -> Color(0xFFFBBF24)
                                            else -> Color.White
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Lunar Day (format: "d" or "d/m" if day 1 or 15)
                                    val lunarText = if (item.lunarDate.day == 1) {
                                        "${item.lunarDate.day}/${item.lunarDate.month}"
                                    } else {
                                        "${item.lunarDate.day}"
                                    }

                                    Text(
                                        text = lunarText,
                                        color = when {
                                            isSelected -> Color(0xFFFEF08A)
                                            item.lunarDate.day == 1 || item.lunarDate.day == 15 -> Color(0xFFFFB74D)
                                            !item.isCurrentMonth -> Color(0xFF4B5563)
                                            else -> Color(0xFF9CA3AF)
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = if (item.lunarDate.day == 1 || item.lunarDate.day == 15) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected Day Details Bottom Sheet
                Surface(
                    color = Color(0xFF252525),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Title row: Solar & Lunar dates
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Dương lịch: Ngày ${selectedDay.solarDay}/${selectedDay.solarMonth}/${selectedDay.solarYear}",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Âm lịch: Ngày ${selectedDay.day} tháng ${selectedDay.month} năm ${selectedDay.canChiYear}",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Text(
                                    text = "Ngày Hoàng Đạo",
                                    color = Color(0xFF34D399),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFF383838), modifier = Modifier.padding(vertical = 2.dp))

                        // Can Chi & Tiết Khí
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Can Chi: ${selectedDay.canChiDay} | ${selectedDay.canChiMonth}",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Tiết khí: $tietKhi",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Giờ Hoàng Đạo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Giờ Hoàng Đạo:",
                                color = Color(0xFFFBBF24),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = gioHoangDao.joinToString(", "),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }

                        // Hướng Xuất Hành
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = Color(0xFFF472B6), modifier = Modifier.size(13.dp))
                            Text(
                                text = "Xuất hành: Hỷ Thần: ${huongXuatHanh.first} | Tài Thần: ${huongXuatHanh.second}",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
