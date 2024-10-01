package com.example.qrscan.schema

import com.example.qrscan.extension.removePrefixIgnoreCase
import com.example.qrscan.extension.startsWithIgnoreCase

data class Youtube(
    val videoId: String? = null,
    val videoUrl: String? = null,
    val channelId: String? = null
) : Schema {

    companion object {
        const val VIDEO_ID_PREFIX = "ytVideoID:"
        const val VIDEO_URL_PREFIX = "https://www.youtube.com/"
        const val CHANNEL_ID_PREFIX = "ytChannelID:"
        const val CHANNEL_URL_PREFIX = "https://www.youtube.com/channel/"

        fun parse(text: String): Youtube? {
            return when {
                text.startsWithIgnoreCase(VIDEO_ID_PREFIX) -> parseAsVideoId(text)
                text.startsWithIgnoreCase(VIDEO_URL_PREFIX) -> parseAsVideoUrl(text)
                text.startsWithIgnoreCase(CHANNEL_ID_PREFIX) -> parseAsChannelId(text)
                text.startsWithIgnoreCase(CHANNEL_URL_PREFIX) -> parseAsChannelUrl(text)
                else -> null
            }
        }

        private fun parseAsVideoId(text: String): Youtube {
            val videoId = text.removePrefixIgnoreCase(VIDEO_ID_PREFIX)
            return Youtube(videoId = videoId)
        }

        private fun parseAsVideoUrl(text: String): Youtube {
            val videoUrl = extractVideoIdFromUrl(text)
            return Youtube(videoUrl = videoUrl)
        }

        private fun parseAsChannelId(text: String): Youtube {
            val channelId = text.removePrefixIgnoreCase(CHANNEL_ID_PREFIX)
            return Youtube(channelId = channelId)
        }

        private fun parseAsChannelUrl(text: String): Youtube {
            val channelId = extractChannelIdFromUrl(text)
            return Youtube(channelId = channelId, videoUrl = text)
        }

        private fun extractVideoIdFromUrl(url: String): String? {
            // Logic để trích xuất Video ID từ URL nếu cần
            // (Chẳng hạn: https://www.youtube.com/watch?v=abcdefghijk)
            // Trả về null nếu không thể trích xuất được Video ID
            return null
        }

        private fun extractChannelIdFromUrl(url: String): String? {
            // Logic để trích xuất Channel ID từ URL nếu cần
            // (Chẳng hạn: https://www.youtube.com/channel/UCxyz1234567890)
            // Trả về null nếu không thể trích xuất được Channel ID
            return null
        }
    }

    override val schema = BarcodeSchema.YOUTUBE

    override fun toFormattedText(): String {
        return if (videoId != null) {
            listOf("$videoId")
                .filter { it.isNotBlank() }
                .joinToString("\n")
        } else if (videoUrl != null) {
            listOf("$videoUrl")
                .filter { it.isNotBlank() }
                .joinToString("\n")
        } else {
            listOf("$channelId")
                .filter { it.isNotBlank() }
                .joinToString("\n")
        }

    }

    override fun toBarcodeText(): String {
        return if (videoId != null) {
            "$VIDEO_ID_PREFIX$videoId"
        } else if (videoUrl != null) {
            "$videoUrl"
        } else {
            "$CHANNEL_ID_PREFIX$channelId"
        }

    }
}
