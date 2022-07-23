package com.peng.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.databinding.ItemProductSearchBinding
import com.peng.model.SearchProductSuggestion
import timber.log.Timber

class SearchProductSuggestionsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: SearchProductSuggestion) -> Unit
) : ListAdapter<SearchProductSuggestion, SearchProductSuggestionsAdapter.SearchProductsResultVH>(object :
    DiffUtil.ItemCallback<SearchProductSuggestion>() {

    override fun areItemsTheSame(oldItem: SearchProductSuggestion, newItem: SearchProductSuggestion): Boolean {
        return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: SearchProductSuggestion, newItem: SearchProductSuggestion): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductsResultVH {
        val binding = ItemProductSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchProductsResultVH(
            binding,
            onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: SearchProductsResultVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }

    inner class SearchProductsResultVH(private val binding: ItemProductSearchBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(searchProductSuggestion: SearchProductSuggestion) {
            Timber.d("$searchProductSuggestion")
            with(binding) {
                binding.itemProductSearchResultTV.text = searchProductSuggestion.text
            }
        }
    }

}