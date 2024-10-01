package com.example.qrscan.ui.fragment.create.phone

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputPhoneBinding
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


class InputCreatePhoneFragment : BaseFragment<FragmentInputPhoneBinding>() {
    private val CONTACT_PERMISSION_CODE = 1
    override fun getLayout(): Int = R.layout.fragment_input_phone

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.apply {
            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            btnCreated.isEnabled = false
            edtPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edtPhone.text.toString().trim() == ""||!isValidPhoneNumber(edtPhone.text.toString().trim())) {
                        btnCreated.isEnabled = false
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                    } else {
                        btnCreated.isEnabled = true
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
            ivBack.setOnSingleClickListener { requireActivity().finish() }
            btnCreated.setOnSingleClickListener {
                handleOnClickBtnCreate(
                    edtPhone.text.toString().trim()
                )
            }
            btnAddPhone.setOnSingleClickListener {
                if (checkContactPermission()) {
                    pickContact()
                } else {
                    requestContactPermission()
                }
            }
        }
    }

    private fun checkContactPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactPermission() {
        val permission = arrayOf(Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(requireActivity(), permission, CONTACT_PERMISSION_CODE)
    }

    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, 111)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContact()
            } else {
                Toast.makeText(requireContext(), "permission denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            var contactUri = data?.data ?: return
            var cols = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
            )
            var rs = activity?.contentResolver?.query(contactUri, cols, null, null, null)
            if (rs?.moveToFirst()!!) {
                mBinding?.edtPhone?.setText(rs.getString(0))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate(phone: String) {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(activity, getText(R.string.txt_invalid_phone), Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(activity, QrResultActivity::class.java)
            val bundle = Bundle()
            mBinding?.apply {
                bundle.putString("TYPE", TypeResult.PHONE)
                bundle.putString("PHONE", phone)
                bundle.putString("time", time)
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        mBinding?.apply {
            return if (Patterns.PHONE.matcher(phoneNumber).matches()){
                edtPhone.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinate.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            }else{
                edtPhone.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinate.visible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
      return false
    }

}