package com.example.qrscan.extension

import android.provider.ContactsContract
import android.util.Patterns
import com.example.qrscan.data.model.QRCreateRequest
import com.example.qrscan.data.model.TypeQr
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

private val escapedRegex = """\\([\\;,":])""".toRegex()

fun String.normalText(): String {
    val nfdNormalizedString: String = Normalizer.normalize(this, Normalizer.Form.NFD)
    val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(nfdNormalizedString).replaceAll("").uppercase(Locale.getDefault())
        .trim()
}

fun String.initQRCreateRequest(): QRCreateRequest {
    return when (true) {
        Patterns.WEB_URL.matcher(this).matches() -> {
            QRCreateRequest(this, TypeQr.URL)
        }
        else -> {
            QRCreateRequest(this, TypeQr.TEXT)
        }
    }
}

fun StringBuilder.appendIfNotNullOrBlank(
    prefix: String = "",
    value: String?,
    suffix: String = ""
): StringBuilder {
    if (value.isNullOrBlank().not()) {
        append(prefix)
        append(value)
        append(suffix)
    }
    return this
}

fun List<String?>.joinToStringNotNullOrBlankWithLineSeparator(): String {
    return joinToStringNotNullOrBlank("\n")
}

fun List<String?>.joinToStringNotNullOrBlank(separator: String): String {
    return filter { it.isNullOrBlank().not() }.joinToString(separator)
}


fun String.removePrefixIgnoreCase(prefix: String): String {
    return substring(prefix.length)
}

fun String.startsWithIgnoreCase(prefix: String): Boolean {
    return startsWith(prefix, true)
}

fun String.startsWithAnyIgnoreCase(prefixes: List<String>): Boolean {
    prefixes.forEach { prefix ->
        if (startsWith(prefix, true)) {
            return true
        }
    }
    return false
}

fun String.equalsAnyIgnoreCase(others: List<String>): Boolean {
    others.forEach { other ->
        if (equals(other, true)) {
            return true
        }
    }
    return false
}

fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun String.removeStartAll(symbol: Char): String {
    var newStart = 0

    run loop@{
        forEachIndexed { index, c ->
            if (c == symbol) {
                newStart = index + 1
            } else {
                return@loop
            }
        }
    }

    return if (newStart >= length) {
        ""
    } else {
        substring(newStart)
    }
}


fun String.unescape(): String {
    return replace(escapedRegex) { escaped ->
        escaped.groupValues[1]
    }
}

fun String.toCaps(): String {
    return toUpperCase(Locale.ROOT)
}

fun String.toEmailType(): Int? {
    return when (toUpperCase(Locale.US)) {
        "HOME" -> ContactsContract.CommonDataKinds.Email.TYPE_HOME
        "WORK" -> ContactsContract.CommonDataKinds.Email.TYPE_WORK
        "OTHER" -> ContactsContract.CommonDataKinds.Email.TYPE_OTHER
        "MOBILE" -> ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
        else -> null
    }
}

fun String.toPhoneType(): Int? {
    return when (toUpperCase(Locale.US)) {
        "HOME" -> ContactsContract.CommonDataKinds.Phone.TYPE_HOME
        "MOBILE", "CELL" -> ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
        "WORK" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK
        "FAX_WORK" -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK
        "FAX_HOME" -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME
        "PAGER" -> ContactsContract.CommonDataKinds.Phone.TYPE_PAGER
        "OTHER" -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
        "CALLBACK" -> ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK
        "CAR" -> ContactsContract.CommonDataKinds.Phone.TYPE_CAR
        "COMPANY_MAIN" -> ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN
        "ISDN" -> ContactsContract.CommonDataKinds.Phone.TYPE_ISDN
        "MAIN" -> ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
        "OTHER_FAX" -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX
        "RADIO" -> ContactsContract.CommonDataKinds.Phone.TYPE_RADIO
        "TELEX" -> ContactsContract.CommonDataKinds.Phone.TYPE_TELEX
        "TTY_TDD" -> ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD
        "WORK_MOBILE" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE
        "WORK_PAGER" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER
        "ASSISTANT" -> ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT
        "MMS" -> ContactsContract.CommonDataKinds.Phone.TYPE_MMS
        else -> null
    }
}

fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date =
    SimpleDateFormat(format, locale).parse(this)
