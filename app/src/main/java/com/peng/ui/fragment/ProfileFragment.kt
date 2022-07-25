package com.peng.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.peng.Utils
import com.peng.contract.GalleryActivityContract
import com.peng.databinding.DialogAddPaymentCardBinding
import com.peng.databinding.DialogEditProfileBinding
import com.peng.databinding.FragmentProfileBinding
import com.peng.db.PaymentCardEntity
import com.peng.model.FavouriteItem
import com.peng.model.PaymentCard
import com.peng.model.PaymentCardOptions
import com.peng.model.VMResult
import com.peng.ui.adapter.FavouriteAdapter
import com.peng.ui.adapter.PaymentCardsAdapter
import com.peng.vm.SharedActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedActivityViewModel by activityViewModels()
    private lateinit var favouriteRecyclerViewAdapter: FavouriteAdapter
    private lateinit var paymentCardsRecyclerViewAdapter: PaymentCardsAdapter
    @Inject
    lateinit var utils: Utils
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var image: String? = null
    private val openGallery = registerForActivityResult(GalleryActivityContract()) { imagePath ->
        if (imagePath.isNotEmpty()) {
            Toast.makeText(requireContext(), "image path: $imagePath", Toast.LENGTH_SHORT).show()
            image = imagePath
            displaySelectedPhoto(imagePath)
        } else {
           Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displaySelectedPhoto(imageUri: String) {
        Glide.with(this).load(imageUri).into(binding.profileUserPhotoIV)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileDetailsMaterialToolbar.setupWithNavController(findNavController())

        enableImageSelection()

        viewModel.cartItems.observe(viewLifecycleOwner) { result ->
            when(result) {
                is VMResult.Success -> {
                    binding.profileCartQuantityTV.text = result.data.size.toString()
                }
                else -> {}
            }
        }

        binding.profileEditUserPhoto.setOnClickListener {
            if (haveStoragePermission()) {
                openGallery.launch(Unit)
            } else {
                requestPermission()
            }
        }

        binding.profileNameEditButton.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToEditProfileField(
                    "name",
                    binding.profileNameTV.text.toString()
                ) {
                        dialog: BottomSheetDialog?, fieldValue: String ->
                    //action to update name
                    binding.profileNameTV.text = fieldValue
                    dialog?.dismiss()
                    bottomSheetDialog = null

                }
            }

        }

        binding.profileEmailAddressEditButton.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToEditProfileField(
                    "email address",
                    binding.profileEmailAddressTV.text.toString()
                ){
                        dialog: BottomSheetDialog?, fieldValue: String ->
                    //action to update email address
                    binding.profileEmailAddressTV.text = fieldValue
                    dialog?.dismiss()
                    bottomSheetDialog = null

                }
            }

        }

        binding.profileDeliveryAddressEditButton.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToEditProfileField(
                    "delivery address",
                    binding.profileDeliveryAddressTV.text.toString()
                ){
                        dialog: BottomSheetDialog?, fieldValue: String ->
                    //action to update delivery address
                    binding.profileDeliveryAddressTV.text = fieldValue
                    dialog?.dismiss()
                    bottomSheetDialog = null
                }
            }

        }

        binding.profilePhoneNumberEditButton.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToEditProfileField(
                    "phone number",
                    binding.profilePhoneNumberTV.text.toString()
                ) {
                        dialog: BottomSheetDialog?, fieldValue: String ->
                    //action to update phone number
                    binding.profilePhoneNumberTV.text = fieldValue
                    dialog?.dismiss()
                    bottomSheetDialog = null
                }
            }

        }

        binding.profilePasswordEditButton.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToEditProfileField(
                    "password",
                    binding.profilePasswordValueTV.text.toString()
                ) {
                        dialog: BottomSheetDialog?, fieldValue: String ->
                    //action to update phone number
                    binding.profilePasswordValueTV.text = fieldValue.map { c: Char -> "*" }
                        .joinToString("")
                    dialog?.dismiss()
                    bottomSheetDialog = null
                }
            }

        }

        binding.profileAddNewCardButton.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToAddNewCard()
            }
        }

        initFavouriteAdapter()

        initCartRecyclerViewAdapter()

        initPaymentCardsAdapter()

        initPaymentCardsRecyclerViewAdapter()

        viewModel.favouriteItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {

                    if (result.data.isNotEmpty()) {
                        favouriteRecyclerViewAdapter.submitList(result.data)
                        binding.profileFavouriteLAV.visibility = VISIBLE
                        binding.profileFavouriteRV.visibility = VISIBLE
                        binding.profileFavouriteTV.visibility = VISIBLE
                    } else {
                        binding.profileFavouriteLAV.visibility = GONE
                        binding.profileFavouriteRV.visibility = GONE
                        binding.profileFavouriteTV.visibility = GONE
                    }

                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }
        }

        viewModel.paymentCards.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {
                    paymentCardsRecyclerViewAdapter.submitList(result.data)
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }
        }

        binding.profileLogoutLAV.setOnClickListener {

        }
        binding.profileShoppingCartLAV.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToCartFragment()
            findNavController().navigate(action)
        }

        if (!haveStoragePermission()) {
            requestPermission()
        }
    }

    private fun initCartRecyclerViewAdapter() {
        binding.profileFavouriteRV.adapter = favouriteRecyclerViewAdapter
    }

    private fun initFavouriteAdapter() {
        favouriteRecyclerViewAdapter = FavouriteAdapter(
            onItemClicked = { position: Int, itemAtPosition: FavouriteItem ->
                val action = ProfileFragmentDirections.actionProfileFragmentToProductDetailsFragment(
                    itemAtPosition.name,
                    itemAtPosition.id,
                    itemAtPosition.image,
                    itemAtPosition.description,
                    itemAtPosition.price.toString(),
                    itemAtPosition.rating,
                )
                findNavController().navigate(action)

            }, onFavouriteButtonClicked = { position: Int, itemAtPosition: FavouriteItem ->
                viewModel.addOrRemoveItemFromFavourite(itemAtPosition)
            },
            utils
        )
    }

    private fun initPaymentCardsRecyclerViewAdapter() {
        binding.profilePaymentRV.adapter = paymentCardsRecyclerViewAdapter
    }

    private fun initPaymentCardsAdapter() {
        paymentCardsRecyclerViewAdapter = PaymentCardsAdapter(
            onItemClicked = { position: Int, itemAtPosition: PaymentCard ->

            },
            onActivateRadioButtonClicked = {position: Int, itemAtPosition: PaymentCard ->
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    viewModel.updateSelectedPaymentCard(itemAtPosition.cardNumber)
                }
            }
        )
    }

    private fun showDialogToEditProfileField(
        fieldName: String,
        currentFieldValue: String,
        onDoneClicked: (dialog: BottomSheetDialog?, value: String) -> Unit
    ) {
        val dialogBinding = DialogEditProfileBinding.inflate(
            LayoutInflater.from(requireContext()),
            this.binding.root,
            false
        )
        dialogBinding.editProfileFieldValueET.setText(currentFieldValue)
        dialogBinding.editProfileTitleTV.text = "Enter new $fieldName "
        dialogBinding.editProfileFieldValueET.hint = fieldName
        dialogBinding.editProfileDoneButton.setOnClickListener {
            onDoneClicked(bottomSheetDialog, dialogBinding.editProfileFieldValueET.text.toString())
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
                bottomSheetDialog?.dismiss()
                bottomSheetDialog = null
            } else {
                Toast.makeText(requireContext(), "Incomplete fields", Toast.LENGTH_SHORT).show()
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

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) { enableImageSelection() } else {
        val showRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        if (showRationale) {
            requestPermission()
        } else {
            goToSettings()
        }
    }
    }

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    private fun goToSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${requireActivity().packageName}")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    private fun enableImageSelection() {
        if (haveStoragePermission()) {
            binding.profileEditUserPhoto.setOnClickListener {
                openGallery.launch(Unit)
            }
        } else {
            requestPermission()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}