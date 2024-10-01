package com.example.qrscan.base

import android.os.Bundle

interface navigator {
    fun showActivity(activity: Class<*>, bundle: Bundle? = null)
}