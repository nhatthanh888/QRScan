package com.example.qrscan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscan.R
import com.example.qrscan.data.model.SearchEngineModel
import com.example.qrscan.databinding.ItemSearchEngineBinding
import com.example.qrscan.extension.setOnSingleClickListener

@Suppress("DEPRECATION")
class SearchEngineAdapter(
    val context: Context,
    val list: List<SearchEngineModel>,
    positionAdapter: Int=0,
    val getItemClick: (SearchEngineModel, Int) -> Unit
) :
    RecyclerView.Adapter<SearchEngineAdapter.SearchViewHolder>() {

    var itemSelected = positionAdapter

    inner class SearchViewHolder(val binding: ItemSearchEngineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setSingleSelected(positionAdapter: Int) {
            if (positionAdapter == RecyclerView.NO_POSITION) return
            notifyItemChanged(itemSelected)
            itemSelected = positionAdapter
            notifyItemChanged(itemSelected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemSearchEngineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.apply {
            binding.apply {
                list[position].apply {
                    tvNameSearchEngine.text = nameSearch
                    ivSelected.setOnSingleClickListener {
                        setSingleSelected(adapterPosition)
                        getItemClick.invoke(list[position], position)
                    }

                    if (itemSelected == position) {
                        ivSelected.setImageDrawable(context.getDrawable(R.drawable.check_search))
                    } else {
                        ivSelected.setImageDrawable(context.getDrawable(R.drawable.uncheck_search))
                    }
                }
            }
        }
    }
}