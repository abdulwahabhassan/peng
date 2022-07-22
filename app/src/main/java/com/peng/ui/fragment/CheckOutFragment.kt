package com.peng.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.peng.R
import com.peng.Utils
import com.peng.databinding.FragmentCheckOutBinding
import com.peng.model.*
import com.peng.ui.adapter.CheckOutItemsAdapter
import com.peng.vm.SharedActivityViewModel
import java.util.*

class CheckOutFragment : Fragment() {

    private var _binding: FragmentCheckOutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedActivityViewModel by activityViewModels()
    private lateinit var checkOutRecyclerViewAdapter: CheckOutItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckOutBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.checkOutMaterialToolbar.setupWithNavController(findNavController())

        viewModel.paymentCards.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {
                    val selectedPaymentCard = result.data.find { paymentCard -> paymentCard.selected }
                    when (selectedPaymentCard?.cardType) {
                        PaymentCardOptions.MASTER_CARD.name -> {
                            binding.checkoutPaymentCardIV.setImageResource(R.drawable.ic_master_card)
                        }
                        PaymentCardOptions.VERVE_CARD.name -> {
                            binding.checkoutPaymentCardIV.setImageResource(R.drawable.ic_verve_card)
                        }
                        PaymentCardOptions.VISA_CARD.name -> {
                            binding.checkoutPaymentCardIV.setImageResource(R.drawable.ic_visa_card)
                        }
                    }
                    binding.checkoutPaymentCardNameTV.text = selectedPaymentCard?.cardType?.lowercase(
                        Locale.getDefault()
                    )?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    binding.checkoutPaymentCardNumberTV.text = selectedPaymentCard?.cardNumber
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }

        }

        binding.checkoutPayButton.setOnClickListener {

        }

        initCheckOutAdapter()

        initCheckOutRecyclerViewAdapter()

        viewModel.cartItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {
                    val subtotal = result.data.fold(0.00) { acc: Double, cartItem: CartItem ->
                        acc + (cartItem.price * cartItem.quantity)
                    }
                    val shippingFee = 0.00
                    binding.cartQuantityTV.text = result.data.size.toString()
                    binding.checkoutShippingFeeTV.text = "₦${Utils().formatCurrency(shippingFee)}"
                    binding.checkoutSubTotalTV.text = "₦${Utils().formatCurrency(subtotal)}"
                    binding.checkoutTotalTV.text = "₦${Utils().formatCurrency(subtotal + shippingFee)}"

                    checkOutRecyclerViewAdapter.submitList(result.data.map { cartItem: CartItem ->
                        cartItem.mapToCheckOutItem()
                    })
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }
        }
    }

    private fun initCheckOutRecyclerViewAdapter() {
        binding.checkOutItemsRV.adapter = checkOutRecyclerViewAdapter
    }

    private fun initCheckOutAdapter() {
        checkOutRecyclerViewAdapter = CheckOutItemsAdapter(
            onItemClicked = { position: Int, itemAtPosition: CheckOutItem ->

            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}