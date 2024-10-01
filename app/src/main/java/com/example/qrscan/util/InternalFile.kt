package com.example.qrscan.util

import android.content.Context
import com.example.qrscan.App
import java.io.File

object InternalFile {
    val filesDir = File(App.appContext().filesDir.absolutePath)
}

fun Context.internalFile(): File {
    return File(this.filesDir.absolutePath).apply {
        if (!exists()) mkdir()
    }
}

