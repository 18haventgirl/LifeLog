package com.lifelog.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lifelog.app.MainActivity
import com.lifelog.app.R

object NotificationHelper {

    const val CHANNEL_TIMER = "lifelog_timer"
    const val CHANNEL_REMINDER = "lifelog_reminder"
    const val REMINDER_NOTIFICATION_ID = 100

    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val timerChannel = NotificationChannel(
            CHANNEL_TIMER,
            "LifeLog 计时器",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "显示当前活动计时状态"
            setShowBadge(false)
        }

        val reminderChannel = NotificationChannel(
            CHANNEL_REMINDER,
            "LifeLog 提醒",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "日报提醒和目标提醒"
        }

        nm.createNotificationChannel(timerChannel)
        nm.createNotificationChannel(reminderChannel)
    }

    fun buildDailyReminder(context: Context): Notification {
        val openIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_REMINDER)
            .setContentTitle("📝 LifeLog 日报提醒")
            .setContentText("今天过得怎么样？记录一下你的时间吧！")
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .build()
    }

    fun showDailyReminder(context: Context) {
        createChannels(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(REMINDER_NOTIFICATION_ID, buildDailyReminder(context))
    }
}
