package com.example.qrscan.ui.activity.create

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.qrscan.ui.fragment.create.phone.InputCreatePhoneFragment
import com.example.qrscan.R
import com.example.qrscan.base.BaseActivity
import com.example.qrscan.databinding.ActivityCreateQrBinding
import com.example.qrscan.ui.fragment.create.barcode.InputBarcodeFragment
import com.example.qrscan.ui.fragment.create.calendar.InputCalendarFragment
import com.example.qrscan.ui.fragment.create.contact.InputContactPhoneFragment
import com.example.qrscan.ui.fragment.create.email.InputEmailFragment
import com.example.qrscan.ui.fragment.create.facebook.FragmentInputFacebook
import com.example.qrscan.ui.fragment.create.instagram.FragmentInputInstagram
import com.example.qrscan.ui.fragment.create.location.InputLocationFragment
import com.example.qrscan.ui.fragment.create.text.InputTextFragment
import com.example.qrscan.ui.fragment.create.tiktok.FragmentInputTiktok
import com.example.qrscan.ui.fragment.create.twitter.FragmentInputTwitter
import com.example.qrscan.ui.fragment.create.web.InputCreateWebFragment
import com.example.qrscan.ui.fragment.create.whatsapp.FragmentInputWhatsApp
import com.example.qrscan.ui.fragment.create.wifi.InputWifiFragment
import com.example.qrscan.ui.fragment.create.youtobe.FragmentInputYoutobe
import com.example.qrscan.util.GrantPermission
import com.example.qrscan.viewmodel.ShareDataVM


class CreateQrActivity : BaseActivity<ActivityCreateQrBinding>() {

    private val scanVM: ShareDataVM by viewModels()
    private var permissionType=0
    override fun getContentView(): Int = R.layout.activity_create_qr

    override fun initView() {
        val qrInputType = intent.getIntExtra("KEY_QR_TYPE", 0)
        addInputQrFragment(qrInputType)
        setupUI(findViewById(R.id.cl_create_qr))
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerCreateQr, fragment)
            .commit()
    }

    fun requestPermissionLocation() {
        GrantPermission.buildPermissionLocation().checkPermission(
            checkPermissionListener = { case ->
                when (case) {
                    GrantPermission.CASE_ALL_PERMISSION_GRANTED -> {
                        scanVM.setPermissionLocation(true)
                    }

                    GrantPermission.CASE_ALL_PERMISSION_DENIED -> {
                        permissionType = PERMISSION_LOCATION
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


    private fun addInputQrFragment(qrInputType: Int) {
        when (qrInputType) {
            0 -> addFragment(InputCreateWebFragment())
            1 -> addFragment(InputCreatePhoneFragment())
            2 -> addFragment(InputEmailFragment())
            3 -> addFragment(InputTextFragment())
            4 -> addFragment(InputContactPhoneFragment())
            5 -> addFragment(InputWifiFragment())
            6 -> addFragment(InputCalendarFragment())
            7 -> addFragment(InputLocationFragment())
            8 -> addFragment(InputBarcodeFragment())

            9 -> addFragment(FragmentInputFacebook())
            10 -> addFragment(FragmentInputInstagram())
            11 -> addFragment(FragmentInputTwitter())
            12 -> addFragment(FragmentInputTiktok())
            13 -> addFragment(FragmentInputYoutobe())
            14 -> addFragment(FragmentInputWhatsApp())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                this.hideSoftKeyboard()
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    companion object {
        const val PERMISSION_LOCATION = 3
    }
}