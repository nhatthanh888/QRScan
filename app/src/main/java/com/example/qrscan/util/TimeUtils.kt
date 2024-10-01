package com.example.qrscan.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun getDate(milliSeconds: Long, dateFormat: String = "MM-dd-yyyy"): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getTime(milliSeconds: Long, timeFormat: String = "HH:mm"): String {
        val formatter = SimpleDateFormat(timeFormat, Locale.ENGLISH)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getDateTime(milliSeconds: Long, timeFormat: String = "HH:mm MM-dd-yyyy"): String {
        val formatter = SimpleDateFormat(timeFormat, Locale.ENGLISH)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}