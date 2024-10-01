package com.example.qrscan.ui.fragment.create.web

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.data.model.ResultScanModel
import com.example.qrscan.databinding.FragmentInputCreateWebBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.ui.fragment.scan.FragmentScan
import com.example.qrscan.util.TypeResult
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class InputCreateWebFragment : BaseFragment<FragmentInputCreateWebBinding>() {
    override fun getLayout(): Int = R.layout.fragment_input_create_web
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.apply {
            btnCreated.isEnabled = false
            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            ivBack.setOnSingleClickListener {
                requireActivity().finish()
            }
            edLinkWeb.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edLinkWeb.text.toString().trim() == "") {
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
            layoutOptionWeb.apply {
                btnTextHttps.setOnSingleClickListener {
                    edLinkWeb.setText("https://")
                    btnTextHttps.setBackgroundResource(R.drawable.custom_button_blue)
                    btnTextHttps.setTextColor(Color.WHITE)
                    btnTextHttp.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextHttp.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                    btnTextWww.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextWww.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                    btnTextCom.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextCom.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                }

                btnTextCom.setOnSingleClickListener {
                    edLinkWeb.text?.append(".com")
                    btnTextCom.setBackgroundResource(R.drawable.custom_button_blue)
                    btnTextCom.setTextColor(Color.WHITE)
                    btnTextWww.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextWww.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                    btnTextHttps.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextHttps.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                    btnTextHttp.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextHttp.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                }

                btnTextWww.setOnSingleClickListener {
                    edLinkWeb.setText("www.")
                    btnTextWww.setBackgroundResource(R.drawable.custom_button_blue)
                    btnTextWww.setTextColor(Color.WHITE)
                    btnTextHttp.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextHttp.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                    btnTextHttps.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextHttps.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                    btnTextCom.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextCom.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                }
                btnTextHttp.setOnSingleClickListener {
                    edLinkWeb.setText("http://")
                    btnTextHttp.setBackgroundResource(R.drawable.custom_button_blue)
                    btnTextHttp.setTextColor(Color.WHITE)
                    btnTextWww.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextWww.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                    btnTextHttps.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextHttps.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                    btnTextCom.setBackgroundResource(R.drawable.custom_button_unselect)
                    btnTextCom.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bottom_nav_unselect
                        )
                    )
                }
            }

            btnCreated.setOnSingleClickListener {
                handleOnClickBtnCreate(edLinkWeb.text.toString().trim())
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate(url: String) {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            Toast.makeText(activity, getText(R.string.txt_invalid_url), Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(activity, QrResultActivity::class.java)
            val bundle = Bundle()
            mBinding?.apply {
                bundle.putString("TYPE", TypeResult.WEB)
                bundle.putString("URL", url)
                bundle.putString("time", time)
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

}