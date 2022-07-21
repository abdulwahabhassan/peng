package com.peng.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.R
import com.peng.databinding.ItemPaymentCardBinding
import com.peng.model.PaymentCard
import com.peng.model.PaymentCardOptions
import timber.log.Timber
import java.util.*

class PaymentCardsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: PaymentCard) -> Unit,
    private val onActivateRadioButtonClicked: (position: Int, itemAtPosition: PaymentCard) -> Unit
) : ListAdapter<PaymentCard, PaymentCardsAdapter.PaymentCardVH>(object :
    DiffUtil.ItemCallback<PaymentCard>() {

    override fun areItemsTheSame(oldItem: PaymentCard, newItem: PaymentCard): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PaymentCard, newItem: PaymentCard): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentCardVH {
        val binding = ItemPaymentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentCardVH(
            binding,
            onItemClick = { position ->
                val itemAtPosition = currentList[position]
                this.onItemClicked(position, itemAtPosition)
            },
            onActivateRadioButtonClick = { position ->
                val itemAtPosition = currentList[position]
                this.onActivateRadioButtonClicked(position, itemAtPosition)
            }
        )

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: PaymentCardVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class PaymentCardVH(
        private val binding: ItemPaymentCardBinding,
        onItemClick: (position: Int) -> Unit,
        onActivateRadioButtonClick: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
            binding.paymentCardSelectedIB.setOnClickListener {
                onActivateRadioButtonClick(bindingAdapterPosition)
            }
        }

        fun bind(paymentCard: PaymentCard) {
            Timber.d("$paymentCard")
            with(binding) {
                paymentCardNameTV.text = paymentCard.cardType.replace("_", " ")
                    .lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                paymentCardNumberTV.text = paymentCard.cardNumber
                when(paymentCard.cardType) {
                    PaymentCardOptions.MASTER_CARD.name -> {
                        paymentCardIV.setImageResource(R.drawable.ic_master_card)
                    }
                    PaymentCardOptions.VISA_CARD.name -> {
                        paymentCardIV.setImageResource(R.drawable.ic_visa_card)
                    }
                    PaymentCardOptions.VERVE_CARD.name -> {
                        paymentCardIV.setImageResource(R.drawable.ic_verve_card)
                    }
                }
                if (paymentCard.selected) {
                    paymentCardSelectedIB.setImageResource(R.drawable.ic_radio_button_active)
                } else {
                    paymentCardSelectedIB.setImageResource(R.drawable.ic_radio_button_inactive)
                }
            }
        }

    }
}