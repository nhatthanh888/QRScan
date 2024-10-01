package com.example.qrscan.util

import android.os.SystemClock
import android.view.View

class SafeOnClickListener : View.OnClickListener {
    private var lastClickTime = 0L
    var onSafeClick: ((View?) -> Unit)? = null
    override fun onClick(view: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        onSafeClick?.invoke(view)
    }
}