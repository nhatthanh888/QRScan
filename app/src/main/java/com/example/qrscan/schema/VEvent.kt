package com.example.qrscan.schema

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.qrscan.extension.appendIfNotNullOrBlank
import com.example.qrscan.extension.formatOrNull
import com.example.qrscan.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.example.qrscan.extension.parseOrNull
import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase
import com.example.qrscan.extension.unsafeLazy
import com.example.qrscan.util.HandleTypeResultScan
import java.text.SimpleDateFormat
import java.util.*

data class VEvent(
    var uid: String? = null,
    var stamp: String? = null,
    val organizer: String? = null,
    val description: String? = null,
    var location: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var startDateStr: String? = null,
    var endDateStr: String? = null,
    var summary: String? = null,
    val isAllDay: Boolean = false,
    val prodid: String? = null
) : Schema {

    companion object {
        const val SCHEMA_PREFIX = "BEGIN:VEVENT"
        private const val SCHEMA_PREFIX_SUB = "BEGIN:VCALENDAR"
        const val SCHEMA_SUFFIX = "END:VEVENT"
        private const val SCHEMA_SUFFIX_SUB = "END:VCALENDAR"
        private const val PARAMETERS_SEPARATOR_1 = "\n"
        private const val PARAMETERS_SEPARATOR_2 = "\r"
        private const val UID_PREFIX = "UID:"
        private const val PRODID_PREFIX = "PRODID:"
        private const val STAMP_PREFIX = "DTSTAMP:"
        private const val ORGANIZER_PREFIX = "ORGANIZER:"
        private const val DESCRIPTION_PREFIX = "DESCRIPTION:"
        private const val LOCATION_PREFIX = "LOCATION:"
        private const val START_PREFIX = "DTSTART:"
        private const val END_PREFIX = "DTEND:"
        private const val SUMMARY_PREFIX = "SUMMARY:"

        private val DATE_PARSERS by unsafeLazy {
            listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
                SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'"),
                SimpleDateFormat("yyyyMMdd'T'HHmmss"),
                SimpleDateFormat("yyyy-MM-dd"),
                SimpleDateFormat("yyyyMMdd")
            )
        }

        private val BARCODE_TEXT_DATE_FORMATTER by unsafeLazy {
            SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault()).apply {
            }
        }

        private val BARCODE_TEXT_DATE_FORMATTER_ALL_DAY by unsafeLazy {
            SimpleDateFormat("yyyyMMdd'T'", Locale.getDefault()).apply {
            }
        }

        private val FORMATTED_TEXT_DATE_FORMATTER by unsafeLazy {
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        }

        fun parse(text: String): VEvent? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not() && text.startsWithIgnoreCase(
                    SCHEMA_PREFIX_SUB
                ).not()
            ) {
                return null
            }

            var uid: String? = null
            var prodid: String? = null
            var stamp: String? = null
            var organizer: String? = null
            var description: String? = null
            var location: String? = null
            var startDate: Long? = null
            var endDate: Long? = null
            var summary: String? = null

            text.removePrefixIgnoreCase(SCHEMA_PREFIX)
                .split(PARAMETERS_SEPARATOR_1, PARAMETERS_SEPARATOR_2).forEach { part ->
                    if (part.startsWithIgnoreCase(UID_PREFIX)) {
                        uid = part.removePrefixIgnoreCase(UID_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(PRODID_PREFIX)) {
                        prodid = part.removePrefixIgnoreCase(PRODID_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(STAMP_PREFIX)) {
                        stamp = part.removePrefixIgnoreCase(STAMP_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(ORGANIZER_PREFIX)) {
                        organizer = part.removePrefixIgnoreCase(ORGANIZER_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(DESCRIPTION_PREFIX)) {
                        description = part.removePrefixIgnoreCase(DESCRIPTION_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(LOCATION_PREFIX)) {
                        location = part.removePrefixIgnoreCase(LOCATION_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(START_PREFIX)) {
                        val startDateOriginal = part.removePrefix(START_PREFIX)
                        startDate = DATE_PARSERS.parseOrNull(startDateOriginal)?.time
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(END_PREFIX)) {
                        val endDateOriginal = part.removePrefix(END_PREFIX)
                        endDate = DATE_PARSERS.parseOrNull(endDateOriginal)?.time
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(SUMMARY_PREFIX)) {
                        summary = part.removePrefixIgnoreCase(SUMMARY_PREFIX)
                        return@forEach
                    }
                }

            return VEvent(
                uid,
                stamp,
                organizer,
                description,
                location,
                startDate,
                endDate,
                summary,
                prodid = prodid
            )
        }
    }

    override val schema = BarcodeSchema.VEVENT

    @RequiresApi(Build.VERSION_CODES.O)
    override fun toFormattedText(): String {
        return listOf(
            prodid,
            uid,
            stamp,
            summary,
            description,
            location,
            startDateStr?.let { HandleTypeResultScan.convertToTime(it) },
            endDateStr?.let { HandleTypeResultScan.convertToTime(it) },
            organizer
        ).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        val startD: String?
        val endD: String?
        if (isAllDay) {
            startD = BARCODE_TEXT_DATE_FORMATTER_ALL_DAY.formatOrNull(startDate)
            endD = BARCODE_TEXT_DATE_FORMATTER_ALL_DAY.formatOrNull(endDate)
        } else {
            startD = BARCODE_TEXT_DATE_FORMATTER.formatOrNull(startDate)
            endD = BARCODE_TEXT_DATE_FORMATTER.formatOrNull(endDate)
        }
        val stringResult = if (prodid != null) {
            StringBuilder()
                .append(SCHEMA_PREFIX_SUB)
                .append(PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(PRODID_PREFIX, prodid, PARAMETERS_SEPARATOR_1)
                .append(SCHEMA_PREFIX)
                .append(PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(UID_PREFIX, uid, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(STAMP_PREFIX, stamp, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(ORGANIZER_PREFIX, organizer, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(DESCRIPTION_PREFIX, description, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(START_PREFIX, startD, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(END_PREFIX, endD, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(SUMMARY_PREFIX, summary, PARAMETERS_SEPARATOR_1)
                .append(SCHEMA_SUFFIX)
                .append(PARAMETERS_SEPARATOR_1)
                .append(SCHEMA_SUFFIX_SUB)
                .toString()
        } else {
            StringBuilder()
                .append(SCHEMA_PREFIX)
                .append(PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(UID_PREFIX, uid, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(STAMP_PREFIX, stamp, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(ORGANIZER_PREFIX, organizer, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(DESCRIPTION_PREFIX, description, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(START_PREFIX, startD, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(END_PREFIX, endD, PARAMETERS_SEPARATOR_1)
                .appendIfNotNullOrBlank(SUMMARY_PREFIX, summary, PARAMETERS_SEPARATOR_1)
                .append(SCHEMA_SUFFIX)
                .toString()
        }

        return stringResult
    }
}