package com.example.qrscan.analyzer

interface ScannerListener {
    fun onScanned(result: String)
    fun checkTypeCode(type: String)
}