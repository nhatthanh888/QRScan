package com.example.qrscan.data.model

import java.io.Serializable

data class QRCreateRequest(val content: String, val typeCreate: TypeQr) : Serializable