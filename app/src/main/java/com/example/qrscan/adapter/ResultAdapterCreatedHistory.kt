package com.example.qrscan.adapter

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.qrscan.databinding.ItemHistoryCreatedBinding
import com.example.qrscan.databinding.ItemHistoryScannedBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.fragment.scan.ListenerListCreated
import com.example.qrscan.ui.fragment.scan.ListenerListFavorite
import com.example.qrscan.util.TypeResult

class ResultAdapterCreatedHistory(
    val context: Context,
    val onClickItem: (ResultScanModel) -> Unit,
    val listenerListCreated: ListenerListCreated
) :
    ListAdapter<ResultScanModel, ResultAdapterCreatedHistory.ResultsViewHolder>(
        DiffCallbackCreatedHistory()
    ) {

    inner class ResultsViewHolder(val binding: ItemHistoryCreatedBinding) :
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
                    listenerListCreated.listenUpdateItem(resultScanModel.id, isCheck)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        return ResultsViewHolder(
            ItemHistoryCreatedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ResultsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            holder.updateItem(bundle)
        }
    }

}

class DiffCallbackCreatedHistory : DiffUtil.ItemCallback<ResultScanModel>() {
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