package com.example.qrscan.ui.activity.create

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrscan.R
import com.example.qrscan.adapter.QrColorAdapter
import com.example.qrscan.base.BaseActivity
import com.example.qrscan.data.model.ColorModel
import com.example.qrscan.databinding.CustomizeQrActivityBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter


class CustomizeQrActivity : BaseActivity<CustomizeQrActivityBinding>() {
    var bundle: Bundle? = null
    private lateinit var arrColor: ArrayList<ColorModel>
    private lateinit var colorAdapter: QrColorAdapter
    var positionChecked = 0
    var bgBlackChecked = false
    private var typeCode = ""

    override fun getContentView(): Int = R.layout.customize_qr_activity

    override fun initView() {
        bundle = intent.extras
        val strRequest = bundle?.getString("QR_CODE")
        typeCode = bundle?.getString("TYPE_CODE").toString()

        if (typeCode == QrResultActivity.QR) {
            generateQrCode(strRequest.toString(), Color.BLACK, Color.WHITE)
        } else {
            generateBarcode(strRequest.toString(), Color.BLACK, Color.WHITE)
        }

        arrColor = ArrayList()
        addListColor()
        colorAdapter = QrColorAdapter(arrColor)
        mDataBinding.rvColor.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mDataBinding.rvColor.adapter = colorAdapter
        colorAdapter.updateData(arrColor)

        mDataBinding.apply {
            btnBgBlack.setOnClickListener {
                setBlackColorBackground(strRequest.toString())
            }
            btnBgWhite.setOnClickListener {
                setWhiteColorBackground(strRequest.toString())
            }
            ivBack.setOnClickListener { finish() }
        }
        colorAdapter.setOnClickListener(object : IClickColorListener {
            override fun onClick(position: Int) {
                colorAdapter.setSelectedColor(position)
                if (typeCode == QrResultActivity.QR) {
                    when (position) {
                        0 -> {
                            if (bgBlackChecked) {
                                generateQrCode(strRequest.toString(), Color.BLACK, Color.BLACK)
                            } else {
                                generateQrCode(strRequest.toString(), Color.BLACK, Color.WHITE)
                            }
                            positionChecked = 0
                        }

                        1 -> {
                            if (bgBlackChecked) {
                                generateQrCode(strRequest.toString(), Color.WHITE, Color.BLACK)
                            } else {
                                generateQrCode(strRequest.toString(), Color.WHITE, Color.WHITE)
                            }
                            positionChecked = 1
                        }

                        2 -> {
                            if (bgBlackChecked) {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_yellow),
                                    Color.BLACK
                                )
                            } else {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_yellow),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 2
                        }

                        3 -> {
                            if (bgBlackChecked) {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_green),
                                    Color.BLACK
                                )
                            } else {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_green),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 3
                        }

                        4 -> {
                            if (bgBlackChecked) {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_blue),
                                    Color.BLACK
                                )
                            } else {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_blue),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 4
                        }

                        5 -> {
                            if (bgBlackChecked) {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_red),
                                    Color.BLACK
                                )
                            } else {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_red),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 5
                        }

                        6 -> {
                            if (bgBlackChecked) {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_purple),
                                    Color.BLACK
                                )
                            } else {
                                generateQrCode(
                                    strRequest.toString(),
                                    getColor(R.color.color_purple),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 6
                        }
                    }
                } else {
                    when (position) {
                        0 -> {
                            if (bgBlackChecked) {
                                generateBarcode(strRequest.toString(), Color.BLACK, Color.BLACK)
                            } else {
                                generateBarcode(strRequest.toString(), Color.BLACK, Color.WHITE)
                            }
                            positionChecked = 0
                        }

                        1 -> {
                            if (bgBlackChecked) {
                                generateBarcode(strRequest.toString(), Color.WHITE, Color.BLACK)
                            } else {
                                generateBarcode(strRequest.toString(), Color.WHITE, Color.WHITE)
                            }
                            positionChecked = 1
                        }

                        2 -> {
                            if (bgBlackChecked) {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_yellow),
                                    Color.BLACK
                                )
                            } else {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_yellow),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 2
                        }

                        3 -> {
                            if (bgBlackChecked) {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_green),
                                    Color.BLACK
                                )
                            } else {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_green),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 3
                        }

                        4 -> {
                            if (bgBlackChecked) {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_blue),
                                    Color.BLACK
                                )
                            } else {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_blue),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 4
                        }

                        5 -> {
                            if (bgBlackChecked) {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_red),
                                    Color.BLACK
                                )
                            } else {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_red),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 5
                        }

                        6 -> {
                            if (bgBlackChecked) {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_purple),
                                    Color.BLACK
                                )
                            } else {
                                generateBarcode(
                                    strRequest.toString(),
                                    getColor(R.color.color_purple),
                                    Color.WHITE
                                )
                            }
                            positionChecked = 6
                        }
                    }
                }

            }
        })
    }

    private fun addListColor() {
        arrColor.apply {
            add(ColorModel(R.drawable.selector_color_black))
            add(ColorModel(R.drawable.selector_white))
            add(ColorModel(R.drawable.selector_yellow))
            add(ColorModel(R.drawable.selector_green))
            add(ColorModel(R.drawable.selector_blue))
            add(ColorModel(R.drawable.selector_red))
            add(ColorModel(R.drawable.selector_purple))
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBlackColorBackground(strRequest: String) {
        mDataBinding.apply {
            btnBgBlack.background = getDrawable(R.drawable.black_selected)
            btnBgWhite.background = getDrawable(R.drawable.rd_white)
            bgBlackChecked = true
            if (typeCode == QrResultActivity.BARCODE) {
                when (positionChecked) {
                    0 -> generateBarcode(strRequest, Color.BLACK, Color.BLACK)
                    1 -> generateBarcode(strRequest, Color.WHITE, Color.BLACK)
                    2 -> generateBarcode(strRequest, getColor(R.color.color_yellow), Color.BLACK)
                    3 -> generateBarcode(strRequest, getColor(R.color.color_green), Color.BLACK)
                    4 -> generateBarcode(strRequest, getColor(R.color.color_blue), Color.BLACK)
                    5 -> generateBarcode(strRequest, getColor(R.color.color_red), Color.BLACK)
                    6 -> generateBarcode(strRequest, getColor(R.color.color_purple), Color.BLACK)
                }
                mDataBinding.btnSave.setOnClickListener {
                    saveCustomQrCode(
                        strRequest,
                        checkColor(positionChecked),
                        Color.BLACK,
                        QrResultActivity.BARCODE
                    )
                }
            } else {
                when (positionChecked) {
                    0 -> generateQrCode(strRequest, Color.BLACK, Color.BLACK)
                    1 -> generateQrCode(strRequest, Color.WHITE, Color.BLACK)
                    2 -> generateQrCode(strRequest, getColor(R.color.color_yellow), Color.BLACK)
                    3 -> generateQrCode(strRequest, getColor(R.color.color_green), Color.BLACK)
                    4 -> generateQrCode(strRequest, getColor(R.color.color_blue), Color.BLACK)
                    5 -> generateQrCode(strRequest, getColor(R.color.color_red), Color.BLACK)
                    6 -> generateQrCode(strRequest, getColor(R.color.color_purple), Color.BLACK)
                }
                mDataBinding.btnSave.setOnClickListener {
                    saveCustomQrCode(
                        strRequest,
                        checkColor(positionChecked),
                        Color.BLACK,
                        QrResultActivity.QR
                    )
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setWhiteColorBackground(strRequest: String) {
        mDataBinding.apply {
            btnBgBlack.background = getDrawable(R.drawable.rd_black)
            btnBgWhite.background = getDrawable(R.drawable.white_selected)
            bgBlackChecked = false
            if (typeCode == QrResultActivity.BARCODE) {
                when (positionChecked) {
                    0 -> generateBarcode(strRequest, Color.BLACK, Color.WHITE)
                    1 -> generateBarcode(strRequest, Color.WHITE, Color.WHITE)
                    2 -> generateBarcode(strRequest, getColor(R.color.color_yellow), Color.WHITE)
                    3 -> generateBarcode(strRequest, getColor(R.color.color_green), Color.WHITE)
                    4 -> generateBarcode(strRequest, getColor(R.color.color_blue), Color.WHITE)
                    5 -> generateBarcode(strRequest, getColor(R.color.color_red), Color.WHITE)
                    6 -> generateBarcode(strRequest, getColor(R.color.color_purple), Color.WHITE)
                }
                mDataBinding.btnSave.setOnClickListener {
                    saveCustomQrCode(
                        strRequest,
                        checkColor(positionChecked),
                        Color.WHITE,
                        QrResultActivity.BARCODE
                    )
                }
            } else {
                when (positionChecked) {
                    0 -> generateQrCode(strRequest, Color.BLACK, Color.WHITE)
                    1 -> generateQrCode(strRequest, Color.WHITE, Color.WHITE)
                    2 -> generateQrCode(strRequest, getColor(R.color.color_yellow), Color.WHITE)
                    3 -> generateQrCode(strRequest, getColor(R.color.color_green), Color.WHITE)
                    4 -> generateQrCode(strRequest, getColor(R.color.color_blue), Color.WHITE)
                    5 -> generateQrCode(strRequest, getColor(R.color.color_red), Color.WHITE)
                    6 -> generateQrCode(strRequest, getColor(R.color.color_purple), Color.WHITE)
                }
                mDataBinding.btnSave.setOnClickListener {
                    saveCustomQrCode(
                        strRequest,
                        checkColor(positionChecked),
                        Color.WHITE,
                        QrResultActivity.QR
                    )
                }
            }

        }

    }

    private fun generateQrCode(strRequest: String, color: Int, colorBackground: Int) {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(
                strRequest,
                BarcodeFormat.QR_CODE,
                256,
                256
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) color else colorBackground)
                }
            }
            mDataBinding.ivResult.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        if (bgBlackChecked) {
            mDataBinding.btnSave.setOnClickListener {
                saveCustomQrCode(
                    strRequest,
                    checkColor(positionChecked),
                    Color.BLACK,
                    QrResultActivity.QR
                )
            }
        } else {
            mDataBinding.btnSave.setOnClickListener {
                saveCustomQrCode(
                    strRequest,
                    checkColor(positionChecked),
                    Color.WHITE,
                    QrResultActivity.QR
                )
            }
        }
    }

    private fun checkColor(position: Int): Int {
        var color = 0
        when (position) {
            0 -> color = Color.BLACK
            1 -> color = Color.WHITE
            2 -> color = getColor(R.color.color_yellow)
            3 -> color = getColor(R.color.color_green)
            4 -> color = getColor(R.color.color_blue)
            5 -> color = getColor(R.color.color_red)
            6 -> color = getColor(R.color.color_purple)
        }
        return color
    }

    private fun generateBarcode(strBarcode: String, color: Int, colorBackground: Int) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(
                strBarcode,
                BarcodeFormat.CODE_128,
                1100,
                400
            )
            val bitmap = Bitmap.createBitmap(
                1100,
                400,
                Bitmap.Config.RGB_565
            )
            for (x in 0 until 1100) {
                for (y in 0 until 400) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) color else colorBackground)
                }
            }
            mDataBinding.ivResult.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        if (bgBlackChecked) {
            mDataBinding.btnSave.setOnClickListener {
                saveCustomQrCode(
                    strBarcode,
                    checkColor(positionChecked),
                    Color.BLACK,
                    QrResultActivity.BARCODE
                )
            }
        } else {
            mDataBinding.btnSave.setOnClickListener {
                saveCustomQrCode(
                    strBarcode,
                    checkColor(positionChecked),
                    Color.WHITE,
                    QrResultActivity.BARCODE
                )
            }
        }
    }

    private fun saveCustomQrCode(
        strRequest: String,
        colorQr: Int,
        backgroundQr: Int,
        typeCode: String
    ) {
        val intent = Intent(this, QrResultActivity::class.java)
        intent.putExtra("QR_COLOR", colorQr)
        intent.putExtra("typeCodeCustom", typeCode)
        intent.putExtra("QR_BACKGROUND", backgroundQr)
        intent.putExtra("STR_QR", strRequest)
        setResult(RESULT_OK, intent)
        finish()
    }
}