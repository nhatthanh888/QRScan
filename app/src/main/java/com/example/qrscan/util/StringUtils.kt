package com.example.qrscan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64.*
import android.webkit.MimeTypeMap
import java.io.ByteArrayOutputStream
import java.text.Normalizer
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


object StringUtils {

    fun getSecondDisplay(second: Int): String {
        val realSecond = second % 60
        val minute = second / 60
//        return if (minute < 10) {
//            String.format("%02d:%02d", minute, realSecond)
//        } else String.format("%logo:%02d", minute, realSecond)
        return String.format("%logo:%02d", minute, realSecond)
    }

    fun getDurationDisplayFromMillis(millis: Int): String {
        val ss = (millis / 1000) % 60
        val mm = ((millis / (1000 * 60)) % 60)
        val hours = ((millis / (1000 * 60 * 60)) % 24)
        return if (hours != 0)
            String.format("%02d:%02d:%02d", hours, mm, ss)
        else
            String.format("%02d:%02d", mm, ss)
    }

    fun getDurationDisplayFromMSecond(mSecond: Int): String {
        val rear = mSecond % 10
        val second = mSecond / 10
        val realSecond = second % 60
        val minute = second / 60
        return if (minute < 10) {
            String.format("%02d:%02d.%logo", minute, realSecond, rear)
        } else String.format("%logo:%02d.%logo", minute, realSecond, rear)
    }

    fun convertNormalText(str: String): String {
        val nfdNormalizedString: String = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("").uppercase(Locale.getDefault())
            .trim()
    }

    fun dpFromPx(context: Context, px: Int): Int {
        return (px / context.resources.displayMetrics.density).toInt()
    }

    fun pxFromDp(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun getDurationMinSec(duration: Long): String {
        val min: Long = (duration / (1_000 * 60))
        val sec: Long = (duration / 1_000) % 60
        return String.format("%02d:%02d", min, sec)
    }

    fun getDurationMinSec(duration: Int): String {
        val min = (duration / (1000 * 60))
        val sec = (duration / 1000) % 60
        return String.format("%02d:%02d", min, sec)
    }

    fun convertingMillisecondsToHours(millis: Long): String {
        return String.format(
            Locale.US,
            "%logo min, %logo sec",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        )
    }

    fun addQuotes(s: String): String {
        return "\"" + s + "\""
    }

    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun bitmapToString(bitmap: Bitmap): String? {
        val bs = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bs)
        val b: ByteArray = bs.toByteArray()
        return encodeToString(b, DEFAULT)
    }

    fun stringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = decode(encodedString, DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }
}