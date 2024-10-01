package com.example.qrscan.schema

import com.example.qrscan.schema.BarcodeSchema
import com.example.qrscan.schema.Schema

class Other(val text: String) : Schema {
    override val schema = BarcodeSchema.OTHER
    override fun toFormattedText(): String = text
    override fun toBarcodeText(): String = text
}