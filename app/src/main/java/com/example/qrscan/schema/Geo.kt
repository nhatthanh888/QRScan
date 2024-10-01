package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase


class Geo : Schema {

    companion object {
         const val PREFIX = "geo:"
        private const val SEPARATOR = ","

        fun parse(text: String): Geo? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }
            return Geo(text)
        }
    }

    private val uri: String

    private constructor(uri: String) {
        this.uri = uri
    }

    constructor(name: String, latitude: String, longitude: String, altitude: String? = null) {
        uri = if (altitude.isNullOrEmpty()) {
            "$PREFIX$name$SEPARATOR$latitude$SEPARATOR$longitude"
        } else {
            "$PREFIX$name$SEPARATOR$latitude$SEPARATOR$longitude$SEPARATOR$altitude"
        }
    }

    override val schema = BarcodeSchema.GEO

    override fun toBarcodeText(): String = uri

    override fun toFormattedText(): String {
        return uri.removePrefixIgnoreCase(PREFIX).replace(SEPARATOR, "\n")
    }
}