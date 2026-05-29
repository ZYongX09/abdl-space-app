package top.abdl.space.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    fun formatRelativeTime(dateString: String): String {
        return try {
            val date = isoFormat.parse(dateString) ?: return dateString
            val now = Date()
            val diff = now.time - date.time

            when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "刚刚"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}分钟前"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}小时前"
                diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}天前"
                else -> {
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    outputFormat.format(date)
                }
            }
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatDate(dateString: String): String {
        return try {
            val date = isoFormat.parse(dateString) ?: return dateString
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }
}
