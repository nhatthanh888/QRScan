package com.example.qrscan.ui.activity.create

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrscan.App
import com.example.qrscan.Bridge
import com.example.qrscan.R
import com.example.qrscan.adapter.QrResultDetailAdapter
import com.example.qrscan.base.BaseActivity
import com.example.qrscan.data.model.QrDetailItem
import com.example.qrscan.data.model.ResultScanModel
import com.example.qrscan.databinding.ActivityQrResultBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.schema.Barcode
import com.example.qrscan.schema.Email
import com.example.qrscan.schema.Facebook
import com.example.qrscan.schema.Geo
import com.example.qrscan.schema.Instagram
import com.example.qrscan.schema.Phone
import com.example.qrscan.schema.Tiktok
import com.example.qrscan.schema.Twitter
import com.example.qrscan.schema.Url
import com.example.qrscan.schema.VCard
import com.example.qrscan.schema.Whatsapp
import com.example.qrscan.schema.Wifi
import com.example.qrscan.schema.Youtube
import com.example.qrscan.util.ActionButtonQrResult
import com.example.qrscan.util.ActionButtonQrResult.addToContacts
import com.example.qrscan.util.ActionButtonQrResult.addToEvent
import com.example.qrscan.util.ActionButtonQrResult.dialPhoneNumber
import com.example.qrscan.util.ActionButtonQrResult.goToWifiSetting
import com.example.qrscan.util.ActionButtonQrResult.openWebsite
import com.example.qrscan.util.ActionButtonQrResult.sendEmail
import com.example.qrscan.util.FileUtils
import com.example.qrscan.util.GrantPermission
import com.example.qrscan.util.PermissionsHelper
import com.example.qrscan.util.TypeResult
import com.example.qrscan.viewmodel.HandleResultFactory
import com.example.qrscan.viewmodel.HandleResultVM
import com.example.qrscan.viewmodel.ShareDataVM
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.example.qrscan.schema.VEvent
import com.example.qrscan.ui.fragment.create.facebook.FragmentInputFacebook
import com.example.qrscan.ui.fragment.create.instagram.FragmentInputInstagram
import com.example.qrscan.ui.fragment.create.tiktok.FragmentInputTiktok
import com.example.qrscan.ui.fragment.create.twitter.FragmentInputTwitter
import com.example.qrscan.ui.fragment.scan.FragmentScan
import com.example.qrscan.ui.fragment.setting.SettingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


@Suppress("NAME_SHADOWING")
class QrResultActivity : BaseActivity<ActivityQrResultBinding>() {
    var bundle: Bundle? = null
    private var timeResult = ""
    var typeResult = ""
    private var isFavorite = false
    private var searchEngine = ""
    private val sharedPreferences by lazy {
        getSharedPreferences(FragmentScan.CONFIG_APP, Context.MODE_PRIVATE)
    }
    private lateinit var listQrDetail: ArrayList<QrDetailItem>
    private val scanVM: ShareDataVM by viewModels()
    private val saveImagePermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val handleResultVM: HandleResultVM by viewModels {
        HandleResultFactory((application as App).handleResultScanRepository)
    }

    override fun getContentView(): Int = R.layout.activity_qr_result
    private val laucher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val colorQr = it.data?.getIntExtra("QR_COLOR", 0)
                val colorBackground = it.data?.getIntExtra("QR_BACKGROUND", 0)
                val strRequest = it.data?.getStringExtra("STR_QR")
                val typeCode = it.data?.getStringExtra("typeCodeCustom")
                if (typeCode == BARCODE) {
                    generateBarcode(strRequest.toString(), colorQr!!, colorBackground!!)
                } else {
                    generateQrCode(strRequest.toString(), colorQr!!, colorBackground!!)
                }

            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun initView() {
        Bridge.getInstance().setCurrentActivity(this)
        if (sharedPreferences.getString(
                SettingFragment.SAVE_SEARCH_ENGINE,
                "http://www.google.com/search?q="
            ) != null
        ) {
            searchEngine = sharedPreferences.getString(
                SettingFragment.SAVE_SEARCH_ENGINE,
                "http://www.google.com/search?q="
            )!!
        }
        bundle = intent.extras
        timeResult = bundle?.getString("time").toString()
        typeResult = bundle?.getString("TYPE").toString()
        listQrDetail = ArrayList()
        mDataBinding.tvResultTime.text = timeResult
        mDataBinding.rcvQrDetail.layoutManager = LinearLayoutManager(this)
        when (typeResult) {
            TypeResult.WEB -> {
                updateResultTypeWeb()
            }

            TypeResult.PHONE -> {
                updateResultTypePhone()
            }

            TypeResult.EMAIL -> {
                updateResultTypeEmail()
            }

            TypeResult.TEXT -> {
                updateResultTypeText()
            }

            TypeResult.CONTACT -> {
                updateResultTypeContact()
            }

            TypeResult.WIFI -> {
                updateResultTypeWifi()
            }

            TypeResult.CALENDAR -> {
                updateCalendarType()
            }

            TypeResult.LOCATION -> {
                updateResultTypeLocation()
            }

            TypeResult.BARCODE -> {
                updateResultTypeBarcode()
            }

            TypeResult.FB -> {
                setUpFB()
            }

            TypeResult.IG -> {
                setUpIG()
            }

            TypeResult.YOUTUBE -> {
                setUpYoutube()
            }

            TypeResult.TIKTOK -> {
                setUpTiktok()
            }

            TypeResult.TWITTER -> {
                setUpTwitter()
            }

            TypeResult.WHATSAPP -> {
                setUpWhatsapp()
            }
        }
        mDataBinding.apply {
            ivBack.setOnSingleClickListener { finish() }
            btnSave.setOnSingleClickListener {
                saveToGallery()
            }
            observerFavorite()
            ivStar.setOnSingleClickListener {
                isFavorite = if (isFavorite) {
                    scanVM.setFavorite(false)
                    handleResultVM.listHistory.observe(this@QrResultActivity) {
                        updateItem(it[0].id, false)
                    }
                    false
                } else {
                    scanVM.setFavorite(true)
                    handleResultVM.listHistory.observe(this@QrResultActivity) {
                        updateItem(it[0].id, true)
                    }
                    true
                }
            }
        }
    }

    private fun observerFavorite() {
        mDataBinding.apply {
            scanVM.isFavorite.observe(this@QrResultActivity) {
                if (it == true) {
                    ivStar.setImageResource(R.drawable.favorited)
                } else {
                    ivStar.setImageResource(R.drawable.star_circle)
                }
            }
        }
    }

    private fun updateItem(id: Int, favorite: Boolean) {
        handleResultVM.updateResultScan(id, favorite)
    }

    private fun shareQR(result: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, result)
        startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun saveToGallery() {
        if (!PermissionsHelper.areAllPermissionsGranted(
                this,
                saveImagePermissions
            )
        ) {
            requestPermissionSaveImage()
        } else {
            val bitmapDrawable = mDataBinding.imvQrResult.drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap
            CoroutineScope(Dispatchers.IO).launch {
                val fileQr = FileUtils.saveImage(bitmap, "QRScan_${System.currentTimeMillis()}")
                withContext(Dispatchers.Main) {
                    fileQr?.let {
                        Toast.makeText(this@QrResultActivity, "Saved", Toast.LENGTH_SHORT).show()
                    } ?: kotlin.run {
                        Toast.makeText(this@QrResultActivity, "Save error", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun requestPermissionSaveImage() {
        GrantPermission.buildPermissionSaveImage().checkPermission(
            checkPermissionListener = { case ->
                when (case) {
                    GrantPermission.CASE_ALL_PERMISSION_GRANTED -> {
                        scanVM.setPermissionSaveImage(true)
                    }

                    GrantPermission.CASE_ALL_PERMISSION_DENIED -> {

                    }

                    GrantPermission.CASE_ALL_PERMISSION_DENIED_WITH_QUESTION -> {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri =
                            Uri.fromParts(
                                "package",
                                packageName,
                                null
                            )
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            })
    }

    private fun updateResultTypeLocation() {
        mDataBinding.apply {
            imvResultType.setImageResource(R.drawable.bg_location_result_type)
            tvTitleToolbar.text = this@QrResultActivity.getString(R.string.Location)
            val nameLocation = bundle?.getString("nameLocation")
            val latitude = bundle?.getString("latitude")
            val longtitude = bundle?.getString("longtitude")
            listQrDetail.add(
                QrDetailItem(
                    this@QrResultActivity.getString(R.string.Location_Name),
                    nameLocation.toString()
                )
            )
            listQrDetail.add(
                QrDetailItem(
                    this@QrResultActivity.getString(R.string.latitude),
                    latitude.toString()
                )
            )
            listQrDetail.add(
                QrDetailItem(
                    this@QrResultActivity.getString(R.string.longtitude),
                    longtitude.toString()
                )
            )
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            val geo = Geo(
                name = nameLocation.toString(),
                latitude = latitude.toString(),
                longitude = longtitude.toString()
            )
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = geo.toBarcodeText(),
                    created = true
                )
            )
            generateQrCode(geo.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(geo.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener { startActivityCustom(geo.toBarcodeText(), QR) }
            btnShare.setOnSingleClickListener {
                shareQR(geo.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                val uri =
//                    "geo:<${latitude}>,<${longtitude}>?q=<${latitude}>,<${longtitude}>()"
//                gotoUrl(uri)
//            }
//            btnAction.text = getString(R.string.Show_Location)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResultTypeWeb() {
        mDataBinding.apply {
            val strWeb = bundle?.getString("URL")
            val url = Url(strWeb.toString())
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = url.toBarcodeText(),
                    created = true
                )
            )
            imvResultType.setImageResource(R.drawable.bg_website_result_type)
            tvTitleToolbar.text = "Website"

            listQrDetail.add(QrDetailItem("Website", strWeb.toString()))
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)

            generateQrCode(url.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(url.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener { startActivityCustom(url.toBarcodeText(), QR) }
            btnShare.setOnSingleClickListener {
                shareQR(url.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                openWebsite(
//                    this@QrResultActivity,
//                    url.toFormattedText()
//                )
//            }
//            btnAction.text = getString(R.string.Open_in_System_Browser)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResultTypePhone() {
        mDataBinding.apply {
            val strPhone = bundle?.getString("PHONE")
            val phone = Phone(strPhone.toString())
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = phone.toBarcodeText(),
                    created = true
                )
            )
            tvTitleToolbar.text = "Phone"
            imvResultType.setImageResource(R.drawable.bg_phone_result_type)
            listQrDetail.add(QrDetailItem("Phone", strPhone.toString()))
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)

            generateQrCode(phone.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(phone.toBarcodeText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(phone.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(phone.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                dialPhoneNumber(
//                    this@QrResultActivity,
//                    strPhone.toString()
//                )
//            }
//            btnAction.text = getString(R.string.Call_Phone)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResultTypeEmail() {
        mDataBinding.apply {
            tvTitleToolbar.text = "Email"
            imvResultType.setImageResource(R.drawable.bg_email_result_type)
            val strEmail = bundle?.getString("EMAIL")
            val email = Email(email = strEmail.toString())
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = email.toBarcodeText(),
                    created = true
                )
            )
            listQrDetail.add(QrDetailItem("Email", strEmail.toString()))
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            generateQrCode(email.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(email.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(email.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(email.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                sendEmail(
//                    this@QrResultActivity,
//                    email.toFormattedText()
//                )
//            }
//            btnAction.text = getString(R.string.Send_Email)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResultTypeContact() {
        mDataBinding.tvTitleToolbar.text = "Contact"
        mDataBinding.imvResultType.setImageResource(R.drawable.type_contact)
        val strName = bundle?.getString("NAME")
        val strPhone = bundle?.getString("PHONE")
        val strEmail = bundle?.getString("EMAIL")
        val strUrl = bundle?.getString("URL")
        val strNote = bundle?.getString("NOTE")
        val strBirthday = bundle?.getString("BIRTHDAY")
        val strNickName = bundle?.getString("NICK_NAME")
        val strAddress = bundle?.getString("ADDRESS")
        val vcard = strName?.let {
            strPhone?.let { it1 ->
                strEmail?.let { it2 ->
                    strUrl?.let { it3 ->
                        strNote?.let { it4 ->
                            strBirthday?.let { it5 ->
                                strNickName?.let { it6 ->
                                    strAddress?.let { it7 ->
                                        VCard(
                                            firstName = it,
                                            phone = it1,
                                            email = it2,
                                            url = it3,
                                            note = it4,
                                            birthday = it5,
                                            nickname = it6,
                                            address = it7
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        handleResultVM.insertResultScan(
            ResultScanModel(
                time = timeResult,
                typeResult = typeResult,
                result = vcard!!.toBarcodeText(),
                created = true
            )
        )
        listQrDetail.apply {
            add(QrDetailItem("Name", strName.toString()))
            add(QrDetailItem("Phone", strPhone.toString()))
            add(QrDetailItem("Email", strEmail.toString()))
            add(QrDetailItem("Website", strUrl.toString()))
            if (strNote != "") {
                add(QrDetailItem("Note", strNote.toString()))
            }
            if (strBirthday != "") {
                add(QrDetailItem("Birthday", strBirthday.toString()))
            }
            if (strBirthday != "") {
                add(QrDetailItem("Nickname", strNickName.toString()))
            }
            if (strAddress != "") {
                add(QrDetailItem("Address", strAddress.toString()))
            }
        }

        mDataBinding.rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
        generateQrCode(vcard.toBarcodeText())
        mDataBinding.btnCopy.setOnSingleClickListener { setClipboard(vcard.toFormattedText()) }
        mDataBinding.btnCustomizeQr.setOnSingleClickListener {
            startActivityCustom(vcard.toBarcodeText(), QR)
        }
        mDataBinding.btnShare.setOnSingleClickListener {
            shareQR(vcard.toFormattedText())
        }
//        mDataBinding.btnAction.setOnSingleClickListener {
//            addToContacts(this, vcard)
//        }
//        mDataBinding.btnAction.text = getText(R.string.Add_to_Contact)
    }

    @SuppressLint("SetTextI18n")
    private fun updateResultTypeWifi() {
        mDataBinding.apply {
            tvTitleToolbar.text = "Wifi"
            imvResultType.setImageResource(R.drawable.bg_wifi_result_type)
            val wifiType = bundle?.getString("WIFI_TYPE")
            val strWifiName = bundle?.getString("WIFI_NAME")
            val strWifiPass = bundle?.getString("WIFI_PASS")
            val wifi: Wifi
            if (wifiType == "Free") {
                wifi = Wifi(
                    encryption = wifiType,
                    name = strWifiName
                )
                listQrDetail.add(QrDetailItem("Type", wifiType.toString()))
                listQrDetail.add(QrDetailItem("Name", strWifiName.toString()))
                rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            } else {
                wifi = Wifi(
                    encryption = wifiType,
                    name = strWifiName,
                    password = strWifiPass
                )
                listQrDetail.add(QrDetailItem("Type", wifiType.toString()))
                listQrDetail.add(QrDetailItem("Name", strWifiName.toString()))
                listQrDetail.add(QrDetailItem("Password", strWifiPass.toString()))
                mDataBinding.rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            }
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = wifi.toBarcodeText(),
                    created = true
                )
            )
            generateQrCode(wifi.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(wifi.toBarcodeText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(wifi.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(wifi.toFormattedText())
            }
//            btnAction.text = getString(R.string.Go_to_Settings)
//            btnAction.setOnSingleClickListener { goToWifiSetting(this@QrResultActivity) }
        }
    }

    private fun setUpFB() {
        mDataBinding.apply {
            val typeInput: String
            tvTitleToolbar.text = this@QrResultActivity.getString(R.string.Facebook)
            imvResultType.setImageResource(R.drawable.type_facebook)
            val profileID = bundle?.getString("profileID")
            val facebook: Facebook
            when (bundle?.getInt("typeInput")) {
                FragmentInputFacebook.ID -> {
                    typeInput = this@QrResultActivity.getString(R.string.facebook_id)
                    facebook = Facebook(facebookId = profileID)
                }

                else -> {
                    typeInput = this@QrResultActivity.getString(R.string.facebook_url)
                    facebook = Facebook(profileUrl = profileID)
                }
            }
            listQrDetail.add(
                QrDetailItem(
                    typeInput,
                    profileID.toString()
                )
            )
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = facebook.toBarcodeText(),
                    created = true
                )
            )
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            generateQrCode(facebook.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(facebook.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(facebook.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(facebook.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                gotoUrl("https://www.facebook.com/profile.php?id=$profileID")
//            }
//            btnAction.text = getString(R.string.Open_Facebook)
        }
    }

    private fun gotoUrl(uri: String) {
        val uri: Uri = Uri.parse(uri)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun setUpTwitter() {
        mDataBinding.apply {
            val typeInput: String
            tvTitleToolbar.text = this@QrResultActivity.getString(R.string.Twitter)
            imvResultType.setImageResource(R.drawable.type_twitter)
            val profileID = bundle?.getString("profileID")
            val twitter: Twitter
            when (bundle?.getInt("typeInput")) {
                FragmentInputTwitter.USERNAME -> {
                    typeInput = this@QrResultActivity.getString(R.string.Twitter_Username)
                    twitter = Twitter(username = profileID)
                }

                else -> {
                    typeInput = this@QrResultActivity.getString(R.string.profile_url)
                    twitter = Twitter(url = profileID)
                }
            }

            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = twitter.toBarcodeText(),
                    created = true
                )
            )
            listQrDetail.add(
                QrDetailItem(
                    typeInput,
                    profileID.toString()
                )
            )
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            generateQrCode(twitter.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(twitter.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(twitter.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(twitter.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                gotoUrl("https://twitter.com/$profileID")
//            }
//            btnAction.text = getString(R.string.Open_Twitter)
        }
    }

    private fun setUpWhatsapp() {
        mDataBinding.apply {
            tvTitleToolbar.text = this@QrResultActivity.getString(R.string.WhatsApp)
            imvResultType.setImageResource(R.drawable.type_whatsapp)
            val phone = bundle?.getString("phone")
            val whatsapp = Whatsapp(phone)
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = whatsapp.toBarcodeText(),
                    created = true
                )
            )

            listQrDetail.add(
                QrDetailItem(
                    this@QrResultActivity.getString(R.string.URL),
                    "whatsapp://send?phone=$phone"
                )
            )
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)

            generateQrCode(whatsapp.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(whatsapp.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(whatsapp.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(whatsapp.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                gotoUrl("whatsapp://send?phone=$phone")
//            }
//            btnAction.text = getString(R.string.Open_Whatsapp)
        }
    }

    private fun setUpYoutube() {
        mDataBinding.apply {
            tvTitleToolbar.text = this@QrResultActivity.getString(R.string.YouTube)
            imvResultType.setImageResource(R.drawable.type_youtobe)
            val result = bundle?.getString("result")
            val youtube : Youtube

            val titleResult: String
            when (bundle?.getInt("typeResult")) {
                VIDEO -> {
                    titleResult = this@QrResultActivity.getString(R.string.YouTube_Video_ID)
                    youtube = Youtube(videoId = result)
//                    btnAction.setOnSingleClickListener {
//                        gotoUrl("https://www.youtube.com/watch?v=$result")
//                    }
                }

                URL -> {
                    titleResult = this@QrResultActivity.getString(R.string.YouTube_URL)
                    youtube = Youtube(videoUrl = result)
//                    btnAction.setOnSingleClickListener {
//                        gotoUrl(result.toString())
//                    }
                }

                else -> {
                    titleResult = this@QrResultActivity.getString(R.string.YouTube_CHANNEL)
                    youtube = Youtube(channelId = result)
//                    btnAction.setOnSingleClickListener {
//                        gotoUrl("https://www.youtube.com/$result")
//                    }
                }
            }
            listQrDetail.add(
                QrDetailItem(
                    titleResult,
                    result.toString()
                )
            )

            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = youtube.toBarcodeText(),
                    created = true
                )
            )

            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            generateQrCode(youtube.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(youtube.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(youtube.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(youtube.toFormattedText())
            }
            //  btnAction.text = getString(R.string.Open_Youtube)
        }
    }

    private fun setUpIG() {
        mDataBinding.apply {
            val typeInput: String
            tvTitleToolbar.text = this@QrResultActivity.getString(R.string.Instagram)
            imvResultType.setImageResource(R.drawable.type_instagram)
            val instagram: Instagram
            val username = bundle?.getString("username")
            when (bundle?.getInt("inputType")) {
                FragmentInputInstagram.ID -> {
                    typeInput = this@QrResultActivity.getString(R.string.username_id)
                    instagram = Instagram(username = username)
                }

                else -> {
                    typeInput = this@QrResultActivity.getString(R.string.profile_url)
                    instagram = Instagram(url = username)
                }
            }

            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = instagram.toBarcodeText(),
                    created = true
                )
            )
            listQrDetail.add(
                QrDetailItem(
                    typeInput,
                    username.toString()
                )
            )
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            generateQrCode(instagram.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(instagram.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(instagram.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(instagram.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                gotoUrl("https://www.instagram.com/$username/")
//            }
//            btnAction.text = getString(R.string.Open_Instagram)
        }
    }

    private fun setUpTiktok() {
        mDataBinding.apply {
            val typeInput: String
            tvTitleToolbar.text = this@QrResultActivity.getString(R.string.Tiktok)
            imvResultType.setImageResource(R.drawable.type_tiktok)
            val profileID = bundle?.getString("profileID")
            val tiktok: Tiktok

            when (bundle?.getInt("typeInput")) {
                FragmentInputTiktok.USERNAME -> {
                    typeInput = this@QrResultActivity.getString(R.string.username_id)
                    tiktok = Tiktok(id = profileID)
                }

                FragmentInputTiktok.VIDEO_URL -> {
                    typeInput = this@QrResultActivity.getString(R.string.video_url)
                    tiktok = Tiktok(url = profileID)
                }

                else -> {
                    typeInput = this@QrResultActivity.getString(R.string.channel_url)
                    tiktok = Tiktok(url = profileID)
                }
            }

            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = tiktok.toBarcodeText(),
                    created = true
                )
            )

            listQrDetail.add(
                QrDetailItem(
                    typeInput,
                    profileID.toString()
                )
            )
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)

            generateQrCode(tiktok.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(tiktok.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(tiktok.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(tiktok.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                gotoUrl("https://www.tiktok.com/$profileID")
//            }
//            btnAction.text = getString(R.string.Open_Tiktok)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateResultTypeText() {
        mDataBinding.apply {
            mDataBinding.tvTitleToolbar.text = "Text"
            mDataBinding.imvResultType.setImageResource(R.drawable.bg_text_result_type)
            val strText = bundle?.getString("TEXT")
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = strText!!,
                    created = true
                )
            )
            listQrDetail.add(QrDetailItem("Text", strText.toString()))
            mDataBinding.rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            generateQrCode("Text :" + strText.trim())
            mDataBinding.btnCopy.setOnSingleClickListener { setClipboard("Text :" + strText.trim()) }
            mDataBinding.btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom("Text :" + strText.trim(), QR)
            }
            mDataBinding.btnShare.setOnSingleClickListener {
                shareQR(strText)
            }
//            btnAction.setOnSingleClickListener {
//                ActionButtonQrResult.searchInGoogle(
//                    this@QrResultActivity,
//                    strText.toString()
//                )
//            }
//            btnAction.text = getString(R.string.Search_in_Google)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateToLong(date: String): Long {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)
        val date = sdf.parse(date)
        return date!!.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun updateCalendarType() {
        mDataBinding.apply {
            tvTitleToolbar.text = "Calendar"
            imvResultType.setImageResource(R.drawable.bg_calendar_result_type)
            val strEvent = bundle?.getString("EVENT_TITLE")
            val strAddress = bundle?.getString("ADDRESS")
            val strStartDate = bundle?.getString("START_DATE")
            val strEndDate = bundle?.getString("END_DATE")
            val strWebsite = bundle?.getString("WEBSITE")
            val strNote = bundle?.getString("NOTE")

            listQrDetail.apply {
                add(QrDetailItem("Event Title", strEvent.toString()))
                add(QrDetailItem("Address", strAddress.toString()))
                add(QrDetailItem("Start date", strStartDate.toString()))
                add(QrDetailItem("End date", strEndDate.toString()))
                if (strNote != "") {
                    add(QrDetailItem("Note", strNote.toString()))
                }
                if (strWebsite != "") {
                    add(QrDetailItem("Website", strWebsite.toString()))
                }
            }
            val strStartVevent = strStartDate!!.replace(" at ", " ")
            val strEndVevent = strEndDate!!.replace(" at ", " ")
            val calendar = VEvent(
                uid = strEvent,
                stamp = strAddress,
                startDate = convertDateToLong(strStartVevent),
                endDate = convertDateToLong(strEndVevent),
                summary = strNote,
            )
            handleResultVM.insertResultScan(
                ResultScanModel(
                    time = timeResult,
                    typeResult = typeResult,
                    result = calendar.toBarcodeText(),
                    created = true
                )
            )
            rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
            generateQrCode(calendar.toBarcodeText())
            btnCopy.setOnSingleClickListener { setClipboard(calendar.toFormattedText()) }
            btnCustomizeQr.setOnSingleClickListener {
                startActivityCustom(calendar.toBarcodeText(), QR)
            }
            btnShare.setOnSingleClickListener {
                shareQR(calendar.toFormattedText())
            }
//            btnAction.setOnSingleClickListener {
//                addToEvent(this@QrResultActivity, calendar)
//            }
//            btnAction.text = getString(R.string.Add_to_Calendar)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResultTypeBarcode() {
        mDataBinding.tvTitleToolbar.text = "Barcode"
        mDataBinding.imvResultType.setImageResource(R.drawable.bg_barcode_result_type)
        val strBarcode = bundle?.getString("BAR_CODE")
        val barcode = Barcode(strBarcode)
        handleResultVM.insertResultScan(
            ResultScanModel(
                time = timeResult,
                typeResult = typeResult,
                result = barcode.toBarcodeText(),
                created = true
            )
        )

        listQrDetail.add(QrDetailItem("Barcode", strBarcode.toString()))
        mDataBinding.rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)

        generateBarcode(barcode.toBarcodeText())
        mDataBinding.btnCopy.setOnSingleClickListener { setClipboard(barcode.toFormattedText()) }
        mDataBinding.btnCustomizeQr.setOnSingleClickListener {
            startActivityCustom(barcode.toBarcodeText(), BARCODE)
        }
        mDataBinding.btnShare.setOnSingleClickListener {
            shareQR(barcode.toFormattedText())
        }
//        mDataBinding.btnAction.setOnSingleClickListener {
//            val uri =
//                Uri.parse("${searchEngine}${barcode.toFormattedText()}")
//            val intent = Intent(Intent.ACTION_VIEW, uri)
//            startActivity(intent)
//        }
//        mDataBinding.btnAction.text = getString(R.string.Search_in_Google)

    }

    private fun startActivityCustom(strRequest: String, typeCode: String) {
        val bundleCustom = Bundle()
        val intent = Intent(this, CustomizeQrActivity::class.java)
        bundleCustom.putString("QR_CODE", strRequest)
        bundleCustom.putString("TYPE_CODE", typeCode)
        intent.putExtras(bundleCustom)
        laucher.launch(intent)
    }

    private fun generateQrCode(strRequest: String) {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(
                strRequest,
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            mDataBinding.imvQrResult.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun generateQrCode(strRequest: String, colorQr: Int, colorBack: Int) {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(
                strRequest,
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) colorQr else colorBack)
                }
            }
            mDataBinding.imvQrResult.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun generateBarcode(strBarcode: String) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(
                strBarcode,
                BarcodeFormat.CODE_128,
                700,
                300
            )
            val bitmap = Bitmap.createBitmap(
                700,
                300,
                Bitmap.Config.RGB_565
            )
            for (x in 0 until 700) {
                for (y in 0 until 300) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            mDataBinding.imvQrResult.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun generateBarcode(strBarcode: String, colorQr: Int, colorBack: Int) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(
                strBarcode,
                BarcodeFormat.CODE_128,
                700,
                300
            )
            val bitmap = Bitmap.createBitmap(
                700,
                300,
                Bitmap.Config.RGB_565
            )
            for (x in 0 until 700) {
                for (y in 0 until 300) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) colorQr else colorBack)
                }
            }
            mDataBinding.imvQrResult.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun setClipboard(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "copied to clipboard", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val BARCODE = "BARCODE"
        const val QR = "QR"
        const val VIDEO = 0
        const val URL = 1
    }

}