package com.example.qrscan.ui.fragment.create.barcode

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputBarcodeBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.util.TypeResult
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class InputBarcodeFragment : BaseFragment<FragmentInputBarcodeBinding>() {
    override fun getLayout(): Int = R.layout.fragment_input_barcode

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.apply {
            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            btnCreated.isEnabled = false
            edtBarcode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edtBarcode.text.toString().trim() == "") {
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
            ivBack.setOnSingleClickListener { requireActivity().finish() }
            btnCreated.setOnSingleClickListener { sendBarcodeToGenrate() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendBarcodeToGenrate() {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        val intent = Intent(activity, QrResultActivity::class.java)
        val bundle = Bundle()
        mBinding?.apply {
            bundle.putString("TYPE", TypeResult.BARCODE)
            bundle.putString("BAR_CODE", edtBarcode.text.toString())
            bundle.putString("time", time)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }
}