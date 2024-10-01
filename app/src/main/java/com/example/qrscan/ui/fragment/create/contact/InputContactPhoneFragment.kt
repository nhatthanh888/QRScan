package com.example.qrscan.ui.fragment.create.contact

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.webkit.URLUtil
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputContactBinding
import com.example.qrscan.extension.invisible
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.ui.fragment.create.create_model.ContactModel
import com.example.qrscan.util.TypeResult
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class InputContactPhoneFragment : BaseFragment<FragmentInputContactBinding>() {
    private val CONTACT_PERMISSION_CODE = 1
    override fun getLayout(): Int = R.layout.fragment_input_contact

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
            ivBack.setOnSingleClickListener { requireActivity().finish() }
            btnAddPhone.setOnSingleClickListener {
                if (checkContactPermission()) {
                    pickContact()
                } else {
                    requestContactPermission()
                }
            }
            edtName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkNotEmptyText()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            edtPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkNotEmptyText()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkNotEmptyText()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            edtBirthday.setOnSingleClickListener { setupDatepicker(edtBirthday) }
            btnCreated.setOnSingleClickListener {
                val contactModel = ContactModel(
                    edtName.text.toString(),
                    edtPhone.text.toString(),
                    edtEmail.text.toString(),
                    edtUrl.text.toString(),
                    edtNote.text.toString(),
                    edtBirthday.text.toString(),
                    edtNickname.text.toString(),
                    edtAddress.text.toString()
                )
                handleOnClickBtnCreate(contactModel)
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
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            var rs = activity?.contentResolver?.query(contactUri, cols, null, null, null)
            if (rs?.moveToFirst()!!) {
                mBinding?.edtName?.setText(rs.getString(0))
                mBinding?.edtPhone?.setText(rs.getString(1))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate(contact: ContactModel) {
        if (checkInputValidate(contact.email, contact.phone, contact.url)) {
            sendContactToGenrate(contact)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun checkNotEmptyText() {
        mBinding?.apply {
            val textPhone = edtPhone.text.toString().trim()
            val textName = edtName.text.toString().trim()
            val textEmail = edtEmail.text.toString().trim()
            if (textPhone == "" || textName == "" || textEmail == "") {
                btnCreated.isEnabled = false
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            } else {
                btnCreated.isEnabled = true
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
            }
        }
    }

    private fun checkInputValidate(email: String, phoneNumber: String, url: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "invalid email", Toast.LENGTH_LONG).show()
            return false
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(requireContext(), "invalid phone number", Toast.LENGTH_LONG).show()
            return false
        }
        if (url != "") {
            if (!Patterns.WEB_URL.matcher(url).matches()) {
                Toast.makeText(activity, getText(R.string.txt_invalid_url), Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        }
        return true
    }

    private fun setupDatepicker(textView: TextView) {
        val calendarBirthday = Calendar.getInstance()

        val datePickerListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendarBirthday.set(Calendar.YEAR, year)
                calendarBirthday.set(Calendar.MONTH, month)
                calendarBirthday.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                textView.text = dateResult(calendarBirthday)
            }
        val datePicker = DatePickerDialog(
            requireContext(), R.style.DatePickerColor,
            datePickerListener,
            calendarBirthday.get(Calendar.YEAR),
            calendarBirthday.get(Calendar.MONTH),
            calendarBirthday.get(Calendar.MONTH)
        )
        datePicker.show()
        datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.color_blue))
        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.color_blue))
    }

    private fun dateResult(calendarDate: Calendar): String {
        val day = SimpleDateFormat("dd").format(calendarDate.time)
        val month = SimpleDateFormat("MM").format(calendarDate.time)
        val year = SimpleDateFormat("yyyy").format(calendarDate.time)
        return "${day}/${month}/${year}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendContactToGenrate(contact: ContactModel) {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        val intent = Intent(activity, QrResultActivity::class.java)
        val bundle = Bundle()
        mBinding?.apply {
            bundle.putString("TYPE", TypeResult.CONTACT)
            bundle.putString("NAME", contact.name)
            bundle.putString("PHONE", contact.phone)
            bundle.putString("EMAIL", contact.email)
            bundle.putString("URL", contact.url)
            bundle.putString("NOTE", contact.note)
            bundle.putString("BIRTHDAY", contact.birthday)
            bundle.putString("NICK_NAME", contact.nickname)
            bundle.putString("ADDRESS", contact.address)
            bundle.putString("time", time)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$""".toRegex()
        mBinding?.apply {
            return if (emailRegex.matches(email)) {
                edtEmail.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinateEmail.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            } else {
                edtEmail.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinateEmail.visible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
        return false
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun isValidUrl(url: String): Boolean {
        mBinding?.apply {
            return try {
                URL(url)
                edtUrl.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinateUrl.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            } catch (e: Exception) {
                edtUrl.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinateUrl.visible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
        return false
    }

    fun isValidPhoneNumber(target: CharSequence?): Boolean {
        return if (target == null || target.length < 6 || target.length > 13) {
            false
        } else {
            Patterns.PHONE.matcher(target).matches()
        }
    }
}