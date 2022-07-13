package com.peng

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.databinding.ItemProductBinding
import timber.log.Timber

class ProductsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Product) -> Unit
) : ListAdapter<Product, ProductsAdapter.ProductVH>(object :
    DiffUtil.ItemCallback<Product>() {

    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        //inflate the view to be used by the payment option view holder
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductVH(binding, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class ProductVH(private val binding: ItemProductBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }


        fun bind(product: Product) {
            Timber.d("$product")
            with(binding) {
                binding.productNameTV.text = product.name
                binding.productDescriptionTV.text = product.description
                binding.productPriceTV.text = "₦${product.price}"
            }
        }

    }
}