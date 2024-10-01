package com.example.qrscan.util

import com.example.qrscan.schema.Barcode
import com.example.qrscan.schema.Email
import com.example.qrscan.schema.Facebook
import com.example.qrscan.schema.Geo
import com.example.qrscan.schema.Instagram
import com.example.qrscan.schema.Phone
import com.example.qrscan.schema.Tiktok
import com.example.qrscan.schema.Twitter
import com.example.qrscan.schema.Url
import com.example.qrscan.schema.VCard
import com.example.qrscan.schema.Whatsapp
import com.example.qrscan.schema.Wifi
import com.example.qrscan.schema.Youtube
import com.example.qrscan.schema.VEvent

object GetTypeCodeScan {

    fun checkTypeResult(result: String): String {
        return if (result.startsWith("MATMSG:TO:") && result.endsWith(
                Email.MATMSG_SEPARATOR
            )
        ) {
            TypeResult.EMAIL
        } else if (result.startsWith(Url.HTTPS_PREFIX) && !(result.contains("www.facebook.com") || result.contains(
                "twitter.com"
            ) || result.contains(
                "www.instagram.com"
            ) || result.contains("www.tiktok.com")||result.contains("www.youtube.com")) || result.startsWith(Url.HTTP_PREFIX) || result.startsWith(
                Url.WWW_PREFIX
            )
        ) {
            TypeResult.WEB
        } else if (result.startsWith(Phone.PREFIX)) {
            TypeResult.PHONE
        } else if (result.startsWith(VCard.SCHEMA_PREFIX) && result.endsWith(VCard.END_PREFIX)) {
            TypeResult.CONTACT
        } else if (result.startsWith(Wifi.FORMAT_TYPE_FREE) || result.startsWith(Wifi.FORMAT_TYPE_WPA) || result.startsWith(
                Wifi.FORMAT_TYPE_WEP
            )
        ) {
            TypeResult.WIFI
        } else if (result.startsWith(VEvent.SCHEMA_PREFIX) && result.endsWith(VEvent.SCHEMA_SUFFIX)) {
            TypeResult.CALENDAR
        } else if (result.startsWith(Geo.PREFIX)) {
            TypeResult.LOCATION
        } else if (result.startsWith(Facebook.PROFILE_URL_PREFIX) && result.contains("www.facebook.com") || result.startsWith(
                Facebook.FACEBOOK_ID_PREFIX
            )
        ) {
            TypeResult.FB
        } else if (result.startsWith(Instagram.URL_PREFIX) && result.contains("www.instagram.com") || result.startsWith(
                Instagram.USERNAME_PREFIX
            )
        ) {
            TypeResult.IG
        } else if (result.startsWith(Twitter.URL_PREFIX) && result.contains("twitter.com") || result.startsWith(
                Twitter.USERNAME_PREFIX
            )
        ) {
            TypeResult.TWITTER
        } else if (result.startsWith(Tiktok.URL_PREFIX) && result.contains("www.tiktok.com") || result.startsWith(
                Tiktok.ID_PREFIX
            )
        ) {
            TypeResult.TIKTOK
        } else if (result.startsWith(Youtube.VIDEO_URL_PREFIX) && result.contains("www.youtube.com") || result.startsWith(
                Youtube.CHANNEL_URL_PREFIX
            ) || result.startsWith(
                Youtube.VIDEO_ID_PREFIX
            ) || result.startsWith(Youtube.CHANNEL_ID_PREFIX)
        ) {
            TypeResult.YOUTUBE
        } else if (result.startsWith(Whatsapp.PHONE_PREFIX)) {
            TypeResult.WHATSAPP
        } else if (result.startsWith(Barcode.BARCODE)) {
            TypeResult.BARCODE
        } else {
            TypeResult.TEXT
        }
    }

    fun getTypeEmail(result: String): String {
        var type = ""
        if (result.startsWith("${Email.MATMSG_SCHEMA_PREFIX} ${Email.MATMSG_EMAIL_PREFIX}") && result.endsWith(
                Email.MATMSG_SEPARATOR
            )
        ) {
            type = TypeResult.EMAIL
        }
        return type
    }

    fun getTypeWeb(result: String): String {
        var type = ""
        if (result.startsWith(Url.HTTPS_PREFIX) || result.startsWith(Url.HTTP_PREFIX) || result.startsWith(
                Url.WWW_PREFIX
            )
        ) {
            type = TypeResult.WEB
        }
        return type
    }

    fun getTypePhone(result: String): String {
        var type = ""
        if (result.startsWith(Phone.PREFIX)) {
            type = TypeResult.PHONE
        }
        return type
    }

    fun getTypeContact(result: String): String {
        var type = ""
        if (result.startsWith(VCard.SCHEMA_PREFIX) && result.endsWith(VCard.END_PREFIX)) {
            type = TypeResult.CONTACT
        }
        return type
    }

    fun getTypeWifi(result: String): String {
        var type = ""
        if (result.startsWith(Wifi.FORMAT_TYPE_FREE) || result.startsWith(Wifi.FORMAT_TYPE_WPA) || result.startsWith(
                Wifi.FORMAT_TYPE_WEP
            )
        ) {
            type = TypeResult.WIFI
        }
        return type
    }

    fun getTypeCalendar(result: String): String {
        var type = ""
        if (result.startsWith(VEvent.SCHEMA_PREFIX) && result.endsWith(VEvent.SCHEMA_SUFFIX)) {
            type = TypeResult.CALENDAR
        }
        return type
    }

    fun getTypeLocation(result: String): String {
        var type = ""
        if (result.startsWith(Geo.PREFIX)) {
            type = TypeResult.LOCATION
        }
        return type
    }

    fun getTypeFacebook(result: String): String {
        var type = ""
        if (result.startsWith(Facebook.FACEBOOK_ID_PREFIX) || result.startsWith(Facebook.PROFILE_URL_PREFIX)) {
            type = TypeResult.FB
        }
        return type
    }

    fun getTypeIG(result: String): String {
        var type = ""
        if (result.startsWith(Instagram.USERNAME_PREFIX)) {
            type = TypeResult.IG
        }
        return type
    }

    fun getTypeTwitter(result: String): String {
        var type = ""
        if (result.startsWith(Twitter.USERNAME_PREFIX)) {
            type = TypeResult.TWITTER
        }
        return type
    }

    fun getTypeTikTok(result: String): String {
        var type = ""
        if (result.startsWith(Tiktok.ID_PREFIX)) {
            type = TypeResult.TIKTOK
        }
        return type
    }

//    fun getTypeYoutube(result: String): String {
//        var type = ""
//        if (result.startsWith(Youtube.PREFIXES[0]) || result.startsWith(Youtube.PREFIXES[1]) || result.startsWith(
//                Youtube.PREFIXES[2]
//            )
//        ) {
//            type = TypeResult.YOUTUBE
//        }
//        return type
//    }

    fun getTypeWhatsapp(result: String): String {
        var type = ""
        if (result.startsWith(Whatsapp.PHONE_PREFIX)) {
            type = TypeResult.WHATSAPP
        }
        return type
    }

}