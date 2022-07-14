package com.peng

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.databinding.ItemProductBinding
import com.peng.databinding.ItemResultFoundBinding
import timber.log.Timber

class ProductsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Product) -> Unit
) : ListAdapter<Product, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<Product>() {

    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder : RecyclerView.ViewHolder? = null
        when (viewType) {
            VIEW_TYPE_RESULT_TEXT -> {
                val view =  LayoutInflater.from(parent.context).inflate(
                    R.layout.item_result_found,
                    parent,
                    false)
                val binding = ItemResultFoundBinding.bind(view)
                viewHolder = ResultTextVH(binding)
            }
            VIEW_TYPE_PRODUCT -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_product,
                    parent,
                    false)
                val binding = ItemProductBinding.bind(view)
                viewHolder = ProductVH(binding, onItemClick = { position ->
                    val itemAtPosition = currentList[position]
                    this.onItemClicked(position, itemAtPosition)
                })
            }
        }
        return viewHolder!!
    }

    override fun getItemViewType(position: Int): Int {
        val viewType: Int = if (position == 0) {
            VIEW_TYPE_RESULT_TEXT
        } else {
            VIEW_TYPE_PRODUCT
        }
        return viewType
    }

    override fun getItemCount(): Int = currentList.size

    override fun submitList(list: MutableList<Product>?) {
        list?.add(0, Product("", "", "", 0.00, ""))
        super.submitList(list)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemAtPosition = currentList[position]
        if (position == 0) {
            (holder as ResultTextVH)
        } else {
            (holder as ProductVH).bind(itemAtPosition)
        }
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

    inner class ResultTextVH(private val binding: ItemResultFoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    companion object {
        var VIEW_TYPE_RESULT_TEXT = 0
        var VIEW_TYPE_PRODUCT = 1

    }
}