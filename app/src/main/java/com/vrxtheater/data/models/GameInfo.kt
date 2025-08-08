package com.vrxtheater.data.models

import android.graphics.drawable.Drawable
import java.util.Date

/**
 * Represents information about an installed game
 */
data class GameInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String,
    val versionCode: Long,
    val installDate: Date,
    val lastUsedDate: Date?,
    val isGame: Boolean = true
) {
    /**
     * Returns a formatted string of when the game was last played
     */
    fun getLastPlayedFormatted(): String {
        return lastUsedDate?.toString() ?: "Never played"
    }
}