package com.example.qrscan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.data.model.QrDetailItem
import com.example.qrscan.databinding.ItemQrDetailBinding

class QrResultDetailAdapter(private val listQrDetail: ArrayList<QrDetailItem>) :
    RecyclerView.Adapter<QrResultDetailAdapter.QrDetailViewHolder>() {
    inner class QrDetailViewHolder(val binding: ItemQrDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(qrDetailItem: QrDetailItem) {
            binding.modelQrDetail = qrDetailItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QrDetailViewHolder {
        return QrDetailViewHolder(
            ItemQrDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listQrDetail.size
    }

    override fun onBindViewHolder(holder: QrDetailViewHolder, position: Int) {
        holder.bind(listQrDetail[position])
    }

}