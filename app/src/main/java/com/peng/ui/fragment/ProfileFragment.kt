package com.peng.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.peng.databinding.FragmentProfileBinding
import com.peng.model.CartItem
import com.peng.model.FavouriteItem
import com.peng.model.PaymentCard
import com.peng.model.VMResult
import com.peng.ui.adapter.CartAdapter
import com.peng.ui.adapter.FavouriteAdapter
import com.peng.ui.adapter.PaymentCardsAdapter
import com.peng.vm.SharedActivityViewModel


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedActivityViewModel by activityViewModels()
    private lateinit var favouriteRecyclerViewAdapter: FavouriteAdapter
    private lateinit var paymentCardsRecyclerViewAdapter: PaymentCardsAdapter


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

        viewModel.cartItems.observe(viewLifecycleOwner) { result ->
            when(result) {
                is VMResult.Success -> {
                    binding.profileCartQuantityTV.text = result.data.size.toString()
                }
                else -> {}
            }
        }

        initCartAdapter()

        initCartRecyclerViewAdapter()

        initPaymentCardsAdapter()

        initPaymentCardsRecyclerViewAdapter()

        viewModel.favouriteItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is VMResult.Success -> {

                    favouriteRecyclerViewAdapter.submitList(result.data)
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
    }

    private fun initCartRecyclerViewAdapter() {
        binding.profileCartRV.adapter = favouriteRecyclerViewAdapter
    }

    private fun initCartAdapter() {
        favouriteRecyclerViewAdapter = FavouriteAdapter(
            onItemClicked = { position: Int, itemAtPosition: FavouriteItem ->

            }, onFavouriteButtonClicked = { position: Int, itemAtPosition: FavouriteItem ->
                viewModel.addOrRemoveItemFromFavourite(itemAtPosition)
            })
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}