package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

data class Facebook(
    val facebookId: String? = null,
    val profileUrl: String? = null
) : Schema {

    companion object {
        const val FACEBOOK_ID_PREFIX = "fb://profile/"
        const val PROFILE_URL_PREFIX = "https://www.facebook.com/"

        fun parse(text: String): Facebook? {
            return when {
                text.startsWithIgnoreCase(FACEBOOK_ID_PREFIX) -> parseAsId(text)
                text.startsWithIgnoreCase(PROFILE_URL_PREFIX) -> parseAsUrl(text)
                else -> null
            }
        }

        private fun parseAsId(text: String): Facebook {
            val facebookId = text.removePrefixIgnoreCase(FACEBOOK_ID_PREFIX)
            return Facebook(facebookId = facebookId)
        }

        private fun parseAsUrl(text: String): Facebook {
            val profileUrl = text.removePrefixIgnoreCase(PROFILE_URL_PREFIX)
            return Facebook( profileUrl = profileUrl)
        }

    }

    override val schema = BarcodeSchema.FACEBOOK

    override fun toFormattedText(): String {
        return if (profileUrl!=null){
            listOf("$profileUrl")
                .filter { it.isNotBlank() }
                .joinToString("\n")
        }else{
            listOf("$facebookId")
                .filter { it.isNotBlank() }
                .joinToString("\n")
        }

    }

    override fun toBarcodeText(): String {
        return if (profileUrl!=null){
            "$profileUrl"
        }else{
            "$FACEBOOK_ID_PREFIX$facebookId"
        }
    }
}
