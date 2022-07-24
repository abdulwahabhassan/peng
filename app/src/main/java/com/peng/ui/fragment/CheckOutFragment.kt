package com.peng.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.peng.R
import com.peng.Utils
import com.peng.databinding.FragmentCheckOutBinding
import com.peng.model.*
import com.peng.ui.adapter.CheckOutItemsAdapter
import com.peng.vm.SharedActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class CheckOutFragment : Fragment() {

    private var _binding: FragmentCheckOutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedActivityViewModel by activityViewModels()
    private lateinit var checkOutRecyclerViewAdapter: CheckOutItemsAdapter
    private var selectedPaymentCard: PaymentCard? = null
    var totalAmount by Delegates.notNull<Double>()
    @Inject
    lateinit var utils: Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // override on back pressed
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!binding.checkOutProgressIndicator.isVisible) {
                        //if transaction is not in progress, allow activity to handle back button
                            //pressed
                        isEnabled = false
                        activity?.onBackPressed()
                    } else {
                        //otherwise beg customer to stay on the page while the app processes their
                            //transaction
                        isEnabled = true
                        Toast.makeText(
                            requireContext(),
                            "Transaction is in progress. Please, stay on this page",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

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
                    selectedPaymentCard = result.data.find { paymentCard -> paymentCard.selected }
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
                    binding.checkoutPaymentCardNumberTV.text = selectedPaymentCard?.cardNumber?.chunked(4)?.joinToString(" ")
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }

        }

        binding.checkoutPayButton.setOnClickListener {
            //callPayStack()

            //demo action
            viewModel.clearCart()
            val action = CheckOutFragmentDirections.actionCheckOutFragmentToSuccessFragment()
            findNavController().navigate(action)
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
                    totalAmount = subtotal + shippingFee

                    binding.cartQuantityTV.text = result.data.size.toString()
                    binding.checkoutShippingFeeTV.text = "₦${utils.formatCurrency(shippingFee)}"
                    binding.checkoutSubTotalTV.text = "₦${utils.formatCurrency(subtotal)}"
                    binding.checkoutTotalTV.text = "₦${utils.formatCurrency(totalAmount)}"
                    binding.checkoutPayButton.text = "Pay (₦${utils.formatCurrency(totalAmount)})"

                    checkOutRecyclerViewAdapter.submitList(result.data.map { cartItem: CartItem ->
                        cartItem.mapToCheckOutItem()
                    })
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }
        }
    }

    private fun callPayStack() {
        selectedPaymentCard?.let {
            binding.checkoutPayButton.visibility = INVISIBLE
            binding.checkOutProgressIndicator.visibility = VISIBLE

            val cardNumber = selectedPaymentCard!!.cardNumber
            val expiryMonth = selectedPaymentCard!!.cardExpiryMonth
            val expiryYear = selectedPaymentCard!!.cardExpiryYear
            val cvv = selectedPaymentCard!!.cardCVV
            val card = Card(cardNumber, expiryMonth, expiryYear, cvv)

            if (card.isValid) {
                val charge = Charge()
                    .setAmount((totalAmount * 100).toInt())
                    .setEmail(viewModel.appConfigPreferences.value?.userEmail)
                    .setCard(card)
                PaystackSdk.chargeCard(requireActivity(), charge, object : Paystack.TransactionCallback {
                    override fun onSuccess(transaction: Transaction?) {
                        //send ref to business owner for confirmation
                        Toast.makeText(
                            requireContext(),
                            "Successful payment ${transaction?.reference}",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.clearCart()
                        val action = CheckOutFragmentDirections.actionCheckOutFragmentToSuccessFragment()
                        findNavController().navigate(action)
                    }

                    override fun beforeValidate(transaction: Transaction?) {
                        Toast.makeText(
                            requireContext(),
                            "Before validate ${transaction?.reference}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: Throwable?, transaction: Transaction?) {
                        Toast.makeText(
                            requireContext(),
                            "Error due to ${error?.message} " +
                                    "Ref: ${transaction?.reference}",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.checkoutPayButton.visibility = VISIBLE
                        binding.checkOutProgressIndicator.visibility = INVISIBLE
                    }

                })
            } else {
                Toast.makeText(requireContext(), "Invalid Card: Please select a valid card", Toast.LENGTH_SHORT).show()
                binding.checkoutPayButton.visibility = VISIBLE
                binding.checkOutProgressIndicator.visibility = INVISIBLE
            }
        } ?: Toast.makeText(requireContext(), "Please select a payment method to use", Toast.LENGTH_SHORT).show()
    }

    private fun initCheckOutRecyclerViewAdapter() {
        binding.checkOutItemsRV.adapter = checkOutRecyclerViewAdapter
    }

    private fun initCheckOutAdapter() {
        checkOutRecyclerViewAdapter = CheckOutItemsAdapter(
            onItemClicked = { position: Int, itemAtPosition: CheckOutItem ->

            },
            utils
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}