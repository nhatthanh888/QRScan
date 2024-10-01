package com.example.qrscan

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.example.qrscan.data.database.AppDatabase
import com.example.qrscan.data.repository.MultipleScanRepository
import com.example.qrscan.data.repository.QRResultRepository
import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithAnyIgnoreCase
import com.example.qrscan.extension.unsafeLazy

class App : Application() {
    private var url: String = ""

    private val dataBase by lazy { AppDatabase.getDatabase(applicationContext) }
    val handleResultScanRepository by lazy { QRResultRepository(dataBase.qrDao()) }
    val handleMultipleScanRepository by lazy { MultipleScanRepository(dataBase.multipleScanDao()) }
    companion object {
        fun parse(text: String): App? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            instance().setUrl(text)
            return instance()
        }

        private val PREFIXES = listOf(
            "market://details?id=",
            "market://search",
            "http://play.google.com/",
            "https://play.google.com/"
        )

        private lateinit var app: App
        fun instance(): App {
            return this.app
        }

        fun appContext(): Context = instance().applicationContext
    }

    val appPackage by unsafeLazy {
        url.removePrefixIgnoreCase(PREFIXES[0])
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    fun setUrl(text: String) {
        url = text
    }

}