package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

data class Barcode(
    val barcode: String? = null,
) : Schema {
    companion object {
         const val BARCODE = "Barcode"
        fun parse(text: String): Barcode? {
            return when {
                text.startsWithIgnoreCase(BARCODE) -> parseAsUsername(text)
                else -> null
            }
        }

        private fun parseAsUsername(text: String): Barcode {
            val username = text.removePrefixIgnoreCase(BARCODE)
            return Barcode(barcode = username)
        }
    }

    override val schema = BarcodeSchema.TIKTOK
    override fun toFormattedText(): String {
        return listOf(
            "$barcode"
        )
            .filter { it.isNotBlank() }
            .joinToString("\n")
    }

    override fun toBarcodeText(): String {
        return when {
            barcode != null -> "$BARCODE$barcode"
            else -> ""
        }
    }
}
