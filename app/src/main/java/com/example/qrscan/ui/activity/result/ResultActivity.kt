package com.example.qrscan.ui.activity.result

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.qrscan.App
import com.example.qrscan.Bridge
import com.example.qrscan.R
import com.example.qrscan.adapter.QrResultDetailAdapter
import com.example.qrscan.data.model.MultipleScanModel
import com.example.qrscan.data.model.QrDetailItem
import com.example.qrscan.data.model.ResultScanModel
import com.example.qrscan.databinding.ActivityResultBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.schema.Facebook
import com.example.qrscan.schema.Instagram
import com.example.qrscan.schema.Tiktok
import com.example.qrscan.schema.Twitter
import com.example.qrscan.schema.VCard
import com.example.qrscan.schema.VEvent
import com.example.qrscan.schema.Youtube
import com.example.qrscan.ui.fragment.scan.FragmentScan
import com.example.qrscan.ui.fragment.scan.ResultFragment
import com.example.qrscan.ui.fragment.setting.SettingFragment
import com.example.qrscan.util.ActionButtonQrResult
import com.example.qrscan.util.HandleTypeResultScan
import com.example.qrscan.util.TypeResult
import com.example.qrscan.viewmodel.HandleResultFactory
import com.example.qrscan.viewmodel.HandleResultVM
import com.example.qrscan.viewmodel.ShareDataVM
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

@Suppress("NAME_SHADOWING", "DEPRECATION")
class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var clipData: ClipData
    private lateinit var itemScan: ResultScanModel
    private var typeCodeResult = ""
    private var resultsScan = ""
    private var timeResult = ""
    private var typeResultSCan = ""
    private var idResult = 0
    private var isFavorite = false
    private lateinit var listQrDetail: ArrayList<QrDetailItem>
    private val scanVM: ShareDataVM by viewModels()
    private var searchEngine = ""
    private val sharedPreferences by lazy {
        getSharedPreferences(FragmentScan.CONFIG_APP, Context.MODE_PRIVATE)
    }
    private val handleResultVM: HandleResultVM by viewModels {
        HandleResultFactory((application as App).handleResultScanRepository)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        listQrDetail = ArrayList()
        handleBackPress()
        getData()
        observerFavorite()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        generatorQRCode()
        checkType()
        binding.apply {
            tvTime.text = timeResult
            if (isFavorite) {
                ivStar.setImageResource(R.drawable.favorited)
            } else {
                ivStar.setImageResource(R.drawable.star_circle)
            }
            ivBack.setOnSingleClickListener {
                finish()
            }
            ivStar.setOnSingleClickListener {
                isFavorite = if (isFavorite) {
                    scanVM.setFavorite(false)
                    if (idResult == 0) {
                        handleResultVM.listHistory.observe(this@ResultActivity) {
                            updateItem(it[0].id, false)
                        }
                    } else {
                        updateItem(idResult, false)
                    }
                    false
                } else {
                    scanVM.setFavorite(true)
                    if (idResult == 0) {
                        handleResultVM.listHistory.observe(this@ResultActivity) {
                            updateItem(it[0].id, true)
                        }
                    } else {
                        updateItem(idResult, true)
                    }
                    true
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkType() {
        binding.apply {
            when (typeResultSCan) {
                TypeResult.WEB -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Website),
                            resultsScan
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.bg_website_result_type
                    )
                    btnAction.setOnSingleClickListener {
                        gotoUrl(resultsScan)
                    }
                    btnAction.text = getString(R.string.Open_in_System_Browser)
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, resultsScan)
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            resultsScan
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                }

                TypeResult.PHONE -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Phone),
                            HandleTypeResultScan.handleResultPhone(resultsScan)
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.bg_phone_result_type
                    )
                    btnAction.setOnSingleClickListener {
                        ActionButtonQrResult.dialPhoneNumber(
                            this@ResultActivity,
                            HandleTypeResultScan.handleResultPhone(resultsScan)
                        )
                    }
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            HandleTypeResultScan.handleResultPhone(resultsScan)
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            HandleTypeResultScan.handleResultPhone(resultsScan)
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.text = getString(R.string.Call_Phone)
                }

                TypeResult.EMAIL -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Email),
                            HandleTypeResultScan.handleResultEmail(resultsScan)
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.bg_email_result_type
                    )
                    btnAction.setOnSingleClickListener {
                        ActionButtonQrResult.sendEmail(
                            this@ResultActivity,
                            HandleTypeResultScan.handleResultEmail(resultsScan)
                        )
                    }
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            HandleTypeResultScan.handleResultEmail(resultsScan)
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            HandleTypeResultScan.handleResultEmail(resultsScan)
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.text = getString(R.string.Send_Email)
                }

                TypeResult.TEXT -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Text),
                            resultsScan
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.bg_text_result_type
                    )
                    btnAction.setOnSingleClickListener {
                        ActionButtonQrResult.searchInGoogle(
                            this@ResultActivity,
                            resultsScan
                        )
                    }
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            resultsScan
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            resultsScan
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.text = getString(R.string.Search_in_Google)
                }

                TypeResult.CONTACT -> {
                    val vcard = HandleTypeResultScan.handleResultContact(resultsScan)
                    listQrDetail.apply {
                        add(QrDetailItem("Name", vcard.firstName))
                        add(QrDetailItem("Phone", vcard.phone))
                        add(QrDetailItem("Email", vcard.email))
                        if (vcard.url != "") {
                            add(QrDetailItem("Website", vcard.url))
                        }
                        if (vcard.note != "") {
                            add(QrDetailItem("Note", vcard.note))
                        }
                        if (vcard.birthday != "") {
                            add(QrDetailItem("Birthday", vcard.birthday))
                        }
                        if (vcard.nickname != "") {
                            add(QrDetailItem("Nickname", vcard.nickname))
                        }
                    }
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.type_contact
                    )
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            vcard.toFormattedText()
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            vcard.toFormattedText()
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.setOnSingleClickListener {
                        ActionButtonQrResult.addToContacts(this@ResultActivity, vcard)
                    }
                    btnAction.text = getText(R.string.Add_to_Contact)
                }

                TypeResult.WIFI -> {
                    val wifi = HandleTypeResultScan.handleResultWifi(resultsScan)
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.WiFi_Type),
                            wifi.encryption.toString()
                        )
                    )
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.name),
                            wifi.name.toString()
                        )
                    )
                    if (wifi.password != null) {
                        listQrDetail.add(
                            QrDetailItem(
                                this@ResultActivity.getString(R.string.Password),
                                wifi.password.toString()
                            )
                        )
                    }

                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.bg_wifi_result_type
                    )
                    btnAction.text = getString(R.string.Go_to_Settings)
                    btnAction.setOnSingleClickListener { ActionButtonQrResult.goToWifiSetting(this@ResultActivity) }

                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            wifi.toFormattedText()
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            wifi.toFormattedText()
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                }

                TypeResult.CALENDAR -> {
                    val calendar = HandleTypeResultScan.handleResultCalendar(resultsScan)

                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.name),
                            calendar.uid.toString()
                        )
                    )
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Address),
                            calendar.stamp.toString()
                        )
                    )
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.star_date),
                            HandleTypeResultScan.convertToTime(calendar.startDateStr.toString())
                        )
                    )
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.end_date),
                            HandleTypeResultScan.convertToTime(calendar.endDateStr.toString())
                        )
                    )

                    if (calendar.summary != null) {
                        listQrDetail.add(
                            QrDetailItem(
                                this@ResultActivity.getString(R.string.Note),
                                calendar.summary.toString()
                            )
                        )
                    }

                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.bg_calendar_result_type
                    )
                    btnAction.setOnSingleClickListener {
                        ActionButtonQrResult.addToEvent(this@ResultActivity, calendar)
                    }
                    btnAction.text = getString(R.string.Add_to_Calendar)

                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            calendar.toFormattedText()
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            calendar.toFormattedText()
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }

                }

                TypeResult.LOCATION -> {
                    val geo = HandleTypeResultScan.handleResultLocation(resultsScan)
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Location_Name),
                            geo.name
                        )
                    )
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.latitude),
                            geo.latitude
                        )
                    )
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.longtitude),
                            geo.longtitude
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.bg_location_result_type
                    )
                    btnAction.setOnSingleClickListener {
                        val uri =
                            "geo:<${geo.latitude}>,<${geo.longtitude}>?q=<${geo.latitude}>,<${geo.longtitude}>()"
                        gotoUrl(uri)
                    }
                    btnAction.text = getString(R.string.Show_Location)

                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            "${geo.name}\n" +
                                    "${geo.latitude}\n" +
                                    geo.longtitude
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            "${geo.name}\n" +
                                    "${geo.latitude}\n" +
                                    geo.longtitude
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                }

                TypeResult.FB -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.facebook),
                            HandleTypeResultScan.handleResultFaceBook(resultsScan)
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.type_facebook
                    )

                    btnAction.setOnSingleClickListener {
                        if (resultsScan.startsWith(Facebook.FACEBOOK_ID_PREFIX)) {
                            gotoUrl(
                                "https://www.facebook.com/profile.php?id=${
                                    HandleTypeResultScan.handleResultFaceBook(
                                        resultsScan
                                    )
                                }"
                            )
                        } else {
                            gotoUrl(
                                HandleTypeResultScan.handleResultFaceBook(
                                    resultsScan
                                )
                            )
                        }
                    }
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            HandleTypeResultScan.handleResultFaceBook(resultsScan)
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            resultsScan
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.text = getString(R.string.Open_Facebook)
                }

                TypeResult.IG -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Instagram),
                            HandleTypeResultScan.handleResultInstagram(resultsScan)
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.type_instagram
                    )
                    btnAction.setOnSingleClickListener {
                        if (resultsScan.startsWith(Instagram.USERNAME_PREFIX)) {
                            gotoUrl(
                                "https://www.instagram.com/${
                                    HandleTypeResultScan.handleResultInstagram(
                                        resultsScan
                                    )
                                }/"
                            )
                        } else if (resultsScan.startsWith(Instagram.URL_PREFIX)) {
                            gotoUrl(
                                HandleTypeResultScan.handleResultInstagram(resultsScan)
                            )
                        }
                    }
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            HandleTypeResultScan.handleResultInstagram(
                                resultsScan
                            )
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            resultsScan
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.text = getString(R.string.Open_Instagram)
                }

                TypeResult.TWITTER -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Twitter),
                            HandleTypeResultScan.handleResultTwitter(resultsScan)
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.type_twitter
                    )
                    btnAction.setOnSingleClickListener {
                        if (resultsScan.startsWith(Twitter.USERNAME_PREFIX)) {
                            gotoUrl(
                                "https://twitter.com/${
                                    HandleTypeResultScan.handleResultTwitter(
                                        resultsScan
                                    )
                                }"
                            )
                        } else {
                            gotoUrl(
                                HandleTypeResultScan.handleResultTwitter(resultsScan)
                            )
                        }
                    }
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            HandleTypeResultScan.handleResultTwitter(
                                resultsScan
                            )
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            resultsScan
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.text = getString(R.string.Open_Twitter)
                }

                TypeResult.TIKTOK -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Tiktok),
                            HandleTypeResultScan.handleResultTiktok(resultsScan, "q")
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.type_tiktok
                    )
                    btnAction.setOnSingleClickListener {
                        if (resultsScan.startsWith(Tiktok.ID_PREFIX)) {
                            gotoUrl(
                                "https://www.tiktok.com/@${
                                    HandleTypeResultScan.handleResultTiktok(
                                        resultsScan, "q"
                                    )
                                }"
                            )
                        } else {
                            gotoUrl(
                                HandleTypeResultScan.handleResultTiktok(resultsScan, "q")
                            )
                        }
                    }

                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            HandleTypeResultScan.handleResultTiktok(
                                resultsScan, "q"
                            )
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            resultsScan
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.text = getString(R.string.Open_Tiktok)
                }

                TypeResult.YOUTUBE -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.YouTube),
                            HandleTypeResultScan.handResultYoutube(resultsScan)
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.type_youtobe
                    )

                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            HandleTypeResultScan.handResultYoutube(resultsScan)
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    btnAction.text = this@ResultActivity.getString(R.string.Open_Youtube)
                    btnAction.setOnSingleClickListener {
                        if (resultsScan.startsWith(Youtube.CHANNEL_ID_PREFIX)) {
                            gotoUrl(
                                "https://www.youtube.com/${
                                    HandleTypeResultScan.handResultYoutube(
                                        resultsScan
                                    )
                                }"
                            )
                        } else if (resultsScan.startsWith(Youtube.VIDEO_ID_PREFIX)) {
                            gotoUrl(
                                "https://www.youtube.com/watch?v=${
                                    HandleTypeResultScan.handResultYoutube(
                                        resultsScan
                                    )
                                }"
                            )
                        } else {
                            gotoUrl(resultsScan)
                        }
                    }

                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            resultsScan
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                }

                TypeResult.WHATSAPP -> {
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.WhatsApp),
                            resultsScan
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.type_whatsapp
                    )
                    btnAction.setOnSingleClickListener {
                        gotoUrl(resultsScan)
                    }

                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            resultsScan
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            resultsScan
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }

                    btnAction.text = getString(R.string.Open_Whatsapp)
                }

                TypeResult.BARCODE -> {
                    generatorBarCode()
                    listQrDetail.add(
                        QrDetailItem(
                            this@ResultActivity.getString(R.string.Product),
                            HandleTypeResultScan.handleResultBarcode(resultsScan)
                        )
                    )
                    rcvQrDetail.adapter = QrResultDetailAdapter(listQrDetail)
                    bgType.background = ContextCompat.getDrawable(
                        this@ResultActivity,
                        R.drawable.bg_barcode_result_type
                    )
                    btnCopy.setOnSingleClickListener {
                        clipData = ClipData.newPlainText(
                            "text",
                            HandleTypeResultScan.handleResultBarcode(resultsScan)
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(this@ResultActivity, "Copied", Toast.LENGTH_SHORT).show()
                    }
                    ivShare.setOnSingleClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            HandleTypeResultScan.handleResultBarcode(resultsScan)
                        )
                        startActivity(Intent.createChooser(intent, "Share Via"))
                    }
                    btnAction.setOnSingleClickListener {
                        val uri =
                            Uri.parse(
                                "${searchEngine}${
                                    HandleTypeResultScan.handleResultBarcode(
                                        resultsScan
                                    )
                                }"
                            )
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    btnAction.text = getString(R.string.Search_in_Google)
                }
            }
        }
    }

    private fun observerFavorite() {
        binding.apply {
            scanVM.isFavorite.observe(this@ResultActivity) {
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

    private fun getData() {
        if (intent.getParcelableExtra<ResultScanModel>(MODEL_SCAN) != null) {
            itemScan =
                intent.getParcelableExtra<ResultScanModel>(MODEL_SCAN) as ResultScanModel
            itemScan.apply {
                idResult = id
                resultsScan = result
                timeResult = time
                typeCodeResult = typeCodeScan
                typeResultSCan = typeResult
                isFavorite = favorite
            }
        }
    }

    private fun generatorQRCode() {
        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix?
        try {
            bitMatrix = writer.encode(
                resultsScan,
                BarcodeFormat.QR_CODE,
                380,
                380
            )

            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            binding.ivResult.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun generatorBarCode() {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix?
        try {
            bitMatrix = writer.encode(
                resultsScan,
                BarcodeFormat.CODE_128,
                600,
                250
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            binding.ivResult.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun handleBackPress() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun gotoUrl(uri: String) {
        val uri: Uri = Uri.parse(uri)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    companion object {
        const val MULTIPLE_SCAN = "multiple_scan"
        const val BARCODE = "barcode"
        const val QRCODE = "qrcode"
        const val MODEL_SCAN = "model_scan"
    }
}