package com.example.qrscan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.R
import com.example.qrscan.data.model.MultipleScanModel
import com.example.qrscan.data.model.ResultScanModel
import com.example.qrscan.databinding.ItemHistoryFavoriteBinding
import com.example.qrscan.databinding.ItemMultipleScanBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.fragment.scan.FragmentScan
import com.example.qrscan.ui.fragment.scan.ListenerItemDelete
import com.example.qrscan.ui.fragment.scan.ListenerListFavorite
import com.example.qrscan.util.TypeResult
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.parcel.Parcelize

class ResultAdapterFavorite(
    val context: Context,
    val onClickItem: (ResultScanModel) -> Unit,
    val listenerListFavorite: ListenerListFavorite,
) :
    ListAdapter<ResultScanModel, ResultAdapterFavorite.FavoriteViewHolder>(DiffCallbackFavorite()) {

    inner class FavoriteViewHolder(val binding: ItemHistoryFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(resultScanModel: ResultScanModel) {
            binding.model = resultScanModel
            binding.apply {
                resultScanModel.apply {
                    when (resultScanModel.typeResult) {
                        TypeResult.TEXT -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_text)
                            ivTypeQR.setImageResource(R.drawable.text)
                        }

                        TypeResult.PHONE -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_phone)
                            ivTypeQR.setImageResource(R.drawable.calling)
                        }

                        TypeResult.EMAIL -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_email)
                            ivTypeQR.setImageResource(R.drawable.email)
                        }


                        TypeResult.CALENDAR -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_calendar)
                            ivTypeQR.setImageResource(R.drawable.calendar)
                        }

                        TypeResult.CONTACT -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_contact)
                            ivTypeQR.setImageResource(R.drawable.contact)
                        }

                        TypeResult.WEB -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_web)
                            ivTypeQR.setImageResource(R.drawable.web)
                        }

                        TypeResult.WIFI -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_wifi)
                            ivTypeQR.setImageResource(R.drawable.wifi)
                        }

                        TypeResult.LOCATION -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_location)
                            ivTypeQR.setImageResource(R.drawable.location)
                        }

                        TypeResult.BARCODE -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_barcode)
                            ivTypeQR.setImageResource(R.drawable.barcode)
                        }

                        TypeResult.FB -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_fb)
                            ivTypeQR.setImageResource(R.drawable.facebook)
                        }

                        TypeResult.IG -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_ig)
                            ivTypeQR.setImageResource(R.drawable.ig)
                        }

                        TypeResult.TWITTER -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_twitter)
                            ivTypeQR.setImageResource(R.drawable.twitter)
                        }

                        TypeResult.TIKTOK -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_tiktok)
                            ivTypeQR.setImageResource(R.drawable.tiktok)
                        }

                        TypeResult.YOUTUBE -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_youtube)
                            ivTypeQR.setImageResource(R.drawable.youtube)
                        }

                        TypeResult.WHATSAPP -> {
                            layoutTypeQRCode.background =
                                context.getDrawable(R.drawable.custom_circle_whatsapp)
                            ivTypeQR.setImageResource(R.drawable.whatsapp)
                        }

                    }
                }
                layout.setOnSingleClickListener {
                    onClickItem.invoke(resultScanModel)
                }
                ckSelectedDelete.setOnCheckedChangeListener { _, isCheck ->
                    listenerListFavorite.listenUpdateItem(
                        resultScanModel.id,
                        isCheck
                    )
                }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(
            ItemHistoryFavoriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: FavoriteViewHolder,
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

class DiffCallbackFavorite : DiffUtil.ItemCallback<ResultScanModel>() {
    override fun areItemsTheSame(oldItem: ResultScanModel, newItem: ResultScanModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ResultScanModel,
        newItem: ResultScanModel,
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ResultScanModel, newItem: ResultScanModel): Any? {
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
