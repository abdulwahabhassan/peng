package com.peng

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.peng.databinding.FragmentProductsBinding
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
            Timber.d( "${itemAtPosition.name} $position")
        }
        binding.productsRV.adapter = productsAdapter
        productsAdapter.submitList(Product.products)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}