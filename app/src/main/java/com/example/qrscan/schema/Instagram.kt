package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

data class Instagram(
    val username: String? = null,
    val url: String? = null,
) : Schema {

    companion object {
        const val USERNAME_PREFIX = "instagram://user?username="
        const val URL_PREFIX = "https://www.instagram.com/"

        fun parse(text: String): Instagram? {
            return when {
                text.startsWithIgnoreCase(USERNAME_PREFIX) -> parseAsUsername(text)
                text.startsWithIgnoreCase(URL_PREFIX) -> parseAsUrl(text)
                else -> null
            }
        }

        private fun parseAsUsername(text: String): Instagram {
            val username = text.removePrefixIgnoreCase(USERNAME_PREFIX)
            return Instagram(username = username)
        }

        private fun parseAsUrl(text: String): Instagram {
            val url = text.removePrefixIgnoreCase(URL_PREFIX)
            return Instagram(url = url)
        }

    }

    override val schema = BarcodeSchema.INSTAGRAM

    override fun toFormattedText(): String {
        return if (username != null) {
            listOf(
                "Instagram Username: $username"

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
            return if (url != null) {
                "$url"
            } else {
                "${USERNAME_PREFIX}$username"
            }
    }
}
