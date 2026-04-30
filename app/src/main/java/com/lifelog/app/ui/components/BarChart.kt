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
import com.lifelog.app.data.model.CategoryTimeEntry

data class BarChartEntry(
    val label: String,
    val segments: List<BarSegment>
)

data class BarSegment(
    val color: Color,
    val value: Float
)

@Composable
fun StackedBarChart(
    data: List<BarChartEntry>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    Canvas(modifier = modifier) {
        val chartLeft = 40.dp.toPx()
        val chartBottom = size.height - 24.dp.toPx()
        val chartTop = 8.dp.toPx()
        val chartHeight = chartBottom - chartTop
        val chartWidth = size.width - chartLeft - 8.dp.toPx()

        val maxValue = data.maxOf { entry -> entry.segments.sumOf { it.value.toDouble() } }.toFloat()
        if (maxValue == 0f) return@Canvas

        val barWidth = (chartWidth / data.size) * 0.6f
        val barGap = (chartWidth / data.size) * 0.4f

        // Y 轴刻度
        for (i in 0..4) {
            val y = chartTop + chartHeight * (1 - i / 4f)
            val value = (maxValue * i / 4f).toInt()
            drawContext.canvas.nativeCanvas.drawText(
                "${value / 60}h",
                chartLeft - 8.dp.toPx(),
                y + 4.dp.toPx(),
                android.graphics.Paint().apply {
                    textSize = 9.dp.toPx()
                    color = android.graphics.Color.parseColor("#94A3B8")
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        data.forEachIndexed { index, entry ->
            val x = chartLeft + index * (barWidth + barGap) + barGap / 2
            var currentY = chartBottom

            entry.segments.forEach { segment ->
                val segmentHeight = (segment.value / maxValue) * chartHeight
                currentY -= segmentHeight

                drawRoundRect(
                    color = segment.color,
                    topLeft = Offset(x, currentY),
                    size = Size(barWidth, segmentHeight),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }

            // X 轴标签
            drawContext.canvas.nativeCanvas.drawText(
                entry.label,
                x + barWidth / 2,
                chartBottom + 16.dp.toPx(),
                android.graphics.Paint().apply {
                    textSize = 9.dp.toPx()
                    color = android.graphics.Color.parseColor("#94A3B8")
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
