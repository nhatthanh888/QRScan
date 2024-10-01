package com.example.qrscan.ui.fragment.create.instagram

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
import com.example.qrscan.databinding.FragmentInputIgBinding
import com.example.qrscan.extension.invisible
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.util.TypeResult
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class FragmentInputInstagram : BaseFragment<FragmentInputIgBinding>() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)
    private var inputType = 0
    override fun getLayout(): Int = R.layout.fragment_input_ig

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.apply {
            btnCreated.isEnabled = false
            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            ivBack.setOnSingleClickListener { requireActivity().finish() }
            btnCreated.setOnSingleClickListener { handleOnClickBtnCreate(edUsermane.text.toString()) }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        ID -> {
                            inputType = ID
                            tvText.text = requireActivity().getString(R.string.username)
                            edUsermane.apply {
                                setText("")
                                hint = requireActivity().getString(R.string.e_g_JohnDoe)
                            }
                        }

                        URL -> {
                            inputType = URL
                            tvText.text = requireActivity().getString(R.string.profile_url)
                            edUsermane.apply {
                                setText("")
                                hint = requireActivity().getString(R.string.enter_instagram_url)
                            }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })

            edUsermane.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (inputType==0){
                        if (edUsermane.text.toString().trim() == "") {
                            btnCreated.isEnabled = false
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                        } else {
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                            btnCreated.isEnabled = true
                        }
                    }else{
                        if (edUsermane.text.toString().trim() == ""||!isValidInstagramUrl(edUsermane.text.toString().trim())) {
                            btnCreated.isEnabled = false
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                        } else {
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                            btnCreated.isEnabled = true
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate(text: String) {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        val intent = Intent(activity, QrResultActivity::class.java)
        val bundle = Bundle()
        mBinding?.apply {
            bundle.putString("TYPE", TypeResult.IG)
            bundle.putInt("inputType", inputType)
            bundle.putString("username", text)
            bundle.putString("time", time)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun isValidInstagramUrl(url: String): Boolean {
        val instagramUrlRegex = """^(https?://)?(www\.)?instagram\.com/[a-zA-Z0-9_]+$""".toRegex()
        mBinding?.apply {
            return if (instagramUrlRegex.matches(url)) {
                edUsermane.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinate.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            } else {
                edUsermane.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinate.visible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
        return false
    }

    companion object {
        const val ID = 0
        const val URL = 1
    }
}