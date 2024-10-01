package com.example.qrscan.ui.fragment.scan

import com.example.qrscan.data.model.MultipleScanModel

interface ListenerItemDelete {
    fun listenUpdateItem(id: Int, check: Boolean)
}