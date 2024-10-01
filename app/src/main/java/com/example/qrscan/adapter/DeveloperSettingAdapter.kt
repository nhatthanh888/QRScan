package com.example.qrscan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.data.model.DeveloperSetting
import com.example.qrscan.databinding.ItemDeveloperBinding
import com.example.qrscan.extension.setOnSingleClickListener

class DeveloperSettingAdapter(
    private val list: List<DeveloperSetting>,
    private val onClickItem: (Int) -> Unit
) :
    RecyclerView.Adapter<DeveloperSettingAdapter.DeveloperViewHolder>() {
    inner class DeveloperViewHolder(val binding: ItemDeveloperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(developerSetting: DeveloperSetting) {
            binding.model = developerSetting
            binding.layout.setOnSingleClickListener {
                onClickItem.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeveloperViewHolder {
        return DeveloperViewHolder(
            ItemDeveloperBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: DeveloperViewHolder, position: Int) {
        holder.bind(list[position])
    }
}