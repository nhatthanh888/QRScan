package com.example.qrscan.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.data.model.ColorModel
import com.example.qrscan.databinding.ItemColorCustomizeBinding
import com.example.qrscan.ui.activity.create.IClickColorListener

class QrColorAdapter(var listColor: ArrayList<ColorModel>) :
    RecyclerView.Adapter<QrColorAdapter.ColorViewHolder>() {
    private val dataSet: MutableList<ColorModel> = arrayListOf()
    private var currentSelectedPos = -1

    companion object {
        private var onClickItem: IClickColorListener? = null
    }

    inner class ColorViewHolder(itemView: ItemColorCustomizeBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val binding: ItemColorCustomizeBinding?

        init {
            binding = itemView
        }

        fun bindView(colorModel: ColorModel, currentSelected: Int) {
            binding?.imgColor?.setImageResource(colorModel.selector)
            binding?.root?.isSelected = layoutPosition == currentSelected
            binding?.root?.setOnClickListener {
                onClickItem?.onClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding =
            ItemColorCustomizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listColor.size
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bindView(dataSet[position], currentSelectedPos)
    }

    fun setOnClickListener(onClickItemListener: IClickColorListener) {
        onClickItem = onClickItemListener
    }

    fun setSelectedColor(newPosition: Int) {
        val oldPos = currentSelectedPos
        currentSelectedPos = newPosition
        if (oldPos >= 0) {
            notifyItemChanged(oldPos)
        }
        if (newPosition >= 0) {
            notifyItemChanged(newPosition)
        }
    }

    fun updateData(listColor: ArrayList<ColorModel>) {
        dataSet.clear()
        dataSet.addAll(listColor)
        notifyDataSetChanged()
    }

}