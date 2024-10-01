package com.example.qrscan.ui.fragment.create.email

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputEmailBinding
import com.example.qrscan.extension.invisible
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.util.TypeResult
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class InputEmailFragment : BaseFragment<FragmentInputEmailBinding>() {
    override fun getLayout(): Int = R.layout.fragment_input_email

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
            ivBack.setOnSingleClickListener {
                requireActivity().finish()
            }
            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edtEmail.text.toString()
                            .trim() == "" || !isValidEmail(edtEmail.text.toString().trim())
                    ) {
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
            btnCreated.setOnSingleClickListener {
                handleOnClickBtnCreate(edtEmail.text.toString().trim())
            }
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    fun isValidEmail(email: String): Boolean {
        val emailRegex = """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$""".toRegex()
        mBinding?.apply {
            return if (emailRegex.matches(email)) {
                edtEmail.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinate.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            } else {
                edtEmail.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinate.visible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate(email: String) {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "invalid email", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(activity, QrResultActivity::class.java)
            val bundle = Bundle()
            mBinding?.apply {
                bundle.putString("TYPE", TypeResult.EMAIL)
                bundle.putString("EMAIL", email)
                bundle.putString("time", time)
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

}
