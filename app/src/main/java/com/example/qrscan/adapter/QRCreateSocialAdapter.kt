package com.example.qrscan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.data.model.QRCreate
import com.example.qrscan.databinding.ItemCreateSocialMediaQrBinding
import com.example.qrscan.extension.setOnSingleClickListener

class QRCreateSocialAdapter(private val list: List<QRCreate>, val clickItem: (Int) -> Unit) :
    RecyclerView.Adapter<QRCreateSocialAdapter.QRCreateSocialHolder>() {

    inner class QRCreateSocialHolder(val binding: ItemCreateSocialMediaQrBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(qrCreate: QRCreate) {
            binding.model = qrCreate
            binding.ivbCreate.setOnSingleClickListener {
                clickItem.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRCreateSocialHolder {
        return QRCreateSocialHolder(
            ItemCreateSocialMediaQrBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: QRCreateSocialHolder, position: Int) {
        holder.bind(list[position])
    }
}