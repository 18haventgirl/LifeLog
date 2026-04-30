package com.lifelog.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.app.ui.theme.Amber500
import com.lifelog.app.ui.theme.Green500
import com.lifelog.app.domain.util.TimeUtils

@Composable
fun CurrentActivityCard(
    isActive: Boolean,
    isPaused: Boolean,
    categoryIcon: String,
    activityName: String,
    categoryColor: Color,
    elapsedSeconds: Int,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isActive) categoryColor else MaterialTheme.colorScheme.outline,
        animationSpec = tween(500),
        label = "timerColor"
    )

    val animatedSeconds by animateIntAsState(
        targetValue = elapsedSeconds,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "timerSeconds"
    )

    LifeLogCard(
        containerColor = if (isActive) {
            animatedColor.copy(alpha = 0.08f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Text(categoryIcon, fontSize = 36.sp)
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activityName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isActive) animatedColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isActive && isPaused) {
                    Text("已暂停", color = Amber500, fontSize = 12.sp)
                }
            }

            // 计时器
            Text(
                text = TimeUtils.formatTime(animatedSeconds),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isPaused) Amber500 else animatedColor
            )
        }

        if (isActive) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onStop, modifier = Modifier.height(36.dp)) {
                    Icon(Icons.Default.Stop, contentDescription = "停止", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("停止", fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))

                if (isPaused) {
                    Button(
                        onClick = onResume,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green500)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "继续", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("继续", fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = onPause,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "暂停", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("暂停", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
