package com.example.qrscan.util

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.qrscan.data.model.Location
import com.example.qrscan.schema.Facebook
import com.example.qrscan.schema.Instagram
import com.example.qrscan.schema.Tiktok
import com.example.qrscan.schema.Twitter
import com.example.qrscan.schema.VCard
import com.example.qrscan.schema.Wifi
import com.example.qrscan.schema.VEvent
import com.example.qrscan.schema.Youtube
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object HandleTypeResultScan {

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToTime(time: String): String {
        val dateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX"))
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.US))
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(inputDateString: String): String {
        val inputFormat = SimpleDateFormat("dd MMM yyyy HH:mm")
        val outputFormat = SimpleDateFormat("dd MMM yyyy 'at' HH:mm")
        val date: Date = inputFormat.parse(inputDateString)!!
        return outputFormat.format(date)
    }

    fun handleResultEmail(result: String): String {
        val emailPattern = Regex("MATMSG:TO:(.*?);;")
        val matchResult = emailPattern.find(result)
        val emailAddress = matchResult?.groupValues?.get(1)
        return if (!emailAddress.isNullOrBlank()) {
            emailAddress
        } else {
            result
        }
    }

    fun handleResultPhone(result: String): String {
        val regex = Regex("""tel:(\d+)""")
        val matchResult = regex.find(result)
        val phone = matchResult?.groupValues?.get(1)
        return phone.toString()
    }

    fun handleResultBarcode(inputString: String): String {
        val regex = Regex("""(\d+)""")
        val matchResult = regex.find(inputString)
        val result = matchResult?.groupValues?.get(1)
        return result!!
    }

    fun handleResultContact(result: String): VCard {
        val inputString = result.trimIndent()

        val nameRegex = Regex("""N:;([^;\n]+)""")
        val nicknameRegex = Regex("""NICKNAME:([^;\n]+)""")
        val emailRegex = Regex("""EMAIL:([^;\n]+)""")
        val telRegex = Regex("""TEL:([^;\n]+)""")
        val urlRegex = Regex("""URL:([^;\n]+)""")
        val noteRegex = Regex("""NOTE:([^;\n]+)""")
        val bdayRegex = Regex("""BDAY;VALUE=text:([^;\n]+)""")

        val nameMatch = nameRegex.find(inputString)
        val nicknameMatch = nicknameRegex.find(inputString)
        val emailMatch = emailRegex.find(inputString)
        val telMatch = telRegex.find(inputString)
        val urlMatches = urlRegex.find(inputString)
        val noteMatch = noteRegex.find(inputString)
        val bdayMatch = bdayRegex.find(inputString)

        val name = nameMatch?.groupValues?.get(1)?.trim()
        val email = emailMatch?.groupValues?.get(1)?.trim()
        val tel = telMatch?.groupValues?.get(1)?.trim()
        val nickname = nicknameMatch?.groupValues?.get(1)?.trim()
        val urls = urlMatches?.groupValues?.get(1)?.trim()
        val note = noteMatch?.groupValues?.get(1)?.trim()
        val bday = bdayMatch?.groupValues?.get(1)?.trim()

        val vCard = VCard()
        vCard.firstName = name.toString()
        vCard.phone = tel.toString()
        vCard.email = email.toString()
        if (nickname != null) {
            vCard.nickname = nickname.toString()
        }
        if (urls != null) {
            vCard.url = urls.toString()
        }
        if (note != null) {
            vCard.note = note.toString()
        }
        if (bday != null) {
            vCard.birthday = bday.toString()
        }

        return vCard
    }

    fun handleResultCalendar(result: String): VEvent {
        val inputString = result.trimIndent()
        val evenTitleRegex = Regex("""UID:([^;\n]+)""")
        val locationRegex = Regex("""DTSTAMP:([^ \n]+)""")
        val timeStartRegex = Regex("""DTSTART:([^;\n]+)""")
        val timeEndRegex = Regex("""DTEND:([^;\n]+)""")
        val noteRegex = Regex("""SUMMARY:([^;\n]+)""")

        val evenTitleMatch = evenTitleRegex.find(inputString)
        val locationMatch = locationRegex.find(inputString)
        val timeStartMatch = timeStartRegex.find(inputString)
        val timeEndMatch = timeEndRegex.find(inputString)
        val noteMatch = noteRegex.find(inputString)

        val evenTitles = evenTitleMatch?.groupValues?.get(1)?.trim()
        val location = locationMatch?.groupValues?.get(1)
        val timeStarts = timeStartMatch?.groupValues?.get(1)?.trim()
        val timeEnds = timeEndMatch?.groupValues?.get(1)?.trim()
        val notes = noteMatch?.groupValues?.get(1)?.trim()
        val vEvent = VEvent()
        vEvent.uid = evenTitles.toString()
        vEvent.startDateStr = timeStarts.toString()
        vEvent.endDateStr = timeEnds.toString()
        vEvent.stamp = location.toString()
        if (notes != null) {
            vEvent.summary = notes
        }
        return vEvent
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateToLong(date: String): Long {
        val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US)
        val date = sdf.parse(date)
        return date!!.time
    }

    fun handleResultLocation(inputString: String): Location {
        val regex = Regex("""geo:([^,]+),([^,]+),([^,]+)""")
        val matchResult = regex.find(inputString)

        val name = matchResult?.groupValues?.get(1)
        val latitude = matchResult?.groupValues?.get(2)
        val longitude = matchResult?.groupValues?.get(3)

        return Location(name!!, latitude!!, longitude!!)
    }

    fun handleResultFaceBook(inputString: String): String {
        return if (inputString.startsWith(Facebook.FACEBOOK_ID_PREFIX)) {
            val regex = Regex("""fb://profile/(\d+)""")
            val matchResult = regex.find(inputString)
            val result = matchResult?.groupValues?.get(1)
            result!!
        } else {
            inputString
        }
    }

    fun handleResultInstagram(inputString: String): String {
        return if (inputString.startsWith(Instagram.USERNAME_PREFIX)) {
            val regex = Regex("""username=([^&]+)""")
            val matchResult = regex.find(inputString)
            val result = matchResult?.groupValues?.get(1)
            result!!
        } else {
            inputString
        }
    }

    fun handleResultTwitter(inputString: String): String {
        return if (inputString.startsWith(Twitter.USERNAME_PREFIX)) {
            val regex = Regex("""screen_name=([^&]+)""")
            val matchResult = regex.find(inputString)
            val result = matchResult?.groupValues?.get(1)
            result!!
        } else {
            inputString
        }

    }


    fun handleResultTiktok(inputString: String, paramName: String): String {
        val paramStartIndex = inputString.indexOf("$paramName=")
        return if (inputString.startsWith(Tiktok.ID_PREFIX)) {
            if (paramStartIndex != -1) {
                val valueStartIndex = paramStartIndex + paramName.length + 1
                val paramEndIndex = inputString.indexOf('&', valueStartIndex)
                val paramValueEndIndex =
                    if (paramEndIndex != -1) paramEndIndex else inputString.length
                inputString.substring(valueStartIndex, paramValueEndIndex)
            } else {
                inputString
            }
        } else {
            inputString
        }
    }


    fun handleResultWifi(inputString: String): Wifi {
        val securityRegex = Regex("""WIFI:T:([^;]+);""")
        val ssidRegex = Regex("""S:([^;]+);""")
        val passwordRegex = Regex("""P:([^;]+);""")
        val securityMatch = securityRegex.find(inputString)
        val ssidMatch = ssidRegex.find(inputString)
        val passwordMatch = passwordRegex.find(inputString)
        val type = securityMatch?.groupValues?.get(1)
        val name = ssidMatch?.groupValues?.get(1)
        val password = passwordMatch?.groupValues?.get(1)

        val wifi = Wifi()
        wifi.name = name.toString()
        wifi.encryption = type.toString()
        if (password != null) {
            wifi.password = password.toString()
        }
        return wifi
    }

    fun handResultYoutube(inputString:String):String{
        return if (inputString.startsWith(Youtube.VIDEO_ID_PREFIX)){
            inputString.substringAfter("ytVideoID:")
        }else if (inputString.startsWith(Youtube.CHANNEL_ID_PREFIX)){
            inputString.substringAfter("ytChannelID:")
        }else{
            inputString
        }
    }

}