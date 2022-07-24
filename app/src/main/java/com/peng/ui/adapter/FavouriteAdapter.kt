package com.peng.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.R
import com.peng.Utils

import com.peng.databinding.ItemFavouriteItemBinding
import com.peng.model.FavouriteItem
import timber.log.Timber

class FavouriteAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: FavouriteItem) -> Unit,
    private val onFavouriteButtonClicked: (position: Int, itemAtPosition: FavouriteItem) -> Unit,
    private val utils: Utils
) : ListAdapter<FavouriteItem, FavouriteAdapter.FavouriteItemVH>(object :
    DiffUtil.ItemCallback<FavouriteItem>() {

    override fun areItemsTheSame(oldItem: FavouriteItem, newItem: FavouriteItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavouriteItem, newItem: FavouriteItem): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteItemVH {
        val binding = ItemFavouriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteItemVH(
            binding,
            onItemClick = { position ->
                try {
                    val itemAtPosition = currentList[position]
                    this.onItemClicked(position, itemAtPosition)
                } catch (e: Exception) { }

            },
            onFavouriteButtonClick = { position ->
                try {
                    val itemAtPosition = currentList[position]
                    this.onFavouriteButtonClicked(position, itemAtPosition)
                } catch (e: Exception) { }
            }
        )

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: FavouriteItemVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class FavouriteItemVH(
        private val binding: ItemFavouriteItemBinding,
        onItemClick: (position: Int) -> Unit,
        onFavouriteButtonClick: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
            binding.favouriteItemFavouriteButton.setOnClickListener {
                onFavouriteButtonClick(bindingAdapterPosition)
            }
        }

        fun bind(favouriteItem: FavouriteItem) {
            Timber.d("$favouriteItem")
            with(binding) {
                favouriteItemNameTV.text = favouriteItem.name
                favouriteItemDescriptionTV.text = favouriteItem.description
                favouriteItemIV.setImageResource(R.drawable.img_cleanser)
                favouriteItemPrice.text = "₦${utils.formatCurrency(favouriteItem.price)}"
            }
        }

    }
}