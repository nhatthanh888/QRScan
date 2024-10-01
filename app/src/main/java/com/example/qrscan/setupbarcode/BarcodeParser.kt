package com.example.qrscan.setupbarcode

import com.example.qrscan.App
import com.example.qrscan.data.model.BarcodeDb
import com.example.qrscan.schema.BarcodeSchema
import com.example.qrscan.schema.Bookmark
import com.example.qrscan.schema.Email
import com.example.qrscan.schema.Geo
import com.example.qrscan.schema.GoogleMaps
import com.example.qrscan.schema.MeCard
import com.example.qrscan.schema.Mms
import com.example.qrscan.schema.Other
import com.example.qrscan.schema.OtpAuth
import com.example.qrscan.schema.Phone
import com.example.qrscan.schema.Schema
import com.example.qrscan.schema.Sms
import com.example.qrscan.schema.Url
import com.example.qrscan.schema.VCard
import com.example.qrscan.schema.VEvent
import com.example.qrscan.schema.Wifi
import com.example.qrscan.schema.Youtube
import com.example.qrscan.util.TimeUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType

object BarcodeParser {
    val listProductFormat = listOf(
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
        BarcodeFormat.UPC_E,
        BarcodeFormat.UPC_A,
        BarcodeFormat.CODABAR,
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_128,
        BarcodeFormat.UPC_EAN_EXTENSION
    )

    fun parseResult(result: Result): BarcodeDb {
        val barcodeSchema = parseSchema(result.barcodeFormat, result.text)
        var schema = barcodeSchema.schema
        listProductFormat.forEach {
            if (it == result.barcodeFormat) {
                schema = BarcodeSchema.PRODUCT
            }
        }
        return BarcodeDb(
            text = result.text,
            formattedText = barcodeSchema.toFormattedText(),
            format = result.barcodeFormat,
            schema = schema,
            date = TimeUtils.getDate(result.timestamp),
            time = TimeUtils.getTime(result.timestamp),
            errorCorrectionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String,
            country = result.resultMetadata?.get(ResultMetadataType.POSSIBLE_COUNTRY) as? String
        )
    }

    fun parseSchema(format: BarcodeFormat, text: String): Schema {
        if (format != BarcodeFormat.QR_CODE) {
            return Other(text)
        }

        return (App.parse(text)
            ?: Youtube.parse(text)
            ?: GoogleMaps.parse(text)
            ?: Url.parse(text)
            ?: Phone.parse(text)
            ?: Geo.parse(text)
            ?: Bookmark.parse(text)
            ?: Sms.parse(text)
            ?: Mms.parse(text)
            ?: Wifi.parse(text)
            ?: Email.parse(text)
            ?: VEvent.parse(text)
            ?: MeCard.parse(text)
            ?: VCard.parse(text)
            ?: OtpAuth.parse(text)
            ?: Other(text)) as Schema
    }
    fun checkIsProductCode(format: BarcodeFormat): Boolean {
        listProductFormat.forEach {
            if (format == it) {
                return true
            }
        }
        return false
    }
}