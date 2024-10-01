package com.example.qrscan.util

import android.os.Environment


object ExternalFileUtils {
    private const val QR_DIR = "/QrFile"

    val DIR_RELATIVE_PATH =
        Environment.DIRECTORY_PICTURES + QR_DIR

}