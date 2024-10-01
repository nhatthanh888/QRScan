package com.example.qrscan.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.qrscan.R
import com.example.qrscan.base.BaseViewModel
import com.example.qrscan.data.model.MultipleScanModel
import com.example.qrscan.data.model.QRType
import com.example.qrscan.ui.fragment.scan.FragmentScan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ShareDataVM : BaseViewModel() {
    var permissionStorage = MutableLiveData<Boolean>()
    var permissionSaveImage = MutableLiveData<Boolean>()
    var permissionCamera = MutableLiveData<Boolean>()
    var permissionLocation = MutableLiveData<Boolean>()
    var barCodeActive = MutableLiveData<Boolean>()
    var multipleScanActive = MutableLiveData<Boolean>()
    var isFAQsStatus = MutableLiveData<Boolean>()
    var isFAQsStatus2 = MutableLiveData<Boolean>()
    var isFAQsStatus3 = MutableLiveData<Boolean>()
    var isFAQsStatus4 = MutableLiveData<Boolean>()
    var isFavorite = MutableLiveData<Boolean>()


    fun setFavorite(active: Boolean) {
        isFavorite.value = active
    }


    fun setFAQsStatus(status: Boolean) {
        isFAQsStatus.value = status
    }
    fun setFAQsStatus2(status: Boolean) {
        isFAQsStatus2.value = status
    }
    fun setFAQsStatus3(status: Boolean) {
        isFAQsStatus3.value = status
    }
    fun setFAQsStatus4(status: Boolean) {
        isFAQsStatus4.value = status
    }

    fun setMultipleScanActive(active: Boolean) {
        multipleScanActive.value = active
    }

    fun setBarcodeActive(active: Boolean) {
        barCodeActive.value = active
    }


    fun setPermissionStorage(isPermission: Boolean) {
        permissionStorage.value = isPermission
    }

    fun setPermissionCamera(isPermission: Boolean) {
        permissionCamera.value = isPermission
    }

    fun setPermissionLocation(isPermission: Boolean) {
        permissionLocation.value = isPermission
    }

    fun setPermissionSaveImage(isPermission: Boolean) {
        permissionSaveImage.value = isPermission
    }

    fun clearVM() {
        barCodeActive.value = false
        multipleScanActive.value = false
        isFAQsStatus.value = false
        isFAQsStatus2.value = false
        isFAQsStatus3.value = false
        isFAQsStatus4.value = false
    }
}