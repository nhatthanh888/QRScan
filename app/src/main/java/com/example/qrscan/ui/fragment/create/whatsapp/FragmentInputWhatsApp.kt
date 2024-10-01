package com.example.qrscan.ui.fragment.create.whatsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputWhatsappBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.util.TypeResult
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class FragmentInputWhatsApp : BaseFragment<FragmentInputWhatsappBinding>() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)
    override fun getLayout(): Int = R.layout.fragment_input_whatsapp

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCountryCode()
        mBinding?.apply {
            btnCreated.isEnabled = false
            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            ivBack.setOnSingleClickListener { requireActivity().finish() }
            btnCreated.setOnSingleClickListener { handleOnClickBtnCreate(edPhone.text.toString()) }
            edPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edPhone.text.toString().trim() == "") {
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
        }
    }

    private fun setUpCountryCode(){
        mBinding?.apply {
            countryCode.registerCarrierNumberEditText(edPhone)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate(text: String) {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        val intent = Intent(activity, QrResultActivity::class.java)
        val bundle = Bundle()
        mBinding?.apply {
            bundle.putString("TYPE", TypeResult.WHATSAPP)
            bundle.putString("phone", text)
            bundle.putString("time", time)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }
}