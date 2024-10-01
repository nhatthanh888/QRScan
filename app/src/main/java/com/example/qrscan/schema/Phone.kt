package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

class Phone(val phone: String) : Schema {

    companion object {
        const val PREFIX = "tel:"

        fun parse(text: String): Phone? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }

            val phone = text.removePrefixIgnoreCase(PREFIX)
            return Phone(phone)
        }
    }

    override val schema = BarcodeSchema.PHONE
    override fun toFormattedText(): String = phone
    override fun toBarcodeText(): String = "$PREFIX$phone"
}