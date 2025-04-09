package com.pstudio.blip.utilclasses

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hr ago"
        days == 1L -> "Yesterday"
        else -> {
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
