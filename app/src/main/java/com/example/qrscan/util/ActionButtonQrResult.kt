package com.example.qrscan.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.provider.ContactsContract
import android.provider.Settings
import com.example.qrscan.schema.VCard
import com.example.qrscan.schema.VEvent


object ActionButtonQrResult {
    fun openWebsite(activity: Activity, webUrl: String) {
        val webpage = Uri.parse(webUrl)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        activity.startActivity(intent)
    }

    fun dialPhoneNumber(activity: Activity, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        activity.startActivity(intent)
    }

    fun sendEmail(activity: Activity, email: String) {
        val TO_EMAIL = arrayOf(email)
        val intent = Intent(Intent.ACTION_SEND)
        intent.setData(Uri.parse("mailto:"))
        intent.putExtra(Intent.EXTRA_EMAIL, TO_EMAIL)
        intent.setType("message/rfc822")
        activity.startActivity(Intent.createChooser(intent, "Choose an email client"))
    }

    fun searchInGoogle(activity: Activity, keyWord: String) {
        val uri = Uri.parse("http://www.google.com/search?q=$keyWord")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
    }

    fun addToContacts(activity: Activity, vCard: VCard) {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            val fullName = vCard.firstName.orEmpty()
            putExtra(ContactsContract.Intents.Insert.NAME, fullName)
            putExtra(ContactsContract.Intents.Insert.PHONE, vCard.phone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.NOTES, vCard.note.orEmpty())
        }
        activity.startActivity(intent)
    }

    fun addToEvent(activity: Activity, vEvent: VEvent) {
        if (Build.VERSION.SDK_INT >= 14) {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.setData(Events.CONTENT_URI)
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, vEvent.startDate)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, vEvent.endDate)
                .putExtra(Events.TITLE, vEvent.uid)
                .putExtra(Events.DESCRIPTION, vEvent.summary)
                .putExtra(Events.EVENT_LOCATION, vEvent.location)
            activity.startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_EDIT)
            intent.type = "vnd.android.cursor.item/event"
            intent.putExtra("beginTime", vEvent.startDate)
            intent.putExtra("allDay", true)
            intent.putExtra("rrule", "FREQ=YEARLY")
            intent.putExtra("endTime", vEvent.endDate?.plus(60 * 60 * 1000))
            intent.putExtra("title", vEvent.uid)
            activity.startActivity(intent)
        }
    }

    fun goToWifiSetting(activity: Activity) {
        activity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
    }
}