package com.example.qrscan.barcode

import com.example.qrscan.App
import com.example.qrscan.data.model.BarcodeDb
import com.example.qrscan.schema.BarcodeSchema
import com.example.qrscan.schema.Bookmark
import com.example.qrscan.schema.Email
import com.example.qrscan.schema.MeCard
import com.example.qrscan.schema.Phone
import com.example.qrscan.schema.Sms
import com.example.qrscan.schema.VCard
import com.example.qrscan.schema.VEvent
import com.example.qrscan.schema.Wifi
import com.google.zxing.BarcodeFormat

@Suppress("KotlinConstantConditions")
class ParsedBarcode(barcodeDb: BarcodeDb) {
    var id = barcodeDb.id
    var name = barcodeDb.name
    val text = barcodeDb.text
    val formattedText = barcodeDb.formattedText
    val format = barcodeDb.format
    val schema = barcodeDb.schema
    val date = barcodeDb.date
    var isFavorite = barcodeDb.isFavorite
    val country = barcodeDb.country

    var firstName: String? = null
    var lastName: String? = null
    var organization: String? = null
    var jobTitle: String? = null
    var address: String? = null

    var email: String? = null

    var note: String? = null

    var phone: String? = null

    var smsBody: String? = null

    var networkAuthType: String? = null
    var networkName: String? = null
    var networkPassword: String? = null
    var isHidden: Boolean? = null
    var anonymousIdentity: String? = null
    var identity: String? = null
    var eapMethod: String? = null
    var phase2Method: String? = null

    var bookmarkTitle: String? = null
    var url: String? = null
    var youtubeUrl: String? = null
    var bitcoinUri: String? = null
    var otpUrl: String? = null
    var geoUri: String? = null
    var longitude: Double? = null
    var latitude: Double? = null

    var eventUid: String? = null
    var eventProdid: String? = null
    var eventStamp: String? = null
    var eventOrganizer: String? = null
    var eventDescription: String? = null
    var eventLocation: String? = null
    var eventSummary: String? = null
    var eventStartDate: Long? = null
    var eventEndDate: Long? = null

    var appMarketUrl: String? = null
    var appPackage: String? = null

    val isInDb: Boolean
        get() = id != 0L

    val isProductBarcode: Boolean
        get() = when (format) {
            BarcodeFormat.EAN_8, BarcodeFormat.EAN_13, BarcodeFormat.UPC_A, BarcodeFormat.UPC_E -> true
            else -> false
        }

    init {
        when (schema) {
            BarcodeSchema.BOOKMARK -> parseBookmark()
            BarcodeSchema.EMAIL -> parseEmail()
            BarcodeSchema.GEO,
            BarcodeSchema.GOOGLE_MAPS -> parseGeoInfo()

            BarcodeSchema.APP -> parseApp()
            BarcodeSchema.VEVENT -> parseCalendar()
            BarcodeSchema.MMS,
            BarcodeSchema.SMS -> parseSms()

            BarcodeSchema.MECARD -> parseMeCard()
            BarcodeSchema.PHONE -> parsePhone()
            BarcodeSchema.VCARD -> parseVCard()
            BarcodeSchema.WIFI -> parseWifi()
            BarcodeSchema.YOUTUBE -> parseYoutube()
            BarcodeSchema.CRYPTOCURRENCY -> parseBitcoin()
            BarcodeSchema.OTP_AUTH -> parseOtp()
            BarcodeSchema.URL -> parseUrl()
            else -> {}
        }
    }

    private fun parseBookmark() {
        val bookmark = Bookmark.parse(text) ?: return
        bookmarkTitle = bookmark.title
        url = bookmark.url
    }

    private fun parseEmail() {
        val email = Email.parse(text) ?: return
        this.email = email.email
    }

    private fun parseGeoInfo() {
        geoUri = text
        if (text.startsWith("geo:")) {
            try {
                val array = text.drop(4).split(",")
                latitude = array[0].toDouble()
                if (array[1].contains("?q")) {
                    longitude = array[1].split("?q")[0].toDouble()
                } else {
                    longitude = array[1].toDouble()
                }
            } catch (e: Exception) {

            }
        } else {
            try {
                val array = formattedText.split("\n")
                latitude = array[0].toDouble()
                longitude = array[1].toDouble()
            } catch (e: Exception) {

            }
        }
    }

    private fun parseApp() {
        appMarketUrl = text
        appPackage = App.parse(text)?.appPackage
    }

    private fun parseCalendar() {
        val calendar = VEvent.parse(text) ?: return
        eventUid = calendar.uid
        eventProdid = calendar.prodid
        eventStamp = calendar.stamp
        eventOrganizer = calendar.organizer
        eventDescription = calendar.description
        eventLocation = calendar.location
        eventSummary = calendar.summary
        eventStartDate = calendar.startDate
        eventEndDate = calendar.endDate
    }

    private fun parseSms() {
        val sms = Sms.parse(text) ?: return
        phone = sms.phone
        smsBody = sms.message
    }

    private fun parsePhone() {
        phone = Phone.parse(text)?.phone
    }

    private fun parseMeCard() {
        val meCard = MeCard.parse(text) ?: return
        firstName = meCard.firstName
        lastName = meCard.lastName
        address = meCard.address
        phone = meCard.phone
        email = meCard.email
        note = meCard.note
    }

    private fun parseVCard() {
        val vCard = VCard.parse(text) ?: return

        firstName = vCard.firstName
        url = vCard.url

        phone = vCard.phone

        email = vCard.email
    }

    private fun parseWifi() {
        val wifi = Wifi.parse(text) ?: return
        networkAuthType = wifi.encryption
        networkName = wifi.name
        networkPassword = wifi.password
        isHidden = wifi.isHidden
        anonymousIdentity = wifi.anonymousIdentity
        identity = wifi.identity
        eapMethod = wifi.eapMethod
        phase2Method = wifi.phase2Method
    }

    private fun parseYoutube() {
        youtubeUrl = text
    }

    private fun parseBitcoin() {
        bitcoinUri = text
    }

    private fun parseOtp() {
        otpUrl = text
    }

    private fun parseUrl() {
        url = text
    }
}