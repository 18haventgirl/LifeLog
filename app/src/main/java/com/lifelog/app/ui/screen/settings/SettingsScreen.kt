package com.lifelog.app.ui.screen.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lifelog.app.ui.components.AnimatedCard
import com.lifelog.app.ui.components.LifeLogCard
import java.io.File

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "⚙️ 设置",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 外观
        AnimatedCard(index = 0) {
            LifeLogCard {
                Text("外观", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                SettingSwitchItem(
                    title = "深色模式",
                    subtitle = if (uiState.darkTheme) "已开启" else "跟随系统或手动切换",
                    checked = uiState.darkTheme,
                    onCheckedChange = { viewModel.setDarkTheme(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 通知
        AnimatedCard(index = 1) {
            LifeLogCard {
                Text("通知", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                SettingSwitchItem(
                    title = "日报提醒",
                    subtitle = "每天 ${"%02d:%02d".format(uiState.notificationHour, uiState.notificationMinute)} 提醒填写日报",
                    checked = uiState.notificationEnabled,
                    onCheckedChange = { viewModel.setNotificationEnabled(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 数据
        AnimatedCard(index = 2) {
            LifeLogCard {
                Text("数据", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                SettingClickableItem(
                    title = "导出数据",
                    subtitle = "导出为 JSON 文件并分享",
                    onClick = { viewModel.exportData() }
                )
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                SettingClickableItem(
                    title = "备份数据",
                    subtitle = "备份数据库到本地存储",
                    onClick = { viewModel.backupData() }
                )
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                SettingClickableItem(
                    title = "恢复数据",
                    subtitle = "从最近的备份恢复（需重启APP）",
                    onClick = { viewModel.restoreData() }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 关于
        AnimatedCard(index = 3) {
            LifeLogCard {
                Text("关于", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                SettingItem(title = "版本", subtitle = "v1.0.0")
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                SettingItem(
                    title = "隐私政策",
                    subtitle = "所有数据纯本地存储，不联网不上传"
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // 导出成功后自动分享
    LaunchedEffect(uiState.exportFilePath) {
        uiState.exportFilePath?.let { path ->
            shareFile(context, path)
        }
    }
}

private fun shareFile(context: Context, filePath: String) {
    try {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "分享导出数据"))
    } catch (_: Exception) { }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingClickableItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingItem(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
