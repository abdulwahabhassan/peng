package com.peng.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import com.peng.R
import com.peng.ViewModelFactory
import com.peng.databinding.FragmentProductDetailsBinding
import com.peng.databinding.FragmentProductsBinding
import com.peng.ui.adapter.ProductsAdapter
import com.peng.vm.ProductDetailsFragmentViewModel
import com.peng.vm.ProductsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private val args: ProductDetailsFragmentArgs by navArgs()
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ProductDetailsFragmentViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: FragmentStateAdapter
    private var pageChangeCallBack: ViewPager2.OnPageChangeCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.productDetailsBottomSheetDialog)
        bottomSheetBehavior.peekHeight = 480

        binding.productDetailsFavouriteIV.setOnClickListener { view ->
            view.setBackgroundResource(R.drawable.ic_favourite_selected)
        }

        binding.productDetailsMaterialToolbar.setupWithNavController(findNavController())

        viewPager = binding.productDetailsImagesVP

        viewPagerAdapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
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

        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        viewPager.setPageTransformer(compositePageTransformer)

        TabLayoutMediator(binding.productDetailsImagesTL, viewPager) { tab, tabPosition ->
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
            pageChangeCallBack?.let { viewPager.registerOnPageChangeCallback(it) }
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        pageChangeCallBack?.let { viewPager.unregisterOnPageChangeCallback(it) }
    }

}