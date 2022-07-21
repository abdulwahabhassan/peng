package com.peng.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.peng.R
import com.peng.databinding.FragmentProductsBinding
import com.peng.model.Product
import com.peng.model.VMResult
import com.peng.model.mapToCartItem
import com.peng.ui.adapter.ProductsAdapter
import com.peng.vm.SharedActivityViewModel


class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsAdapter: ProductsAdapter
    private val viewModel: SharedActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cartItems.observe(viewLifecycleOwner) { result ->
            when(result) {
                is VMResult.Success -> {
                    binding.productsCartQuantityTV.text = result.data.size.toString()
                }
                else -> {}
            }
        }

        //set profile pic
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

        viewModel.products.observe(viewLifecycleOwner) { result ->
            when(result) {
                is VMResult.Success -> {
                    productsAdapter.submitList(result.data.toMutableList())
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            initStaggeredGridLayoutManager(viewModel.getAppConfig().gridColumns)
        }

        binding.productsGridButton.setOnClickListener {
           changeGridLayout()
        }

        binding.productsFilterButton.setOnClickListener {
        }

    }

    private fun initProductsAdapter() {
        productsAdapter = ProductsAdapter (
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
        })
        productsAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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
            var gridColumns = viewModel.getAppConfig().gridColumns
            if (gridColumns == 3) gridColumns = 1 else gridColumns += 1
            viewModel.updateGridPref(gridColumns)
            initStaggeredGridLayoutManager(viewModel.getAppConfig().gridColumns)
        }
    }

    private fun setGridIcon(columns: Int) {
        when(columns) {
            1 -> { binding.productsGridButton.setImageResource(R.drawable.ic_grid_2_column) }
            2 -> { binding.productsGridButton.setImageResource(R.drawable.ic_grid_3_column) }
            3 -> { binding.productsGridButton.setImageResource(R.drawable.ic_grid_1_column) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}