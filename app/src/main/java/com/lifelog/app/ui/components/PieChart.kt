package com.lifelog.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class PieSlice(
    val label: String,
    val value: Float,
    val color: Color,
    val percent: Float
)

@Composable
fun PieChart(
    data: List<PieSlice>,
    modifier: Modifier = Modifier,
    innerRadiusRatio: Float = 0.55f
) {
    val surfaceColor = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier) {
        val total = data.sumOf { it.value.toDouble() }.toFloat()
        if (total == 0f) return@Canvas

        val chartSize = minOf(this.size.width, this.size.height)
        val radius = chartSize / 2 - 20.dp.toPx()
        val center = Offset(this.size.width / 2, this.size.height / 2)

        var startAngle = -90f

        data.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            startAngle += sweepAngle
        }

        val innerRadius = radius * innerRadiusRatio
        drawCircle(
            color = surfaceColor,
            radius = innerRadius,
            center = center
        )
    }
}
