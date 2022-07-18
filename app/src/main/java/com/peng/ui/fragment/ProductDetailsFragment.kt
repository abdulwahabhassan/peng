package com.peng.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.peng.R
import com.peng.Utils
import com.peng.ViewModelFactory
import com.peng.databinding.FragmentProductDetailsBinding
import com.peng.model.Product
import com.peng.model.Review
import com.peng.model.mapToCartItem
import com.peng.ui.adapter.ReviewsAdapter
import com.peng.vm.SharedActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.properties.Delegates

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private val args: ProductDetailsFragmentArgs by navArgs()
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SharedActivityViewModel by activityViewModels()
    private lateinit var productImagesViewPager: ViewPager2
    private lateinit var productImagesViewPagerAdapter: FragmentStateAdapter
    private lateinit var reviewsRecyclerViewAdapter: ReviewsAdapter
    private var pageChangeCallBack: ViewPager2.OnPageChangeCallback? = null
    private var isInCart by Delegates.notNull<Boolean>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productDetailsMaterialToolbar.setupWithNavController(findNavController())

        bindViewsToValuesAndActions()

        setUpBottomSheetDialog()

        initProductImagesAdapter()

        initProductImagesViewPagerAdapter()

        setProductImagesViewPagerCompositePageTransformer()

        setUpTabLayoutMediator()

        initReviewsAdapter()

        initRecyclerViewAdapter()

    }

    private fun bindViewsToValuesAndActions() {

        setAddToCartButtonIcon()

        binding.productDetailsAddToCartButton.setOnClickListener {
            isInCart = !isInCart //for addToCartIcon toggle
            addProductToCart()
        }
        binding.productDetailsFavouriteIV.setOnClickListener { view ->
            view.setBackgroundResource(R.drawable.ic_favourite_selected)
        }

        binding.productDetailsCartButton.setOnClickListener {
            val action = ProductDetailsFragmentDirections
                .actionProductDetailsFragmentToCartFragment()
            findNavController().navigate(action)
        }
        binding.productDetailsNameTV.text = args.productName
        binding.productExtraDetailsTV.text = args.productDescription
        binding.productDetailsPriceTV.text = "₦${Utils().formatCurrency(args.productPrice)}"
        binding.productDetailsRB.progress = args.productRating / binding.productDetailsRB.max
    }

    private fun setAddToCartButtonIcon() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            if (viewModel.isItemInCart(args.productId)) {
                isInCart = true
                binding.productDetailsAddToCartButton.setImageResource(R.drawable.ic_added_to_cart)
            }
            else {
                isInCart = false
                binding.productDetailsAddToCartButton.setImageResource(R.drawable.ic_add_to_cart)
            }
        }
    }

    private fun addProductToCart() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            Log.d("PO", "Clicked")
            viewModel.addOrRemoveItemFromCart(
                Product(
                    args.productId,
                    args.productName,
                    args.productDescription,
                    args.productPrice.toDouble(),
                    args.productImage,
                    args.productRating
                ).mapToCartItem()
            )
            if (isInCart)
                binding.productDetailsAddToCartButton.setImageResource(R.drawable.ic_added_to_cart)
            else
                binding.productDetailsAddToCartButton.setImageResource(R.drawable.ic_add_to_cart)
        }
    }

    private fun setUpBottomSheetDialog() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.productDetailsBottomSheetDialog)
        bottomSheetBehavior.peekHeight = 550
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.showBottomSheetButton.visibility = VISIBLE
                    }
                    else -> {
                        binding.showBottomSheetButton.visibility = INVISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                when(slideOffset) {
                    in -1F..0F -> {
                        binding.showBottomSheetButton.visibility = VISIBLE
                        binding.showBottomSheetButton.alpha = abs(slideOffset)
                    }
                }
            }

        })
        binding.showBottomSheetButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setProductImagesViewPagerCompositePageTransformer() {
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        productImagesViewPager.setPageTransformer(compositePageTransformer)
    }

    private fun initProductImagesViewPagerAdapter() {
        productImagesViewPager = binding.productDetailsImagesVP
        productImagesViewPager.adapter = productImagesViewPagerAdapter
        productImagesViewPager.offscreenPageLimit = 3
        productImagesViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private fun initProductImagesAdapter() {

        productImagesViewPagerAdapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            private val fragments = arrayOf(
                ProductImageFragment(""),
                ProductImageFragment(""),
                ProductImageFragment(""),
                ProductImageFragment(""),
                ProductImageFragment(""),
                ProductImageFragment("")
            )

            override fun createFragment(position: Int) = fragments[position]

            override fun getItemCount(): Int = fragments.size
        }


    }

    private fun setUpTabLayoutMediator() {
        TabLayoutMediator(binding.productDetailsImagesTL, productImagesViewPager) { tab, tabPosition ->
            tab.setIcon(R.drawable.ic_tab_indicator_inactive)
            pageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if(tabPosition == position) {
                        tab.setIcon(R.drawable.ic_tab_indicator_active)
                    } else {
                        tab.setIcon(R.drawable.ic_tab_indicator_inactive)
                    }
                }
            }
            pageChangeCallBack?.let { productImagesViewPager.registerOnPageChangeCallback(it) }
        }.attach()
    }

    private fun initRecyclerViewAdapter() {
        binding.reviewsRV.adapter = reviewsRecyclerViewAdapter
        reviewsRecyclerViewAdapter.submitList(Review.reviews.toMutableList())
    }

    private fun initReviewsAdapter() {
        reviewsRecyclerViewAdapter = ReviewsAdapter {position: Int, itemAtPosition: Review ->

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        pageChangeCallBack?.let { productImagesViewPager.unregisterOnPageChangeCallback(it) }
    }

}