package com.peng.ui.adapter

import com.peng.databinding.ItemCheckoutItemBinding
import com.peng.model.CheckOutItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.R
import com.peng.Utils
import timber.log.Timber

class CheckOutItemsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: CheckOutItem) -> Unit
) : ListAdapter<CheckOutItem, CheckOutItemsAdapter.CheckOutItemVH>(object :
    DiffUtil.ItemCallback<CheckOutItem>() {

    override fun areItemsTheSame(oldItem: CheckOutItem, newItem: CheckOutItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CheckOutItem, newItem: CheckOutItem): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckOutItemVH {
        val binding = ItemCheckoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckOutItemVH(
            binding,
            onItemClick = { position ->
                val itemAtPosition = currentList[position]
                this.onItemClicked(position, itemAtPosition)
            }
        )

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: CheckOutItemVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class CheckOutItemVH(
        private val binding: ItemCheckoutItemBinding,
        onItemClick: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }

        }

        fun bind(checkOutItem: CheckOutItem) {
            Timber.d("$checkOutItem")
            with(binding) {
                checkOutItemNameTV.text = checkOutItem.name
                checkOutItemDescriptionTV.text = checkOutItem.description
                checkOutItemIV.setImageResource(R.drawable.img_cleanser)
                checkOutItemPrice.text = "₦${Utils().formatCurrency(checkOutItem.price)}"
                checkOutItemQuantity.text = "x${checkOutItem.quantity}"
            }
        }

    }
}