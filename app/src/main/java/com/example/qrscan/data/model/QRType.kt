@file:Suppress("DEPRECATED_ANNOTATION")

package com.example.qrscan.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
data class QRType(
    val checked: Drawable,
    val qrType: String,
    val premium: Drawable,
    val basic: Drawable
)