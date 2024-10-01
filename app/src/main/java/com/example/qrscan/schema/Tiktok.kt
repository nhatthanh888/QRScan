package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

data class Tiktok(
    val id: String? = null,
    val url: String? = null,
) : Schema {
    companion object {
        const val ID_PREFIX = "https://www.tiktok.com/search?q="
        const val URL_PREFIX = "https://www.tiktok.com/"
        fun parse(text: String): Tiktok? {
            return when {
                text.startsWithIgnoreCase(ID_PREFIX) -> parseAsUsername(text)
                text.startsWithIgnoreCase(URL_PREFIX) -> parseAsUrl(text)
                else -> null
            }
        }

        private fun parseAsUsername(text: String): Tiktok {
            val username = text.removePrefixIgnoreCase(ID_PREFIX)
            return Tiktok(id = username)
        }

        private fun parseAsUrl(text: String): Tiktok {
            val url = text.removePrefixIgnoreCase(URL_PREFIX)
            return Tiktok(url = url)
        }
    }

    override val schema = BarcodeSchema.TIKTOK
    override fun toFormattedText(): String {
        return if (id != null) {
            listOf(
                "ID: $id"
            )
                .filter { it.isNotBlank() }
                .joinToString("\n")
        } else {
            listOf(
                "$url"
            )
                .filter { it.isNotBlank() }
                .joinToString("\n")
        }
    }

    override fun toBarcodeText(): String {
        return when {
            id != null -> "$ID_PREFIX$id"
            else -> "$url"
        }
    }
}
