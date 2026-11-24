package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClockFontOption
import com.example.model.ClockStyle
import com.example.model.ClockWeightOption
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockDisplayView(
    currentTime: Calendar,
    formattedDate: String,
    is24Hour: Boolean,
    clockStyle: ClockStyle,
    clockFont: ClockFontOption,
    clockWeight: ClockWeightOption,
    clockFontSize: Float,
    centered: Boolean = false,
    modifier: Modifier = Modifier
) {
    val fontFamily = remember(clockFont) { clockFont.toFontFamily() }
    val fontWeight = clockWeight.fontWeight
    val requestedSize = clockFontSize.coerceIn(20f, 44f)

    val hourText = remember(currentTime.timeInMillis, is24Hour) {
        SimpleDateFormat(if (is24Hour) "HH" else "hh", Locale.getDefault()).format(currentTime.time)
    }
    val minuteText = remember(currentTime.timeInMillis) {
        SimpleDateFormat("mm", Locale.getDefault()).format(currentTime.time)
    }
    val secondText = remember(currentTime.timeInMillis) {
        SimpleDateFormat("ss", Locale.getDefault()).format(currentTime.time)
    }
    val amPmText = remember(currentTime.timeInMillis, is24Hour) {
        if (is24Hour) "" else SimpleDateFormat("a", Locale.getDefault()).format(currentTime.time)
    }

    val shadowStyle = remember {
        Shadow(
            color = Color.Black.copy(alpha = 0.45f),
            offset = Offset(0f, 2f),
            blurRadius = 6f
        )
    }

    val horizontalAlign = if (centered) Alignment.CenterHorizontally else Alignment.Start
    val textAlign = if (centered) TextAlign.Center else TextAlign.Start
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = if (centered) Alignment.Center else Alignment.CenterStart
    ) {
        val maxWidthPx = with(density) { maxWidth.toPx() }.coerceAtLeast(1f)

        // Tự động tính tỉ lệ thu gọn vừa khít khung để giờ luôn căn đều, không đụng hoặc tràn qua dấu gạch |
        val autoScale = remember(
            maxWidthPx,
            clockStyle,
            clockFont,
            clockWeight,
            requestedSize,
            hourText,
            minuteText,
            secondText,
            amPmText
        ) {
            if (maxWidth == androidx.compose.ui.unit.Dp.Infinity) {
                1f
            } else {
                val safePaddingPx = with(density) { 4.dp.toPx() }
                val availablePx = (maxWidthPx - safePaddingPx).coerceAtLeast(24f)

                when (clockStyle) {
                    ClockStyle.CLASSIC -> {
                        val measuredWidth = textMeasurer.measure(
                            text = buildAnnotatedString {
                                append("$hourText:$minuteText")
                                if (amPmText.isNotEmpty()) {
                                    append(" ")
                                    withStyle(SpanStyle(fontSize = (requestedSize * 0.44f).sp, fontWeight = FontWeight.SemiBold)) {
                                        append(amPmText)
                                    }
                                }
                            },
                            style = TextStyle(
                                fontSize = requestedSize.sp,
                                fontWeight = fontWeight,
                                fontFamily = fontFamily,
                                letterSpacing = (-0.5).sp
                            )
                        ).size.width.toFloat()
                        if (measuredWidth > availablePx) (availablePx / measuredWidth).coerceIn(0.55f, 1f) else 1f
                    }

                    ClockStyle.WITH_SECONDS -> {
                        val adjustedMain = (requestedSize * 0.88f).coerceIn(18f, 38f)
                        val measuredWidth = textMeasurer.measure(
                            text = buildAnnotatedString {
                                append("$hourText:$minuteText")
                                withStyle(SpanStyle(fontSize = (adjustedMain * 0.62f).sp, fontWeight = fontWeight)) {
                                    append(":$secondText")
                                }
                                if (amPmText.isNotEmpty()) {
                                    append(" ")
                                    withStyle(SpanStyle(fontSize = (adjustedMain * 0.38f).sp, fontWeight = FontWeight.SemiBold)) {
                                        append(amPmText)
                                    }
                                }
                            },
                            style = TextStyle(
                                fontSize = adjustedMain.sp,
                                fontWeight = fontWeight,
                                fontFamily = fontFamily,
                                letterSpacing = (-0.5).sp
                            )
                        ).size.width.toFloat()
                        if (measuredWidth > availablePx) (availablePx / measuredWidth).coerceIn(0.52f, 1f) else 1f
                    }

                    ClockStyle.STACKED -> {
                        val stackedSize = (requestedSize * 0.78f).coerceIn(18f, 34f)
                        val measuredWidth = textMeasurer.measure(
                            text = buildAnnotatedString {
                                append(minuteText)
                                if (amPmText.isNotEmpty()) {
                                    append(" ")
                                    withStyle(SpanStyle(fontSize = (stackedSize * 0.38f).sp, fontWeight = FontWeight.SemiBold)) {
                                        append(amPmText)
                                    }
                                }
                            },
                            style = TextStyle(
                                fontSize = stackedSize.sp,
                                fontWeight = fontWeight,
                                fontFamily = fontFamily
                            )
                        ).size.width.toFloat()
                        if (measuredWidth > availablePx) (availablePx / measuredWidth).coerceIn(0.6f, 1f) else 1f
                    }

                    ClockStyle.ANALOG -> {
                        val dialPx = with(density) { (requestedSize * 1.42f).coerceIn(36f, 54f).dp.toPx() }
                        val gapPx = with(density) { 8.dp.toPx() }
                        val digitalSize = (requestedSize * 0.62f).coerceIn(14f, 25f)
                        val textSample = if (amPmText.isEmpty()) "$hourText:$minuteText" else "$hourText:$minuteText $amPmText"
                        val textWidth = textMeasurer.measure(
                            text = textSample,
                            style = TextStyle(
                                fontSize = digitalSize.sp,
                                fontWeight = fontWeight,
                                fontFamily = fontFamily
                            )
                        ).size.width.toFloat()
                        val totalWidth = dialPx + gapPx + textWidth
                        if (totalWidth > availablePx) (availablePx / totalWidth).coerceIn(0.55f, 1f) else 1f
                    }

                    ClockStyle.PILL_BADGE -> {
                        val pillTextSize = (requestedSize * 0.84f).coerceIn(18f, 36f)
                        val pillPaddingPx = with(density) { 22.dp.toPx() }
                        val textWidth = textMeasurer.measure(
                            text = buildAnnotatedString {
                                append("$hourText:$minuteText")
                                if (amPmText.isNotEmpty()) {
                                    append(" ")
                                    withStyle(SpanStyle(fontSize = (pillTextSize * 0.44f).sp, fontWeight = FontWeight.SemiBold)) {
                                        append(amPmText)
                                    }
                                }
                            },
                            style = TextStyle(
                                fontSize = pillTextSize.sp,
                                fontWeight = fontWeight,
                                fontFamily = fontFamily
                            )
                        ).size.width.toFloat()
                        val totalWidth = textWidth + pillPaddingPx
                        if (totalWidth > availablePx) (availablePx / totalWidth).coerceIn(0.55f, 1f) else 1f
                    }
                }
            }
        }

        val baseSize = requestedSize * autoScale
        val dateSize = (requestedSize * 0.36f).coerceIn(10f, 13f)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = horizontalAlign,
            verticalArrangement = Arrangement.Center
        ) {
            when (clockStyle) {
                ClockStyle.CLASSIC -> {
                    val annotated = buildAnnotatedString {
                        append("$hourText:$minuteText")
                        if (amPmText.isNotEmpty()) {
                            append(" ")
                            withStyle(
                                SpanStyle(
                                    fontSize = (baseSize * 0.44f).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            ) {
                                append(amPmText)
                            }
                        }
                    }
                    Text(
                        text = annotated,
                        color = Color.White,
                        fontSize = baseSize.sp,
                        fontWeight = fontWeight,
                        fontFamily = fontFamily,
                        lineHeight = (baseSize * 1.05f).sp,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = textAlign,
                        style = TextStyle(shadow = shadowStyle, letterSpacing = (-0.5).sp)
                    )
                }

                ClockStyle.WITH_SECONDS -> {
                    val adjustedMain = (requestedSize * 0.88f).coerceIn(18f, 38f) * autoScale
                    val annotated = buildAnnotatedString {
                        append("$hourText:$minuteText")
                        withStyle(
                            SpanStyle(
                                fontSize = (adjustedMain * 0.62f).sp,
                                fontWeight = fontWeight,
                                color = Color(0xFFFDE047)
                            )
                        ) {
                            append(":$secondText")
                        }
                        if (amPmText.isNotEmpty()) {
                            append(" ")
                            withStyle(
                                SpanStyle(
                                    fontSize = (adjustedMain * 0.38f).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            ) {
                                append(amPmText)
                            }
                        }
                    }
                    Text(
                        text = annotated,
                        color = Color.White,
                        fontSize = adjustedMain.sp,
                        fontWeight = fontWeight,
                        fontFamily = fontFamily,
                        lineHeight = (adjustedMain * 1.05f).sp,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = textAlign,
                        style = TextStyle(shadow = shadowStyle, letterSpacing = (-0.5).sp)
                    )
                }

                ClockStyle.STACKED -> {
                    val stackedSize = (requestedSize * 0.78f).coerceIn(18f, 34f) * autoScale
                    Column(
                        horizontalAlignment = horizontalAlign,
                        verticalArrangement = Arrangement.spacedBy((-4).dp)
                    ) {
                        Text(
                            text = hourText,
                            color = Color.White,
                            fontSize = stackedSize.sp,
                            fontWeight = fontWeight,
                            fontFamily = fontFamily,
                            lineHeight = (stackedSize * 0.92f).sp,
                            maxLines = 1,
                            textAlign = textAlign,
                            style = TextStyle(shadow = shadowStyle)
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = minuteText,
                                color = Color(0xFF93C5FD),
                                fontSize = stackedSize.sp,
                                fontWeight = fontWeight,
                                fontFamily = fontFamily,
                                lineHeight = (stackedSize * 0.92f).sp,
                                maxLines = 1,
                                style = TextStyle(shadow = shadowStyle)
                            )
                            if (amPmText.isNotEmpty()) {
                                Text(
                                    text = amPmText,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = (stackedSize * 0.38f).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = fontFamily,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                        }
                    }
                }

                ClockStyle.ANALOG -> {
                    val dialSize = ((requestedSize * 1.42f).coerceIn(36f, 54f) * autoScale).dp
                    val digitalSize = (requestedSize * 0.62f).coerceIn(14f, 25f) * autoScale
                    val hours = currentTime.get(Calendar.HOUR)
                    val minutes = currentTime.get(Calendar.MINUTE)
                    val seconds = currentTime.get(Calendar.SECOND)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Canvas(modifier = Modifier.size(dialSize)) {
                            val radius = size.minDimension / 2f
                            val center = Offset(size.width / 2f, size.height / 2f)

                            drawCircle(
                                color = Color.White.copy(alpha = 0.14f),
                                radius = radius,
                                center = center
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.72f),
                                radius = radius - 1.dp.toPx(),
                                center = center,
                                style = Stroke(width = 1.6.dp.toPx())
                            )

                            for (tick in 0 until 12) {
                                val angleRad = (tick * 30.0 - 90.0) * PI / 180.0
                                val innerR = radius * if (tick % 3 == 0) 0.72f else 0.82f
                                val outerR = radius * 0.91f
                                drawLine(
                                    color = Color.White.copy(alpha = if (tick % 3 == 0) 0.9f else 0.45f),
                                    start = Offset(
                                        center.x + (innerR * cos(angleRad)).toFloat(),
                                        center.y + (innerR * sin(angleRad)).toFloat()
                                    ),
                                    end = Offset(
                                        center.x + (outerR * cos(angleRad)).toFloat(),
                                        center.y + (outerR * sin(angleRad)).toFloat()
                                    ),
                                    strokeWidth = if (tick % 3 == 0) 1.5.dp.toPx() else 1.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            // Hour hand
                            val hourAngle = ((hours + minutes / 60.0) * 30.0 - 90.0) * PI / 180.0
                            drawLine(
                                color = Color.White,
                                start = center,
                                end = Offset(
                                    center.x + (radius * 0.48f * cos(hourAngle)).toFloat(),
                                    center.y + (radius * 0.48f * sin(hourAngle)).toFloat()
                                ),
                                strokeWidth = 2.4.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Minute hand
                            val minAngle = ((minutes + seconds / 60.0) * 6.0 - 90.0) * PI / 180.0
                            drawLine(
                                color = Color(0xFF93C5FD),
                                start = center,
                                end = Offset(
                                    center.x + (radius * 0.70f * cos(minAngle)).toFloat(),
                                    center.y + (radius * 0.70f * sin(minAngle)).toFloat()
                                ),
                                strokeWidth = 1.8.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Second hand
                            val secAngle = (seconds * 6.0 - 90.0) * PI / 180.0
                            drawLine(
                                color = Color(0xFFFFB74D),
                                start = center,
                                end = Offset(
                                    center.x + (radius * 0.78f * cos(secAngle)).toFloat(),
                                    center.y + (radius * 0.78f * sin(secAngle)).toFloat()
                                ),
                                strokeWidth = 1.1.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            drawCircle(
                                color = Color(0xFFFFB74D),
                                radius = 2.2.dp.toPx(),
                                center = center
                            )
                        }

                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = if (amPmText.isEmpty()) "$hourText:$minuteText" else "$hourText:$minuteText $amPmText",
                                color = Color.White,
                                fontSize = digitalSize.sp,
                                fontWeight = fontWeight,
                                fontFamily = fontFamily,
                                maxLines = 1,
                                softWrap = false,
                                style = TextStyle(shadow = shadowStyle)
                            )
                        }
                    }
                }

                ClockStyle.PILL_BADGE -> {
                    val pillTextSize = (requestedSize * 0.84f).coerceIn(18f, 36f) * autoScale
                    val pillShape = RoundedCornerShape(14.dp)
                    Box(
                        modifier = Modifier
                            .clip(pillShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF3B82F6).copy(alpha = 0.28f),
                                        Color(0xFFEC4899).copy(alpha = 0.24f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF93C5FD).copy(alpha = 0.75f),
                                        Color(0xFFF9A8D4).copy(alpha = 0.75f)
                                    )
                                ),
                                shape = pillShape
                            )
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val annotated = buildAnnotatedString {
                            append("$hourText:$minuteText")
                            if (amPmText.isNotEmpty()) {
                                append(" ")
                                withStyle(
                                    SpanStyle(
                                        fontSize = (pillTextSize * 0.44f).sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                ) {
                                    append(amPmText)
                                }
                            }
                        }
                        Text(
                            text = annotated,
                            color = Color.White,
                            fontSize = pillTextSize.sp,
                            fontWeight = fontWeight,
                            fontFamily = fontFamily,
                            maxLines = 1,
                            softWrap = false,
                            style = TextStyle(shadow = shadowStyle)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = formattedDate,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = dateSize.sp,
                fontWeight = FontWeight.Medium,
                textAlign = textAlign,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
