package com.peng.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.R
import com.peng.databinding.ItemProductBinding
import com.peng.databinding.ItemResultFoundBinding
import com.peng.databinding.ItemReviewBinding
import com.peng.model.Product
import com.peng.model.Review
import timber.log.Timber

class ReviewsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Review) -> Unit
) : ListAdapter<Review, ReviewsAdapter.ReviewVH>(object :
    DiffUtil.ItemCallback<Review>() {

    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewVH {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_review,
            parent,
            false)
        val binding = ItemReviewBinding.bind(view)
        return ReviewVH(binding, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: ReviewVH, position: Int) {
        val animation = android.view.animation.AnimationUtils
            .loadAnimation(holder.itemView.context, android.R.anim.slide_in_left)
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
        holder.itemView.startAnimation(animation)
    }

    inner class ReviewVH(private val binding: ItemReviewBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(review: Review) {
            Timber.d("$review")
            with(binding) {
                binding.reviewerNameTV.text = review.name
                binding.reviewerCommentTV.text = review.comment
                binding.reviewerIV.setImageResource(R.drawable.img_profile_pic)
                binding.reviewTimeStampTV.text = review.date
                binding.reviewRB.progress = review.rating / binding.reviewRB.max
            }
        }
    }

}