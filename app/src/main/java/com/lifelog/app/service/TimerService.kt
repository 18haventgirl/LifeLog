package com.lifelog.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.lifelog.app.MainActivity
import com.lifelog.app.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerService : Service() {

    companion object {
        const val CHANNEL_ID = "lifelog_timer"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_ACTIVITY_NAME = "activity_name"
        const val EXTRA_CATEGORY_ICON = "category_icon"
    }

    private var isActive = false
    private var isPaused = false
    private var elapsedSeconds = 0
    private var activityName = ""
    private var categoryIcon = ""
    private var timerJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                activityName = intent.getStringExtra(EXTRA_ACTIVITY_NAME) ?: ""
                categoryIcon = intent.getStringExtra(EXTRA_CATEGORY_ICON) ?: "⏱️"
                startTimer()
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }

    private fun startTimer() {
        isActive = true
        isPaused = false
        elapsedSeconds = 0
        NotificationHelper.createChannels(this)
        startForeground(NOTIFICATION_ID, buildNotification())
        startTicking()
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && !isPaused) {
                delay(1000)
                elapsedSeconds++
                updateNotification()
            }
        }
    }

    private fun pauseTimer() {
        isPaused = true
        timerJob?.cancel()
        updateNotification()
    }

    private fun resumeTimer() {
        isPaused = false
        startTicking()
        updateNotification()
    }

    private fun stopTimer() {
        isActive = false
        isPaused = false
        timerJob?.cancel()
        elapsedSeconds = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pauseResumeAction = if (isPaused) {
            val resumeIntent = PendingIntent.getService(
                this, 1,
                Intent(this, TimerService::class.java).apply { action = ACTION_RESUME },
                PendingIntent.FLAG_IMMUTABLE
            )
            NotificationCompat.Action.Builder(R.drawable.ic_timer, "继续", resumeIntent).build()
        } else {
            val pauseIntent = PendingIntent.getService(
                this, 1,
                Intent(this, TimerService::class.java).apply { action = ACTION_PAUSE },
                PendingIntent.FLAG_IMMUTABLE
            )
            NotificationCompat.Action.Builder(R.drawable.ic_timer, "暂停", pauseIntent).build()
        }

        val stopIntent = PendingIntent.getService(
            this, 2,
            Intent(this, TimerService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )

        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        val timeStr = "%02d:%02d:%02d".format(h, m, s)
        val statusText = if (isPaused) "已暂停 · $timeStr" else timeStr

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("$categoryIcon $activityName")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_timer)
            .setOngoing(true)
            .setContentIntent(openIntent)
            .addAction(pauseResumeAction)
            .addAction(R.drawable.ic_timer, "停止", stopIntent)
            .setSilent(true)
            .build()
    }

    private fun updateNotification() {
        val nm = getSystemService(android.app.NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, buildNotification())
    }

    override fun onDestroy() {
        timerJob?.cancel()
        super.onDestroy()
    }
}
