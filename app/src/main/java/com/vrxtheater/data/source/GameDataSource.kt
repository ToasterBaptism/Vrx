package com.vrxtheater.data.source

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.vrxtheater.data.models.GameInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for accessing installed games
 */
@Singleton
class GameDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Returns a list of all installed games
     */
    suspend fun getInstalledGames(): List<GameInfo> {
        val packageManager = context.packageManager
        val usageStatsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        } else null
        
        // Get all installed applications
        val installedApps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(0)
        }
        
        // Get usage stats for the last year
        val endTime = System.currentTimeMillis()
        val startTime = endTime - TimeUnit.DAYS.toMillis(365)
        val usageStats = usageStatsManager?.queryUsageStats(
            UsageStatsManager.INTERVAL_YEARLY, startTime, endTime
        )?.associateBy { it.packageName } ?: emptyMap()
        
        // Filter for games and create GameInfo objects
        return installedApps
            .filter { isGame(it) }
            .map { appInfo ->
                val packageName = appInfo.packageName
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(packageName, 0)
                }
                
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val icon = packageManager.getApplicationIcon(appInfo)
                val versionName = packageInfo.versionName ?: ""
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                }
                
                val installDate = Date(packageInfo.firstInstallTime)
                val lastUsedDate = usageStats[packageName]?.lastTimeUsed?.let { Date(it) }
                
                GameInfo(
                    packageName = packageName,
                    appName = appName,
                    icon = icon,
                    versionName = versionName,
                    versionCode = versionCode,
                    installDate = installDate,
                    lastUsedDate = lastUsedDate,
                    isGame = true
                )
            }
            .sortedByDescending { it.lastUsedDate }
    }
    
    /**
     * Determines if an application is likely a game
     */
    private fun isGame(appInfo: ApplicationInfo): Boolean {
        // Check if the app is a system app
        if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            return false
        }
        
        // Check if the app has a launcher activity
        val packageManager = context.packageManager
        val launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
        if (launchIntent == null) {
            return false
        }
        
        // Check if the app is categorized as a game
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appInfo.category == ApplicationInfo.CATEGORY_GAME) {
                return true
            }
        }
        
        // Check if the app has game-related permissions or features
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                appInfo.packageName,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS)
        }
        
        // Look for game-related keywords in the app name or package
        val appName = packageManager.getApplicationLabel(appInfo).toString().lowercase()
        val packageName = appInfo.packageName.lowercase()
        val gameKeywords = listOf("game", "play", "arcade", "racing", "shooter", "rpg", "puzzle")
        
        return gameKeywords.any { keyword ->
            appName.contains(keyword) || packageName.contains(keyword)
        }
    }
    
    /**
     * Launches a game by package name
     */
    fun launchGame(packageName: String): Boolean {
        val packageManager = context.packageManager
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        
        return if (launchIntent != null) {
            launchIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            true
        } else {
            false
        }
    }
}