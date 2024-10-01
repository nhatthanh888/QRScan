package com.example.qrscan.schema

import com.example.qrscan.extension.startsWithAnyIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

class Url(val url: String) : Schema {

    companion object {
         const val HTTP_PREFIX = "http://"
         const val HTTPS_PREFIX = "https://"
         const val WWW_PREFIX = "www."
        private val PREFIXES = listOf(HTTP_PREFIX, HTTPS_PREFIX, WWW_PREFIX)

        fun parse(text: String): Url? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }

            val url = when {
                text.startsWithIgnoreCase(WWW_PREFIX) -> "$HTTP_PREFIX$text"
                else -> text
            }

            return Url(url)
        }
    }

    override val schema = BarcodeSchema.URL
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}