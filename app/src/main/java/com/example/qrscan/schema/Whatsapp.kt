package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

data class Whatsapp(
    val phone: String? = null,
) : Schema {
    companion object {
         const val PHONE_PREFIX = "whatsapp://send?phone="
        fun parse(text: String): Whatsapp? {
            return when {
                text.startsWithIgnoreCase(PHONE_PREFIX) -> parseAsUsername(text)
                else -> null
            }
        }

        private fun parseAsUsername(text: String): Whatsapp {
            val username = text.removePrefixIgnoreCase(PHONE_PREFIX)
            return Whatsapp(phone = username)
        }
    }

    override val schema = BarcodeSchema.WHATSAPP
    override fun toFormattedText(): String {
        return listOf(
            "URL: $phone"
        )
            .filter { it.isNotBlank() }
            .joinToString("\n")
    }

    override fun toBarcodeText(): String {
        return when {
            phone != null -> "$PHONE_PREFIX$phone"
            else -> ""
        }
    }
}
