package com.example.qrscan.extension

import android.net.Uri

fun Uri.isGoogleDrive(): Boolean {
    return if (this.authority != null)
        "com.google.android.apps.docs.storage".contains(this.authority!!) else false
}


fun Uri.Builder.appendQueryParameterIfNotNullOrBlank(key: String, value: String?): Uri.Builder {
    if (value.isNullOrBlank().not()) {
        appendQueryParameter(key, value)
    }
    return this
}