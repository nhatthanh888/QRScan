package com.example.qrscan.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.qrscan.App
import com.example.qrscan.barcode.ParsedBarcode
import com.example.qrscan.base.BaseViewModel
import com.example.qrscan.data.model.BarcodeDb
import com.example.qrscan.setupbarcode.BarcodeImageGenerator
import com.example.qrscan.util.FileUtils
import com.example.qrscan.util.StringUtils
import com.example.qrscan.util.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class QrDetailVM : BaseViewModel() {
    lateinit var barcodeDbData: BarcodeDb
    var fileQr: File? = null
    var bitmapShareQr: Bitmap? = null

    var parseBarcodeLiveData = MutableLiveData<ParsedBarcode>()
    var bitmapIconQrLiveData = MutableLiveData<Bitmap>()
    var bitmapViewQrLiveData = MutableLiveData<String>()
    var favoriteLiveData = MutableLiveData<Boolean>()

    var textQr = ""
    var url = ""
    var mail = ""
    var messageQr = ""
    var job = ""
    var name = ""
    var country = ""
    var organization = ""
    var firstName = ""
    var lastName = ""
    var address = ""
    var locationAdress = ""
    var phone = ""
    var networkName = ""
    var networkPassword = ""
    var networkAuthType = ""
    var latitude = ""
    var longitude = ""
    var eventUid = ""
    var eventProdid = ""
    var eventLocation = ""
    var eventDescription = ""
    var eventOrganizer = ""
    var eventStartDate = ""
    var eventEndDate = ""
    var eventSummary = ""
    var format = ""

    fun prepareData() {
        parseBarcodeLiveData.value = ParsedBarcode(barcodeDbData)
        parseBarcodeLiveData.value?.let {
            textQr = it.formattedText
            url = it.url ?: ""
            mail = it.email ?: ""
            job = it.jobTitle ?: ""
            messageQr = it.smsBody ?: ""
            name = it.name ?: ""
            organization = it.organization ?: ""
            address = it.address ?: ""
            locationAdress = it.geoUri ?: ""
            phone = it.phone ?: ""
            url = it.url ?: ""
            firstName = it.firstName ?: ""
            lastName = it.lastName ?: ""
            networkName = it.networkName ?: ""
            networkPassword = it.networkPassword ?: ""
            networkAuthType = it.networkAuthType ?: ""
            latitude = (it.latitude ?: "").toString()
            longitude = (it.longitude ?: "").toString()
            country = it.country ?: ""
            eventUid = it.eventUid ?: ""
            eventProdid = it.eventProdid ?: ""
            it.eventStamp?.let { location ->
                eventLocation = location ?: ""
            } ?: kotlin.run {
                eventLocation = it.eventLocation ?: ""
            }
            eventDescription = it.eventDescription ?: ""
            eventOrganizer = it.eventOrganizer ?: ""
            it.eventStartDate?.let { startDate ->
                eventStartDate =
                    TimeUtils.getDate(startDate) + "   " + TimeUtils.getTime(startDate)
            }
            it.eventEndDate?.let { endDate ->
                eventEndDate =
                    TimeUtils.getDate(endDate) + "   " + TimeUtils.getTime(endDate)
            }
            eventSummary = it.eventSummary ?: ""
            format = it.format.name
        }

        CoroutineScope(Dispatchers.IO).launch {
            bitmapIconQrLiveData.postValue(
                BarcodeImageGenerator.generateBitmap(
                    barcodeDbData,
                    500,
                    500,
                    2,
                    false
                )
            )

            val bitmap = BarcodeImageGenerator.generateBitmap(
                barcodeDbData,
                800,
                800,
                2
            )

            bitmap?.let {
                bitmapViewQrLiveData.postValue(StringUtils.bitmapToString(bitmap))

                bitmapShareQr = bitmap
                fileQr = FileUtils.saveBitmapToInternal(bitmap)
            }
        }
    }

//    fun setFavoriteCode() = viewModelScope.launch {
//        favoriteLiveData.value = !favoriteLiveData.value!!
//        withContext(Dispatchers.IO) {
//            App.instance().database.barcodeDao().updateFavorite(
//                barcodeDbData.id,
//                favoriteLiveData.value!!
//            )
//        }
//    }
}