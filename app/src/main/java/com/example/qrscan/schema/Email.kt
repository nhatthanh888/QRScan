package com.example.qrscan.schema

import android.net.MailTo
import com.example.qrscan.extension.appendIfNotNullOrBlank
import com.example.qrscan.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

data class Email(
    val email: String? = null,
) : Schema {

    companion object {
         const val MATMSG_SCHEMA_PREFIX = "MATMSG:"
         const val MATMSG_EMAIL_PREFIX = "TO:"
         const val MATMSG_SEPARATOR = ";"

         const val MAILTO_SCHEMA_PREFIX = "mailto:"

        fun parse(text: String): Email? {
            return when {
                text.startsWithIgnoreCase(MATMSG_SCHEMA_PREFIX) -> parseAsMatmsg(text)
                text.startsWithIgnoreCase(MAILTO_SCHEMA_PREFIX) -> parseAsMailTo(text)
                else -> null
            }
        }

        private fun parseAsMatmsg(text: String): Email {
            var email: String? = null

            text.removePrefixIgnoreCase(MATMSG_SCHEMA_PREFIX).split(MATMSG_SEPARATOR)
                .forEach { part ->
                    if (part.startsWithIgnoreCase(MATMSG_EMAIL_PREFIX)) {
                        email = part.removePrefixIgnoreCase(MATMSG_EMAIL_PREFIX)
                        return@forEach
                    }
                }

            return Email(email)
        }

        private fun parseAsMailTo(text: String): Email? {
            return try {
                val mailto = MailTo.parse(text)
                Email(mailto.to)
            } catch (ex: Exception) {
                null
            }
        }
    }

    override val schema = BarcodeSchema.EMAIL

    override fun toFormattedText(): String {
        return listOf(email).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return StringBuilder()
            .append(MATMSG_SCHEMA_PREFIX)
            .appendIfNotNullOrBlank(MATMSG_EMAIL_PREFIX, email, MATMSG_SEPARATOR)
            .append(MATMSG_SEPARATOR)
            .toString()
    }
}