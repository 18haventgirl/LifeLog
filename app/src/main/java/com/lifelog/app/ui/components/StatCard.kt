package com.lifelog.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatCard(
    icon: String,
    title: String,
    value: String,
    subtitle: String? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    LifeLogCard(modifier = modifier) {
        Text(icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.labelSmall)
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            color = accentColor
        )
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.labelSmall)
        }
    }
}
