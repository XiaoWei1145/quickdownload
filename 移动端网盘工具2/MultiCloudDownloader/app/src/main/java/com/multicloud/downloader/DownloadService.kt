package com.multicloud.downloader.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class DownloadService : Service() {
    private val binder = DownloadBinder()
    private var isForeground = false
    
    inner class DownloadBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForegroundNotification()
        return START_STICKY
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "下载通知",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "文件下载状态通知"
        }
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun startForegroundNotification() {
        if (!isForeground) {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("多云下载器")
                .setContentText("正在后台运行")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .build()
            
            startForeground(NOTIFICATION_ID, notification)
            isForeground = true
        }
    }
    
    fun updateProgressNotification(fileName: String, progress: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("正在下载: $fileName")
            .setContentText("$progress%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, false)
            .build()
        
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }
    
    fun completeNotification(fileName: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("下载完成")
            .setContentText("$fileName 下载完成")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .build()
        
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }
    
    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1
    }
}