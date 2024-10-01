package com.example.qrscan.schema

import android.net.Uri
import com.example.qrscan.extension.joinToStringNotNullOrBlank
import com.example.qrscan.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.example.qrscan.extension.startsWithIgnoreCase
import ezvcard.Ezvcard
import ezvcard.VCardVersion
import ezvcard.property.*
import ezvcard.property.Email
import ezvcard.property.Url


data class VCard(
    var firstName: String = "",
    var nickname: String = "",
    var email: String = "",
    var phone: String = "",
    var address: String = "",
    var url: String = "",
    var note: String = "",
    var birthday: String = ""
) : Schema {

    companion object {
        const val SCHEMA_PREFIX = "BEGIN:VCARD"
        const val END_PREFIX = "END:VCARD"
        private const val ADDRESS_SEPARATOR = ","

        fun parse(text: String): VCard? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            val vCard = Ezvcard.parse(text).first() ?: return null
            val firstName = vCard.structuredName?.given
            val nickname = vCard.nickname?.values?.firstOrNull()
            val url = vCard.urls?.firstOrNull()?.value
            val note = vCard.notes?.firstOrNull()?.value
            val birthday = vCard.birthdays?.firstOrNull().toString()
            var email: String? = null
            var phone: String? = null
            var address: String? = null

            vCard.emails?.getOrNull(0)?.apply {
                email = value

            }


            vCard.telephoneNumbers?.getOrNull(0)?.apply {
                phone = this.text
            }

            vCard.addresses.firstOrNull()?.apply {
                address = listOf(
                    country,
                    postalCode,
                    region,
                    locality,
                    streetAddress
                ).joinToStringNotNullOrBlank(ADDRESS_SEPARATOR)
            }

            return firstName?.let {
                nickname?.let { it1 ->
                    email?.let { it2 ->
                        phone?.let { it3 ->
                            address?.let { it4 ->
                                url?.let { it5 ->
                                    note?.let { it6 ->
                                        VCard(
                                            it,
                                            it1,
                                            it2,
                                            it3,
                                            it4,
                                            it5,
                                            it6,
                                            birthday
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override val schema = BarcodeSchema.VCARD

    override fun toFormattedText(): String {
        return listOf(
            firstName.orEmpty(),
            nickname,
            phone.orEmpty(),
            email.orEmpty(),
            address,
            url,
            note,
            birthday
        ).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        val vCard = ezvcard.VCard()
        vCard.structuredName = StructuredName().apply {
            given = firstName
        }
        if (nickname.isNullOrBlank().not()) {
            vCard.nickname = Nickname().apply { values.add(nickname) }
        }
        if (email.isNullOrBlank().not()) {
            vCard.addEmail(Email(email))
        }
        if (phone.isNullOrBlank().not()) {
            vCard.addTelephoneNumber(Telephone(phone))
        }
        if (url.isNullOrBlank().not()) {
            vCard.addUrl(Url(url))
        }
        if (note.isNullOrBlank().not()) {
            vCard.addNote(Note(note))
        }
        if (birthday.isNullOrBlank().not()) {
            vCard.birthday = Birthday(birthday)
        }

        return Ezvcard
            .write(vCard)
            .version(VCardVersion.V4_0)
            .prodId(false)
            .go()
            .trimEnd('\n', '\r', ' ')
    }
}