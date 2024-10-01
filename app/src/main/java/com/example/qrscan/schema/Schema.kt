package com.example.qrscan.schema

enum class BarcodeSchema {
    APP,
    BOOKMARK,
    CRYPTOCURRENCY,
    EMAIL,
    GEO,
    GOOGLE_MAPS,
    MMS,
    MECARD,
    OTP_AUTH,
    PHONE,
    SMS,
    URL,
    VEVENT,
    VCARD,
    WIFI,
    YOUTUBE,
    PRODUCT,
    FACEBOOK,
    INSTAGRAM,
    TWITTER,
    TIKTOK,
    WHATSAPP,
    OTHER;
}

interface Schema {
    val schema: BarcodeSchema
    fun toFormattedText(): String
    fun toBarcodeText(): String
}