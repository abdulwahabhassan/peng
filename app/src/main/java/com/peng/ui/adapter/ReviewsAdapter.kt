package com.peng.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.R
import com.peng.databinding.ItemCartItemBinding
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
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewVH(
            binding,
            onItemClick = { position ->
                try {
                    val itemAtPosition = currentList[position]
                    this.onItemClicked(position, itemAtPosition)
                } catch (e: Exception) { }

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
                reviewerNameTV.text = review.name
                reviewerCommentTV.text = review.comment
                reviewerIV.setImageResource(R.drawable.img_profile_pic)
                reviewTimeStampTV.text = review.date
                reviewRB.progress = review.rating / binding.reviewRB.max
            }
        }
    }

}