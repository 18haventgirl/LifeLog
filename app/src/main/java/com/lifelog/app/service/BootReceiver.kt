package com.lifelog.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 开机后恢复通知提醒等
            // 具体的 WorkManager 调度将在后续版本中实现
        }
    }
}
