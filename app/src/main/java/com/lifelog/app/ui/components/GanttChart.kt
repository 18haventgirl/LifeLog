package com.lifelog.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.lifelog.app.data.model.RecordWithDetails

@Composable
fun GanttChart(
    records: List<RecordWithDetails>,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) return

    Canvas(modifier = modifier) {
        val chartLeft = 50.dp.toPx()
        val chartRight = size.width - 10.dp.toPx()
        val chartWidth = chartRight - chartLeft
        val totalSeconds = 24 * 3600f

        // 时间刻度线 + 标签
        for (hour in 0..24 step 3) {
            val x = chartLeft + (hour * 3600 / totalSeconds) * chartWidth
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(x, 0f),
                end = Offset(x, size.height - 20.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
            // 小时标签
            drawContext.canvas.nativeCanvas.drawText(
                "${hour}h",
                x,
                size.height - 4.dp.toPx(),
                android.graphics.Paint().apply {
                    textSize = 10.dp.toPx()
                    color = android.graphics.Color.parseColor("#94A3B8")
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // 记录条
        records.forEach { record ->
            val startSecond = record.startHour * 3600 + record.startMinute * 60
            val endSecond = startSecond + record.durationSeconds

            val x1 = chartLeft + (startSecond / totalSeconds) * chartWidth
            val x2 = chartLeft + (minOf(endSecond.toFloat(), totalSeconds) / totalSeconds) * chartWidth
            val barWidth = maxOf(x2 - x1, 3.dp.toPx())

            val barColor = try {
                Color(android.graphics.Color.parseColor(record.categoryColor))
            } catch (e: Exception) {
                Color(0xFF3B82F6)
            }

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x1, 10.dp.toPx()),
                size = Size(barWidth, 30.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}
