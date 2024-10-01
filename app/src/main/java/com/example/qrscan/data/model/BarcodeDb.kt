package com.example.qrscan.data.model

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Parcelable
import com.example.qrscan.schema.BarcodeSchema
import com.example.qrscan.util.TimeUtils
import com.google.firebase.encoders.annotations.Encodable.Ignore
import com.google.zxing.BarcodeFormat
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.lang.ref.WeakReference

@Parcelize
data class BarcodeDb(
    val id: Long = 0,
    val text: String,
    val formattedText: String,
    val format: BarcodeFormat,
    val schema: BarcodeSchema,
    val date: String = TimeUtils.getDate(System.currentTimeMillis()),
    val time: String = TimeUtils.getTime(System.currentTimeMillis()),
    val errorCorrectionLevel: String? = null,
    val country: String? = null,
    val name: String? = null,
    val isGenerated: Boolean = false,
    val isFavorite: Boolean = false,
    var checkDelete: Boolean = false,
    var color: Int = Color.BLACK,
    var colorTop: Int = Color.BLACK,
    var colorBottom: Int = Color.BLACK,
    var colorBackground: Int = Color.WHITE,
    var backgroundTemplate: String? = null,
    var logo: String? = null,
    var isGradient: Boolean = false,
) : Parcelable {

    @IgnoredOnParcel
    var bitmapWr: WeakReference<Bitmap> = WeakReference(null)
}