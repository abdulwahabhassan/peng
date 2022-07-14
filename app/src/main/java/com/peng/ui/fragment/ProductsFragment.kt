package com.peng.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.peng.vm.ProductsFragmentViewModel
import com.peng.R
import com.peng.ViewModelFactory
import com.peng.databinding.FragmentProductsBinding
import com.peng.model.Product
import com.peng.ui.adapter.ProductsAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var productsAdapter: ProductsAdapter
    //view model
    private lateinit var viewModel: ProductsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[ProductsFragmentViewModel::class.java]

        productsAdapter = ProductsAdapter { position: Int, itemAtPosition: Product ->
            findNavController().navigate(
                ProductsFragmentDirections.actionProductsFragmentToProductDetailsFragment(
                    itemAtPosition.name,
                    itemAtPosition.id,
                    itemAtPosition.image,
                    itemAtPosition.description,
                    itemAtPosition.price.toString()
                )
            )
        }

        initRecyclerViewAdapter()

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            initStaggeredGridLayoutManager(viewModel.getAppConfig().gridColumns)
        }

        binding.productsGridButton.setOnClickListener {
           changeGridLayout()
        }

    }

    private fun initRecyclerViewAdapter() {
        binding.productsRV.adapter = productsAdapter
        productsAdapter.submitList(Product.products.toMutableList())
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