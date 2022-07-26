package com.peng.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.R
import com.peng.Utils
import com.peng.databinding.ItemProductBinding
import com.peng.databinding.ItemResultFoundBinding
import com.peng.model.Product
import timber.log.Timber

class ProductsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Product) -> Unit,
    private val onItemAddedToCart: (position: Int, itemAtPosition: Product) -> Unit,
    private val utils: Utils
) : ListAdapter<Product, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<Product>() {

    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        //trigger change for result found text everytime a new list is submit by forcing
        //the equality check to return false
        return if (oldItem.id == "*" && newItem.id == "*") false
        else oldItem == newItem

    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            VIEW_TYPE_RESULT_TEXT -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_result_found,
                    parent,
                    false
                )
                val binding = ItemResultFoundBinding.bind(view)
                viewHolder = ResultTextVH(binding)
            }
            VIEW_TYPE_PRODUCT -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_product,
                    parent,
                    false
                )
                val binding = ItemProductBinding.bind(view)
                viewHolder = ProductVH(
                    binding,
                    onItemClick = { position ->
                        try {
                            val itemAtPosition = currentList[position]
                            this.onItemClicked(position, itemAtPosition)
                        } catch (e: Exception) {
                        }
                    },
                    onItemAddToCart = { position ->
                        try {
                            val itemAtPosition = currentList[position]
                            this.onItemAddedToCart(position, itemAtPosition)
                        } catch (e: Exception) {
                        }

                    }
                )
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

    override fun submitList(list: MutableList<Product>?) {
        //add dummy product
        list?.add(0, Product("*", "*", "*", 0.00, "*", list.size))
        super.submitList(list)
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemAtPosition = currentList[position]
        if (position == 0) {
            (holder as ResultTextVH).bind(currentList.size - 1)
        } else {
            (holder as ProductVH).bind(itemAtPosition)
        }
    }

    inner class ProductVH(
        private val binding: ItemProductBinding,
        onItemClick: (position: Int) -> Unit,
        onItemAddToCart: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }

            binding.addToCartButton.setOnClickListener {
                onItemAddToCart(bindingAdapterPosition)
            }
        }

        fun bind(product: Product) {
            Timber.d("$product")
            with(binding) {
                productIV.setImageResource(R.drawable.img_cleanser)
                productNameTV.text = product.name
                productDescriptionTV.text = product.description
                productPriceTV.text = "₦${utils.formatCurrency(product.price)}"
                addToCartButton.setImageResource(
                    if (product.isInCart)
                        R.drawable.ic_added_to_cart
                    else
                        R.drawable.ic_add_to_cart
                )
            }
        }
    }

    inner class ResultTextVH(private val binding: ItemResultFoundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(size: Int) {
            Timber.d("$size")
            with(binding) {
                binding.resultTV.text = "Found \n$size Result"
            }
        }
    }

    companion object {
        var VIEW_TYPE_RESULT_TEXT = 0
        var VIEW_TYPE_PRODUCT = 1

    }
}