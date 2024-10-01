package com.example.qrscan.ui.fragment.create.wifi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputWifiBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.util.TypeResult
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class InputWifiFragment : BaseFragment<FragmentInputWifiBinding>() {
    var wifiType = "Free"
    override fun getLayout(): Int = R.layout.fragment_input_wifi
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {}
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.apply {
            btnCreated.isEnabled = false
            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            edtWifiName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edtWifiName.text.toString().trim() == "") {
                        btnCreated.isEnabled = false
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                    } else {
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                        btnCreated.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            tabInputWifi.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            wifiType = "Free"
                            tvWifiPass.visibility = View.GONE
                            edtWifiPass.visibility = View.GONE
                        }
                        1 -> {
                            wifiType = "WPA/WPA2"
                            tvWifiPass.visibility = View.VISIBLE
                            edtWifiPass.visibility = View.VISIBLE
                        }
                        2 -> {
                            wifiType = "WEP"
                            tvWifiPass.visibility = View.VISIBLE
                            edtWifiPass.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
            ivBack.setOnSingleClickListener { requireActivity().finish() }
            btnCreated.setOnSingleClickListener {
                val currentDateTime = LocalDateTime.now()
                val time = currentDateTime.format(formatter)
                val intent = Intent(activity, QrResultActivity::class.java)
                val bundle = Bundle()
                bundle.putString("TYPE",TypeResult.WIFI)
                bundle.putString("WIFI_TYPE",wifiType)
                bundle.putString("time",time)
                if (wifiType=="Free"){
                    bundle.putString("WIFI_NAME",edtWifiName.text.toString())
                }else{
                    bundle.putString("WIFI_NAME",edtWifiName.text.toString())
                    bundle.putString("WIFI_PASS",edtWifiName.text.toString())
                }
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

}