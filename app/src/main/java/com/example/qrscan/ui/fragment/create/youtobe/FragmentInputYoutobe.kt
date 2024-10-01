package com.example.qrscan.ui.fragment.create.youtobe

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
import com.example.qrscan.databinding.FragmentInputYoutobeBinding
import com.example.qrscan.extension.invisible
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.util.TypeResult
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class FragmentInputYoutobe : BaseFragment<FragmentInputYoutobeBinding>() {
    private var typeResult = 0

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)
    override fun getLayout(): Int = R.layout.fragment_input_youtobe

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
            btnCreated.setOnSingleClickListener { handleOnClickBtnCreate(edInput.text.toString()) }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        VIDEO -> {
                            tvText.text = this@FragmentInputYoutobe.getString(R.string.video_id2)
                            edInput.setText("")
                            edInput.hint =
                                this@FragmentInputYoutobe.getString(R.string.enter_youtube_video_id)
                            typeResult = VIDEO
                        }

                        URL -> {
                            tvText.text = this@FragmentInputYoutobe.getString(R.string.URL)
                            edInput.setText("https://")
                            typeResult = URL
                        }

                        CHANNEL -> {
                            tvText.text = this@FragmentInputYoutobe.getString(R.string.channel_id)
                            edInput.hint = "Enter ID"
                            edInput.setText("")
                            typeResult = CHANNEL
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })

            edInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (typeResult == URL) {
                        if (edInput.text.toString()
                                .trim() == "" || !isValidYouTubeUrl(edInput.text.toString().trim())
                        ) {
                            btnCreated.isEnabled = false
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                        } else {
                            btnCreated.isEnabled = true
                            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                        }
                    } else {
                        if (edInput.text.toString()
                                .trim() == ""
                        ) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate(text: String) {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        val intent = Intent(activity, QrResultActivity::class.java)
        val bundle = Bundle()
        mBinding?.apply {
            bundle.putString("TYPE", TypeResult.YOUTUBE)
            bundle.putInt("typeResult", typeResult)
            bundle.putString("result", text)
            bundle.putString("time", time)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun isValidYouTubeUrl(url: String): Boolean {
        val youtubeUrlRegex =
            """^(https?://)?(www\.)?(m\.)?youtube\.com/(channel/|user/|c/|v/|embed/|watch\?v=|watch\?feature=youtu.be&v=|watch\?feature=player_embedded&v=)([a-zA-Z0-9_-]{1,})$""".toRegex()
        mBinding?.apply {
            return if (youtubeUrlRegex.matches(url)) {
                edInput.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinate.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            } else {
                edInput.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinate.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
        return false
    }

    companion object {
        const val VIDEO = 0
        const val URL = 1
        const val CHANNEL = 2
    }

}