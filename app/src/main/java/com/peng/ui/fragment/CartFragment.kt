package com.peng.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.peng.R
import com.peng.Utils
import com.peng.ViewModelFactory
import com.peng.databinding.FragmentCartBinding
import com.peng.databinding.FragmentProductDetailsBinding
import com.peng.model.CartItem
import com.peng.ui.adapter.CartAdapter
import com.peng.ui.adapter.ReviewsAdapter
import com.peng.vm.CartFragmentViewModel
import com.peng.vm.ProductDetailsFragmentViewModel
import com.peng.vm.ProductsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: CartFragmentViewModel
    private lateinit var cartRecyclerViewAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[CartFragmentViewModel::class.java]

        binding.cartProceedToCheckOutButton.setOnClickListener {

        }

        val subtotal = CartItem.cartItems.fold(0.00) {
                acc: Double, cartItem: CartItem -> acc + cartItem.price
        }
        val fee = 40.00
        binding.cartQuantityTV.text = CartItem.cartItems.size.toString()
        binding.cartShippingFeeTV.text = "₦${Utils().formatCurrency(fee)}"
        binding.cartSubTotalTV.text = "₦${Utils().formatCurrency(subtotal)}"
        binding.cartTotalTV.text = "₦${Utils().formatCurrency(subtotal + fee)}"

        initCartAdapter()

        initCartRecyclerViewAdapter()

    }

    private fun initCartRecyclerViewAdapter() {
        binding.cartRV.adapter = cartRecyclerViewAdapter
        cartRecyclerViewAdapter.submitList(CartItem.cartItems)
    }

    private fun initCartAdapter() {
        cartRecyclerViewAdapter = CartAdapter(
            onItemClicked = { position: Int, itemAtPosition: CartItem ->

            }, onMinusButtonClicked = { position: Int, itemAtPosition: CartItem ->
                val qty = itemAtPosition.quantity
                if (qty == 0) {
                    cartRecyclerViewAdapter.currentList.removeAt(position)
                    cartRecyclerViewAdapter.notifyItemRemoved(position)
                } else {
                    itemAtPosition.quantity -= 1
                }
            }, onPlusButtonClicked = { position: Int, itemAtPosition: CartItem ->
                itemAtPosition.quantity += 1
            })
    }
}