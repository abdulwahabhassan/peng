package com.peng.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.peng.PriceInputMask
import com.peng.R
import com.peng.Utils
import com.peng.databinding.DialogProductsFilterBinding
import com.peng.databinding.FragmentProductsBinding
import com.peng.model.Product
import com.peng.model.SearchProductSuggestion
import com.peng.model.VMResult
import com.peng.model.mapToCartItem
import com.peng.ui.adapter.ProductsAdapter
import com.peng.ui.adapter.SearchProductSuggestionsAdapter
import com.peng.vm.SharedActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var searchProductSuggestionsAdapter: SearchProductSuggestionsAdapter
    private val viewModel: SharedActivityViewModel by activityViewModels()
    private var bottomSheetDialog: BottomSheetDialog? = null

    @Inject
    lateinit var utils: Utils
    private val productsAdapterObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            binding.productsRV.visibility = VISIBLE
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            binding.productsRV.scrollToPosition(0)
            binding.productsRV.visibility = VISIBLE
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            binding.productsRV.scrollToPosition(0)
            binding.productsRV.visibility = VISIBLE

        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            binding.productsRV.scrollToPosition(0)
            binding.productsRV.visibility = VISIBLE
        }

        override fun onChanged() {
            super.onChanged()
            binding.productsRV.visibility = VISIBLE
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productsSearchViewET.setOnTouchListener { _, _ ->
            binding.productsSearchViewET.isCursorVisible = true
            binding.productsRV.visibility = INVISIBLE
            binding.productsFilterButton.visibility = GONE
            binding.productsGridButton.visibility = GONE
            binding.searchProductsCancelTV.visibility = VISIBLE
            binding.productsSearchRV.visibility = VISIBLE
            false
        }

        binding.searchProductsCancelTV.setOnClickListener {
            binding.root.requestFocus()
            binding.productsSearchViewET.isCursorVisible = false
            utils.hideKeyboard(requireContext(), binding.productsSearchViewET)
            binding.productsSearchViewET.setText("")
            binding.searchProductsCancelTV.visibility = INVISIBLE
            binding.productsSearchRV.visibility = INVISIBLE
            binding.productsFilterButton.visibility = VISIBLE
            binding.productsGridButton.visibility = VISIBLE
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                viewModel.fetchProducts()
            }
            false
        }

        binding.productsSearchViewET.setOnKeyListener { searchView, i, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                binding.root.requestFocus()
                binding.productsSearchViewET.isCursorVisible = false
                utils.hideKeyboard(requireContext(), searchView)
                binding.productsSearchRV.visibility = INVISIBLE
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    viewModel.fetchProducts(binding.productsSearchViewET.text.toString())
                }
            }
            return@setOnKeyListener true
        }

        binding.productsSearchViewET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    viewModel.updateSearchProductsResult(p0.trim().toString())
                }
            }
        })

        viewModel.cartItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {
                    binding.productsCartQuantityTV.text = result.data.size.toString()
                }
                else -> {}
            }
        }

        binding.productProfilePhotoIV.setImageResource(R.drawable.img_profile_pic)

        binding.productProfilePhotoIV.setOnClickListener {
            val action = ProductsFragmentDirections.actionProductsFragmentToProfileFragment()
            findNavController().navigate(action)
        }

        binding.productsShoppingCartLAV.setOnClickListener {
            val action = ProductsFragmentDirections.actionProductsFragmentToCartFragment()
            findNavController().navigate(action)
        }

        initProductsAdapter()
        initProductsRecyclerViewAdapter()

        initSearchProductSuggestionsAdapter()
        initSearchProductSuggestionsAdapterRVAdapter()

        viewModel.products.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {
                    productsAdapter.submitList(result.data.toMutableList())
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }
        }

        viewModel.searchProductSuggestion.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {
                    searchProductSuggestionsAdapter.submitList(result.data)
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }
        }

        initStaggeredGridLayoutManager(viewModel.appConfigPreferences.value?.gridColumns!!)

        binding.productsGridButton.setOnClickListener { changeGridLayout() }

        binding.productsFilterButton.setOnClickListener {
            if (bottomSheetDialog == null) {
                showDialogToFilter()
            }
        }

    }

    private fun showDialogToFilter() {
        val dialogBinding = DialogProductsFilterBinding.inflate(
            LayoutInflater.from(requireContext()),
            this.binding.root,
            false
        )

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

        PriceInputMask(dialogBinding.filterProductsPriceFromET).listen()
        PriceInputMask(dialogBinding.filterProductsPriceToET).listen()
        dialogBinding.filterProductsPriceFromET.setText(viewModel.appConfigPreferences.value?.filterByPriceLowRange.toString())
        dialogBinding.filterProductsPriceToET.setText(viewModel.appConfigPreferences.value?.filterByPriceHighRange.toString())
        dialogBinding.filterProductsRatingSlider.valueFrom = 0f
        dialogBinding.filterProductsRatingSlider.valueTo = 4f
        dialogBinding.filterProductsRatingSlider.setLabelFormatter { value ->
            "${value.toInt()} star"
        }
        dialogBinding.filterProductsRatingSlider.value =
            viewModel.appConfigPreferences.value?.filterByRating ?: 0f
        dialogBinding.filterProductsRatingSliderValueTV.text =
            "${(viewModel.appConfigPreferences.value?.filterByRating ?: 0f).toInt()} star and above"

        dialogBinding.filterProductsRatingSlider.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    dialogBinding.filterProductsRatingSliderValueTV.text =
                        "${slider.value.toInt()} star and above"
                }
            }
        )
        dialogBinding.filterProductsDoneButton.setOnClickListener {
            val lowRange = dialogBinding.filterProductsPriceFromET.text.toString()
                .replace(Regex("\\D"), "").toInt()
            val highRange = dialogBinding.filterProductsPriceToET.text.toString()
                .replace(Regex("\\D"), "").toInt()
            if (lowRange < highRange || lowRange == highRange) {
                viewModel.updateProductsFilter(
                    lowRange,
                    highRange,
                    dialogBinding.filterProductsRatingSlider.value
                )
                bottomSheetDialog?.dismiss()
            } else {
                Snackbar.make(
                    dialogBinding.root,
                    "Invalid price filter specified",
                    Snackbar.LENGTH_LONG
                ).setTextColor(requireContext().getColor(R.color.black))
                    .setActionTextColor(requireContext().getColor(R.color.black))
                    .setBackgroundTint(requireContext().getColor(R.color.white))
                    .setAnchorView(dialogBinding.root)
                    .show()
            }

        }
        bottomSheetDialog?.show()
    }

    private fun initSearchProductSuggestionsAdapter() {
        searchProductSuggestionsAdapter = SearchProductSuggestionsAdapter(
            onItemClicked = { position: Int, itemAtPosition: SearchProductSuggestion ->
                binding.root.requestFocus()
                binding.productsSearchViewET.isCursorVisible = false
                binding.productsSearchRV.visibility = INVISIBLE
                binding.productsSearchViewET.setText(itemAtPosition.text)
                utils.hideKeyboard(requireContext(), binding.productsSearchViewET)
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    viewModel.fetchProducts(itemAtPosition.text)
                }
            }
        )
        searchProductSuggestionsAdapter
            .stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun initSearchProductSuggestionsAdapterRVAdapter() {
        binding.productsSearchRV.adapter = searchProductSuggestionsAdapter
    }

    private fun initProductsAdapter() {
        productsAdapter = ProductsAdapter(
            onItemClicked = { position: Int, itemAtPosition: Product ->
                findNavController().navigate(
                    ProductsFragmentDirections.actionProductsFragmentToProductDetailsFragment(
                        itemAtPosition.name,
                        itemAtPosition.id,
                        itemAtPosition.image,
                        itemAtPosition.description,
                        itemAtPosition.price.toString(),
                        itemAtPosition.rating
                    )
                )
            },
            onItemAddedToCart = { position: Int, itemAtPosition: Product ->
                viewModel.addOrRemoveItemFromCart(itemAtPosition.mapToCartItem())
                if (!itemAtPosition.isInCart) {
                    binding.productsShoppingCartLAV.playAnimation()
                }
                productsAdapter.notifyItemChanged(position)
            },
            utils
        )
        productsAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun initProductsRecyclerViewAdapter() {
        binding.productsRV.adapter = productsAdapter
    }

    private fun initStaggeredGridLayoutManager(gridColumns: Int) {
        binding.productsRV.layoutManager = StaggeredGridLayoutManager(
            gridColumns,
            LinearLayoutManager.VERTICAL
        )
        setGridIcon(gridColumns)
    }

    private fun changeGridLayout() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            var gridColumns = viewModel.appConfigPreferences.value?.gridColumns!!
            if (gridColumns == 3) gridColumns = 1 else gridColumns += 1
            viewModel.updateGridPref(gridColumns)
            initStaggeredGridLayoutManager(viewModel.appConfigPreferences.value?.gridColumns!!)
        }
    }

    private fun setGridIcon(columns: Int) {
        when (columns) {
            1 -> {
                binding.productsGridButton.setImageResource(R.drawable.ic_grid_2_column)
            }
            2 -> {
                binding.productsGridButton.setImageResource(R.drawable.ic_grid_3_column)
            }
            3 -> {
                binding.productsGridButton.setImageResource(R.drawable.ic_grid_1_column)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        productsAdapter.registerAdapterDataObserver(productsAdapterObserver)
    }

    override fun onStop() {
        super.onStop()
        productsAdapter.unregisterAdapterDataObserver(productsAdapterObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}