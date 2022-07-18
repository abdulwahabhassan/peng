package com.peng.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.peng.Utils
import com.peng.ViewModelFactory
import com.peng.databinding.FragmentCartBinding
import com.peng.model.CartItem
import com.peng.model.VMResult
import com.peng.ui.adapter.CartAdapter
import com.peng.vm.SharedActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SharedActivityViewModel by activityViewModels()
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

        binding.productDetailsMaterialToolbar.setupWithNavController(findNavController())

        binding.cartProceedToCheckOutButton.setOnClickListener {

        }

        initCartAdapter()

        initCartRecyclerViewAdapter()

        viewModel.cartItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {
                    val subtotal = result.data.fold(0.00) { acc: Double, cartItem: CartItem ->
                        acc + cartItem.price
                    }
                    val fee = 40.00
                    binding.cartQuantityTV.text = result.data.size.toString()
                    binding.cartShippingFeeTV.text = "₦${Utils().formatCurrency(fee)}"
                    binding.cartSubTotalTV.text = "₦${Utils().formatCurrency(subtotal)}"
                    binding.cartTotalTV.text = "₦${Utils().formatCurrency(subtotal + fee)}"

                    cartRecyclerViewAdapter.submitList(result.data)
                }
                is VMResult.Error -> {}
                is VMResult.Loading -> {}
            }
        }
    }

    private fun initCartRecyclerViewAdapter() {
        binding.cartRV.adapter = cartRecyclerViewAdapter
    }

    private fun initCartAdapter() {
        cartRecyclerViewAdapter = CartAdapter(
            onItemClicked = { position: Int, itemAtPosition: CartItem ->

            }, onMinusButtonClicked = { position: Int, itemAtPosition: CartItem ->
                Toast.makeText(requireContext(), "${itemAtPosition.quantity}", Toast.LENGTH_SHORT).show()
                viewModel.updateCartItemQuantity(itemAtPosition.copy(quantity = itemAtPosition.quantity - 1))
            }, onPlusButtonClicked = { position: Int, itemAtPosition: CartItem ->
                Toast.makeText(requireContext(), "${itemAtPosition.quantity}", Toast.LENGTH_SHORT).show()
                viewModel.updateCartItemQuantity(itemAtPosition.copy(quantity = itemAtPosition.quantity + 1))
            })
    }
}