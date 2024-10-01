package com.example.qrscan.ui.fragment.create.facebook

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputFbBinding
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

class FragmentInputFacebook : BaseFragment<FragmentInputFbBinding>() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)
    private var typeInput = 0
    override fun getLayout(): Int = R.layout.fragment_input_fb

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
            btnCreated.setOnSingleClickListener { handleOnClickBtnCreate(edProfileID.text.toString()) }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        ID -> {
                            typeInput = ID
                            tvID.text = requireActivity().getString(R.string.facebook_id)
                            edProfileID.apply {
                                setText("")
                                inputType = InputType.TYPE_CLASS_NUMBER
                                hint = requireActivity().getString(R.string.e_g_JohnDoe)
                            }
                        }

                        URL -> {
                            typeInput = URL
                            tvID.text = requireActivity().getString(R.string.facebook_url)
                            edProfileID.apply {
                                setText("")
                                inputType = InputType.TYPE_CLASS_TEXT
                                hint = requireActivity().getString(R.string.enter_facebook_url)
                            }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            edProfileID.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (typeInput == 0) {
                        if (edProfileID.text.toString().trim() == "" || edProfileID.text.toString()
                                .trim().length < 15
                        ) {
                            btnCreated.isEnabled = false
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                        } else {
                            btnCreated.isEnabled = true
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                        }
                    } else {
                        if (edProfileID.text.toString().trim() == ""||!isValidFacebookUrl(edProfileID.text.toString().trim())) {
                            btnCreated.isEnabled = false
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                        } else {
                            btnCreated.isEnabled = true
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                        }
                    }

                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun isValidFacebookUrl(url: String): Boolean {
        val facebookUrlRegex = """^(https?://)?(www\.)?facebook\.com/[a-zA-Z0-9.]+$""".toRegex()
        mBinding?.apply {
          return  if (facebookUrlRegex.matches(url)) {
                edProfileID.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinate.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            } else {
                edProfileID.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinate.visible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate(text: String) {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        val intent = Intent(activity, QrResultActivity::class.java)
        val bundle = Bundle()
        mBinding?.apply {
            bundle.putString("TYPE", TypeResult.FB)
            bundle.putInt("typeInput", typeInput)
            bundle.putString("profileID", text)
            bundle.putString("time", time)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }

    companion object {
        const val ID = 0
        const val URL = 1
    }
}