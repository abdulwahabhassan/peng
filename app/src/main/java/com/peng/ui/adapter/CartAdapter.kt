package com.peng.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.R
import com.peng.databinding.ItemCartItemBinding
import com.peng.model.CartItem
import timber.log.Timber

class CartAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: CartItem) -> Unit,
    private val onMinusButtonClicked: (position: Int, itemAtPosition: CartItem) -> Unit,
    private val onPlusButtonClicked: (position: Int, itemAtPosition: CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartItemVH>(object :
    DiffUtil.ItemCallback<CartItem>() {

    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemVH {
        val binding = ItemCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemVH(
            binding,
            onItemClick = { position ->
                val itemAtPosition = currentList[position]
                this.onItemClicked(position, itemAtPosition)
            },
            onMinusButtonClick = { position ->
                val itemAtPosition = currentList[position]
                this.onMinusButtonClicked(position, itemAtPosition)
            },
            onPlusButtonClick = { position ->
                val itemAtPosition = currentList[position]
                this.onPlusButtonClicked(position, itemAtPosition)
            }
        )

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: CartItemVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class CartItemVH(
        private val binding: ItemCartItemBinding,
        onItemClick: (position: Int) -> Unit,
        onMinusButtonClick: (position: Int) -> Unit,
        onPlusButtonClick: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
            binding.cartItemMinusButton.setOnClickListener {
                onMinusButtonClick(adapterPosition)
            }
            binding.cartItemPlusButton.setOnClickListener {
                onPlusButtonClick(adapterPosition)
            }
        }

        fun bind(cartItem: CartItem) {
            Timber.d("$cartItem")
            with(binding) {
                cartItemNameTV.text = cartItem.name
                cartItemDescriptionTV.text = cartItem.description
                cartItemIV.setImageResource(R.drawable.img_cleanser)
                cartItemPrice.text = "₦${cartItem.price}"
                cartItemQuantityTV.text = cartItem.quantity.toString()
            }
        }

    }
}