package com.vrxtheater.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.vrxtheater.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Service for capturing screen content and rendering it to a VR surface
 */
@AndroidEntryPoint
class VrProjectionService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var surface: Surface? = null
    
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenDensity = 0
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Get screen metrics
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(metrics)
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(metrics)
        }
        
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        screenDensity = metrics.densityDpi
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val resultCode = it.getIntExtra("resultCode", 0)
            val resultData = it.getParcelableExtra<Intent>("resultData")
            val packageName = it.getStringExtra("packageName")
            
            if (resultCode != 0 && resultData != null) {
                // Initialize media projection
                val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                mediaProjection = projectionManager.getMediaProjection(resultCode, resultData)
                
                // Create virtual display
                createVirtualDisplay()
                
                // Launch game if package name is provided
                packageName?.let { pkg ->
                    launchGame(pkg)
                }
            }
        }
        
        return START_STICKY
    }
    
    /**
     * Creates a virtual display for screen capture
     */
    private fun createVirtualDisplay() {
        // Create image reader for capturing frames
        imageReader = ImageReader.newInstance(
            screenWidth, screenHeight, PixelFormat.RGBA_8888, 2
        )
        
        // Get surface from image reader
        surface = imageReader?.surface
        
        // Create virtual display
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "VrProjection",
            screenWidth,
            screenHeight,
            screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surface,
            null,
            null
        )
    }
    
    /**
     * Launches a game by package name
     */
    private fun launchGame(packageName: String) {
        serviceScope.launch {
            try {
                // Launch the game
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                launchIntent?.let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Creates the notification channel for foreground service
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "VR Projection Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for capturing screen content for VR"
                setSound(null, null)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Creates the notification for foreground service
     */
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("VR Theater")
            .setContentText("Capturing screen for VR display")
            .setSmallIcon(R.drawable.ic_vr)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        // Clean up resources
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        
        // Cancel coroutines
        serviceScope.cancel()
        
        super.onDestroy()
    }
    
    companion object {
        private const val CHANNEL_ID = "vr_projection_channel"
        private const val NOTIFICATION_ID = 1001
    }
}