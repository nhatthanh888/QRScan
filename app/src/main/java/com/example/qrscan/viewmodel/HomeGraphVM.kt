package com.example.qrscan.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.example.qrscan.R
import com.example.qrscan.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeGraphVM:BaseViewModel() {
    private val _backToGraphRootEvent = MutableSharedFlow<Unit>()
    val backToGraphRootEvent = _backToGraphRootEvent.asSharedFlow()

    fun submitBackToGraphRootEvent() {
        viewModelScope.launch {
            _backToGraphRootEvent.emit(Unit)
        }
    }

}