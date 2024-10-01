package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

data class Twitter(
    val username: String? = null,
    val url: String? = null,
) : Schema {
    companion object {
        const val USERNAME_PREFIX = "twitter://user?screen_name="
        const val URL_PREFIX = "https://twitter.com/"
        fun parse(text: String): Twitter? {
            return when {
                text.startsWithIgnoreCase(USERNAME_PREFIX) -> parseAsUsername(text)
                text.startsWithIgnoreCase(URL_PREFIX) -> parseAsUrl(text)
                else -> null
            }
        }

        private fun parseAsUsername(text: String): Twitter {
            val username = text.removePrefixIgnoreCase(USERNAME_PREFIX)
            return Twitter(username = username)
        }

        private fun parseAsUrl(text: String): Twitter {
            val url = text.removePrefixIgnoreCase(URL_PREFIX)
            return Twitter(url = url)
        }
    }

    override val schema = BarcodeSchema.TWITTER
    override fun toFormattedText(): String {
        return if (url != null) {
            listOf(
                "$url"
            )
                .filter { it.isNotBlank() }
                .joinToString("\n")
        } else {
            listOf(
                "Twitter Username: $username"
            )
                .filter { it.isNotBlank() }
                .joinToString("\n")
        }
    }

    override fun toBarcodeText(): String {
        return when {
            username != null -> "$USERNAME_PREFIX$username"
            else -> "$url"
        }
    }
}
