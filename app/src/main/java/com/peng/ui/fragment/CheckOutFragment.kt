package com.peng.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.peng.R
import com.peng.Utils
import com.peng.databinding.DialogAddPaymentCardBinding
import com.peng.databinding.DialogEditProfileBinding
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
    private var bottomSheetDialog: BottomSheetDialog? = null
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
                    if (result.data.isEmpty() || result.data.find { it.selected } == null) {
                        binding.checkoutSwitchCardTV.visibility = INVISIBLE
                        binding.checkoutSelectedPaymentCardCL.visibility = GONE
                    } else {
                        if (result.data.size == 1) {
                            binding.checkoutSwitchCardTV.visibility = GONE
                        } else {
                            binding.checkoutSwitchCardTV.visibility = VISIBLE
                        }
                        binding.checkoutSelectedPaymentCardCL.visibility = VISIBLE
                        setSelectedPaymentCard(
                            result.data.find { paymentCard -> paymentCard.selected }
                        )
                    }

                }
                else -> {}
            }

        }

        binding.checkoutPayButton.setOnClickListener {
            when (val result = viewModel.paymentCards.value) {
                is VMResult.Success -> {
                    if (result.data.isEmpty() || result.data.find { it.selected } == null) {
                        Snackbar.make(
                            binding.root,
                            "Please add a payment method",
                            Snackbar.LENGTH_LONG
                        ).setTextColor(requireContext().getColor(R.color.black))
                            .setBackgroundTint(requireContext().getColor(R.color.transparent_pink))
                            .setAnchorView(binding.checkoutPayButton)
                            .show()
                    } else {
                        //callPayStack()
                        viewModel.clearCart()
                        val action =
                            CheckOutFragmentDirections.actionCheckOutFragmentToSuccessFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }

        binding.checkOutEditAddressIB.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToEditCheckOutDetailsField(
                    "delivery address for this delivery",
                    binding.checkOutDeliveryAddressValueTv.text.toString()
                ) { dialog: BottomSheetDialog?, fieldValue: String ->
                    //action to update phone number
                    binding.checkOutDeliveryAddressValueTv.text = fieldValue
                    dialog?.dismiss()
                    bottomSheetDialog = null

                }
            }

        }

        binding.checkOutEditPhoneNumberIB.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToEditCheckOutDetailsField(
                    "phone number for this delivery",
                    binding.checkOutPhoneNumberValueTv.text.toString()
                ) { dialog: BottomSheetDialog?, fieldValue: String ->
                    //action to update phone number
                    binding.checkOutPhoneNumberValueTv.text = fieldValue
                    dialog?.dismiss()
                    bottomSheetDialog = null

                }
            }
        }

        binding.checkoutSwitchCardTV.setOnClickListener {
            when (val result = viewModel.paymentCards.value) {
                is VMResult.Success -> {
                    val currentCard = result.data.find {
                        it.cardNumber == binding.checkoutPaymentCardNumberTV.text.toString()
                            .replace(" ", "")
                    }
                    val pos = if (result.data.lastIndex == result.data.indexOf(currentCard)) 0
                    else result.data.indexOf(currentCard) + 1
                    setSelectedPaymentCard(result.data[pos])
                }
                else -> {}
            }

        }

        binding.checkoutAddNewCardButton.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToAddNewCard()
            }
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

    private fun showDialogToAddNewCard() {
        val dialogBinding = DialogAddPaymentCardBinding.inflate(
            LayoutInflater.from(requireContext()),
            this.binding.root,
            false
        )
        dialogBinding.addPaymentCardDoneButton.setOnClickListener {
            val cardType = PaymentCardOptions.values().random().name
            val cardTitle = dialogBinding.addPaymentCardNameET.text.toString().trim()
            val cardNumber = dialogBinding.addPaymentCardNumberET.text.toString().trim()
            val cardExpiryMonth = dialogBinding.addPaymentCardExpiryMonth.text.toString().trim()
            val cardExpiryYear = dialogBinding.addPaymentCardExpiryYear.text.toString().trim()
            val cardCVV = dialogBinding.addPaymentCardCVVET.text.toString().trim()
            if (
                cardType.isNotEmpty() &&
                cardTitle.isNotEmpty() &&
                cardNumber.isNotEmpty() &&
                cardExpiryMonth.isNotEmpty() &&
                cardExpiryYear.isNotEmpty() &&
                cardCVV.isNotEmpty()
            ) {
                val card = PaymentCard(
                    cardType = cardType,
                    cardTitle = cardTitle,
                    cardNumber = cardNumber,
                    cardExpiryMonth = cardExpiryMonth.toInt(),
                    cardExpiryYear = cardExpiryYear.toInt(),
                    cardCVV = cardCVV.trim()
                )
                viewModel.insertItemToPaymentCards(card)
                viewModel.updateSelectedPaymentCard(card.cardNumber)
                bottomSheetDialog?.dismiss()
                bottomSheetDialog = null
            } else {
                Snackbar.make(
                    dialogBinding.root,
                    "Incomplete field(s)!",
                    Snackbar.LENGTH_LONG
                ).setTextColor(requireContext().getColor(R.color.black))
                    .setBackgroundTint(requireContext().getColor(R.color.transparent_pink))
                    .setAnchorView(dialogBinding.root)
                    .show()
            }
        }

        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(dialogBinding.root)
        bottomSheetDialog?.dismissWithAnimation = true
        bottomSheetDialog?.setCanceledOnTouchOutside(false)
        bottomSheetDialog?.setOnDismissListener {
            bottomSheetDialog = null
        }
        bottomSheetDialog?.setOnCancelListener {
            bottomSheetDialog = null
        }
        bottomSheetDialog?.show()

    }

    private fun setSelectedPaymentCard(selectedPaymentCard: PaymentCard?) {
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
        )
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.checkoutPaymentCardNumberTV.text =
            selectedPaymentCard?.cardNumber?.chunked(4)?.joinToString(" ")
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
                PaystackSdk.chargeCard(
                    requireActivity(),
                    charge,
                    object : Paystack.TransactionCallback {
                        override fun onSuccess(transaction: Transaction?) {
                            //send ref to business owner for confirmation
                            Toast.makeText(
                                requireContext(),
                                "Successful payment ${transaction?.reference}",
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.clearCart()
                            val action =
                                CheckOutFragmentDirections.actionCheckOutFragmentToSuccessFragment()
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
                Toast.makeText(
                    requireContext(),
                    "Invalid Card: Please select a valid card",
                    Toast.LENGTH_SHORT
                ).show()
                binding.checkoutPayButton.visibility = VISIBLE
                binding.checkOutProgressIndicator.visibility = INVISIBLE
            }
        } ?: Toast.makeText(
            requireContext(),
            "Please select a payment method to use",
            Toast.LENGTH_SHORT
        ).show()
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

    private fun showDialogToEditCheckOutDetailsField(
        extendedTitleText: String,
        currentFieldValue: String,
        onDoneClicked: (dialog: BottomSheetDialog?, value: String) -> Unit
    ) {
        val dialogBinding = DialogEditProfileBinding.inflate(
            LayoutInflater.from(requireContext()),
            this.binding.root,
            false
        )
        dialogBinding.editProfileFieldValueET.setText(currentFieldValue)
        dialogBinding.editProfileTitleTV.text = "Enter new $extendedTitleText "
        dialogBinding.editProfileFieldValueET.hint = extendedTitleText
        dialogBinding.editProfileDoneButton.setOnClickListener {
            if (dialogBinding.editProfileFieldValueET.text.toString().trim().isEmpty()) {
                Snackbar.make(
                    dialogBinding.root,
                    "Empty field!",
                    Snackbar.LENGTH_LONG
                ).setTextColor(requireContext().getColor(R.color.black))
                    .setBackgroundTint(requireContext().getColor(R.color.transparent_pink))
                    .setAnchorView(dialogBinding.root)
                    .show()
            } else {
                onDoneClicked(
                    bottomSheetDialog,
                    dialogBinding.editProfileFieldValueET.text.toString().trim()
                )
            }
        }
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(dialogBinding.root)
        bottomSheetDialog?.dismissWithAnimation = true
        bottomSheetDialog?.setCanceledOnTouchOutside(false)
        bottomSheetDialog?.setOnDismissListener {
            bottomSheetDialog = null
        }
        bottomSheetDialog?.setOnCancelListener {
            bottomSheetDialog = null
        }
        bottomSheetDialog?.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}