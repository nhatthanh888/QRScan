package com.example.qrscan.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.data.model.MultipleScanModel
import com.example.qrscan.databinding.ItemMultipleScanBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.fragment.scan.FragmentScan
import com.example.qrscan.ui.fragment.scan.ListenerItemDelete
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.parcel.Parcelize

class MultipleScanAdapter(
    val onClickItem: (MultipleScanModel) -> Unit,
    val listenerItemDelete: ListenerItemDelete,
) :
    ListAdapter<MultipleScanModel, MultipleScanAdapter.MultipleScanViewHolder>(DiffCallback2()) {

    inner class MultipleScanViewHolder(val binding: ItemMultipleScanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(multipleScanModel: MultipleScanModel) {
            binding.model = multipleScanModel
            binding.apply {
                multipleScanModel.apply {
                    layout.setOnSingleClickListener {
                        onClickItem.invoke(multipleScanModel)
                    }

                    ckSelectedDelete.setOnCheckedChangeListener { _, isCheck ->
                        listenerItemDelete.listenUpdateItem(
                            multipleScanModel.id,
                            isCheck
                        )
                    }
                }
            }
        }

        fun generateQrCode(strRequest: String) {
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(
                    strRequest,
                    BarcodeFormat.QR_CODE,
                    120,
                    120
                )
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                binding.ivQR.setImageBitmap(bmp)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }

        fun generateBarcode(strBarcode: String) {
            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix = multiFormatWriter.encode(
                    strBarcode,
                    BarcodeFormat.CODE_128,
                    100,
                    50
                )
                val bitmap = Bitmap.createBitmap(
                    100,
                    50,
                    Bitmap.Config.RGB_565
                )
                for (x in 0 until 100) {
                    for (y in 0 until 50) {
                        bitmap.setPixel(
                            x,
                            y,
                            if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                        )
                    }
                }
                binding.ivQR.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }


        fun updateItem(bundle: Bundle) {
            with(binding) {
                with(bundle) {
                    if (containsKey("check")) {
                        ckSelectedDelete.isChecked = getBoolean("check")
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleScanViewHolder {
        return MultipleScanViewHolder(
            ItemMultipleScanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MultipleScanViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (getItem(position).typeCode == FragmentScan.BARCODE) {
            holder.generateBarcode(getItem(position).value)
        } else {
            holder.generateQrCode(getItem(position).value)
        }
    }

    override fun onBindViewHolder(
        holder: MultipleScanViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            holder.updateItem(bundle)
        }
    }
}

class DiffCallback2 : DiffUtil.ItemCallback<MultipleScanModel>() {
    override fun areItemsTheSame(oldItem: MultipleScanModel, newItem: MultipleScanModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MultipleScanModel,
        newItem: MultipleScanModel,
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: MultipleScanModel, newItem: MultipleScanModel): Any? {
        if (oldItem.id == newItem.id) {
            return if (oldItem.isCheck == newItem.isCheck) {
                super.getChangePayload(oldItem, newItem)
            } else {
                val bundle = Bundle()
                if (oldItem.isCheck != newItem.isCheck) {
                    bundle.putBoolean("check", newItem.isCheck)
                }
                bundle
            }
        }
        return super.getChangePayload(oldItem, newItem)
    }

}
