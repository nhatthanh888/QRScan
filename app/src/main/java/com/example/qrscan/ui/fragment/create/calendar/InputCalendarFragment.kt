@file:Suppress("DEPRECATION")

package com.example.qrscan.ui.fragment.create.calendar

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputCalendarBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.util.TypeResult
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class InputCalendarFragment : BaseFragment<FragmentInputCalendarBinding>() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)

    override fun getLayout(): Int = R.layout.fragment_input_calendar

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNotEmptyText()
        mBinding?.apply {
            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            btnCreated.isEnabled = false
            ivBack.setOnSingleClickListener { requireActivity().finish() }
            btnStartdate.setOnSingleClickListener {
                setupDatepicker(tvStartDate)
            }
            btnEnddate.setOnSingleClickListener {
                setupDatepicker(tvEndDate)
            }
            btnCreated.setOnSingleClickListener {
                val isValidated =
                    validateTime(tvStartDate.text.toString(), tvEndDate.text.toString())
                if (isValidated) {
                    sendCalendarToGenrate()
                }
            }
            edtEvent.addTextChangedListener(object : TextWatcher {
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
            edtAddress.addTextChangedListener(object : TextWatcher {
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
            tvStartDate.addTextChangedListener(object : TextWatcher {
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
            tvEndDate.addTextChangedListener(object : TextWatcher {
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
        }


    }

    private fun setupDatepicker(textView: TextView) {
        val calendarStartDate = Calendar.getInstance()

        val datePickerStartDate =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendarStartDate.set(Calendar.YEAR, year)
                calendarStartDate.set(Calendar.MONTH, month)
                calendarStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker(dateResult(calendarStartDate), textView)
            }
        val datePicker = DatePickerDialog(
            requireContext(), R.style.DatePickerColor,
            datePickerStartDate,
            calendarStartDate.get(Calendar.YEAR),
            calendarStartDate.get(Calendar.MONTH),
            calendarStartDate.get(Calendar.MONTH)
        )
        datePicker.show()
        datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.color_blue))
        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.color_blue))
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateResult(calendarStartDate: Calendar): String {
        val day = SimpleDateFormat("dd", Locale.US).format(calendarStartDate.time)
        val month = SimpleDateFormat("MMM", Locale.US).format(calendarStartDate.time)
        val year = SimpleDateFormat("yyyy", Locale.US).format(calendarStartDate.time)
        return "$day $month $year"
    }

    @SuppressLint("SimpleDateFormat")
    private fun showTimePicker(dateResult: String, textView: TextView) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            val timeResult = dateResult + " at " + SimpleDateFormat("HH:mm").format(cal.time)
            textView.text = timeResult
        }
        val timePicker = TimePickerDialog(
            requireContext(), R.style.DatePickerColor,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
        timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.color_blue))
        timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.color_blue))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendCalendarToGenrate() {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        val intent = Intent(activity, QrResultActivity::class.java)
        val bundle = Bundle()
        mBinding?.apply {
            bundle.putString("TYPE", TypeResult.CALENDAR)
            bundle.putString("EVENT_TITLE", edtEvent.text.toString())
            bundle.putString("ADDRESS", edtAddress.text.toString())
            bundle.putString("START_DATE", tvStartDate.text.toString())
            bundle.putString("END_DATE", tvEndDate.text.toString())
            bundle.putString("WEBSITE", edLinkWeb.text.toString())
            bundle.putString("NOTE", edtNote.text.toString())
            bundle.putString("time", time)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateTime(startDate: String, endDate: String): Boolean {

        val simpleDateformat = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.US)
        val startDateTime = LocalDateTime.parse(startDate.replace(" at ", " "), simpleDateformat)
        val endDateTime = LocalDateTime.parse(endDate.replace(" at ", " "), simpleDateformat)
        return if (startDateTime.isAfter(endDateTime)) {
            Toast.makeText(requireContext(), "End time must after start time", Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            true
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun checkNotEmptyText() {
        mBinding?.apply {
            val textEvent = edtEvent.text.toString().trim()
            val textaddress = edtAddress.text.toString().trim()
            val textStartDate = tvStartDate.text.toString().trim()
            val textEndDate = tvStartDate.text.toString().trim()
            if (textEvent == "" || textaddress == "" || textStartDate == "" || textEndDate == "") {
                btnCreated.isEnabled = false
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            } else {
                btnCreated.isEnabled = true
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
            }
        }
    }
}