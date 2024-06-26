package com.ns.turkcellfinal.presentation.home

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.core.util.gone
import com.ns.turkcellfinal.core.util.hideKeyboard
import com.ns.turkcellfinal.core.util.showToast
import com.ns.turkcellfinal.core.util.visible
import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.databinding.FragmentHomeBinding
import com.ns.turkcellfinal.databinding.ItemHomeCategoriesBinding
import com.ns.turkcellfinal.databinding.ItemHomeSearchBinding
import com.ns.turkcellfinal.databinding.ItemProductsBinding
import com.ns.turkcellfinal.databinding.ItemProductsViewBinding
import com.ns.turkcellfinal.presentation.account.UserManager
import com.ns.turkcellfinal.presentation.adapter.SingleRecyclerAdapter
import com.ns.turkcellfinal.presentation.home.util.Category
import com.ns.turkcellfinal.presentation.home.util.dpToPx
import com.ns.turkcellfinal.presentation.home.util.setCategoriesChips
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {

    private val viewModel: HomeViewModel by viewModels()
    private val productsInFavorites = mutableListOf<ProductEntity>()

    @Inject
    lateinit var userManager: UserManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initAdapters()
        initClick()

        getData()

        checkQuantity()
    }

    private fun checkQuantity() {
        with(binding.toolbar) {

            viewModel.getTotalQuantity()
            viewModel.totalQuantity.observe(viewLifecycleOwner) {
                it?.let {
                    tvBadge.visible()
                    tvBadge.text = it.toString()
                } ?: run {
                    tvBadge.gone()
                    "0"
                }
            }
        }
    }

    private fun getData() {
        with(binding) {
            viewModel.getProductsFromFavorites()

            viewModel.productsFromFavorites.observe(viewLifecycleOwner, Observer { products ->
                productsInFavorites.clear()
                productsInFavorites.addAll(products)
            })

            viewModel.getProducts()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.products.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { response ->
                        when (response) {
                            is ViewState.Loading -> {
                                progressBar.visible()
                            }

                            is ViewState.Success -> {
                                val result = response.result as BaseResponse.Success
                                val products = result.data.products
                                progressBar.gone()
                                itemProductListingAdapter.data = products

                                products.forEach { product ->
                                    productsInFavorites.forEach { productInFavorites ->
                                        if (product.id == productInFavorites.id) {
                                            product.isLiked = true
                                        }
                                    }
                                }
                            }

                            is ViewState.Error -> {
                                progressBar.gone()
                                requireContext().showToast(response.error)
                            }
                        }
                    }
            }

            val header = navView.getHeaderView(0)
            val tvUserName = header.findViewById<TextView>(R.id.tvUserName)
            val ivProfilePhoto = header.findViewById<ImageView>(R.id.ivProfilePhoto)

            tvUserName.text = userManager.user?.username
            Glide.with(requireContext())
                .load(userManager.user?.image)
                .into(ivProfilePhoto)
        }
    }

    private val itemSearchAdapter =
        SingleRecyclerAdapter<ItemHomeSearchBinding, String>(
            { inflater, _, _ ->
                ItemHomeSearchBinding.inflate(
                    inflater,
                    binding.rvProducts,
                    false
                )
            },
            { binding, _ ->
                binding.apply {
                    tlSearch.isEndIconVisible = false

                    etSearch.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            // Do nothing
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            // Perform search when text changes
                            if (s.isNullOrEmpty()) {
                                tlSearch.isEndIconVisible = false
                            } else {
                                tlSearch.isEndIconVisible = true
                                tlSearch.setEndIconOnClickListener {
                                    etSearch.text?.clear()
                                    etSearch.clearFocus()
                                    requireView().hideKeyboard()
                                }
                            }
                            s?.let {
                                viewModel.searchProduct(it.toString())
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {
                            // Do nothing

                        }
                    })

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.products.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                            .collect { response ->
                                when (response) {
                                    is ViewState.Loading -> {
                                        Log.d("Loading", "Loading")
                                    }

                                    is ViewState.Success -> {
                                        val result = response.result as BaseResponse.Success
                                        val products = result.data.products
                                        itemProductListingAdapter.data = products

                                        products.forEach { product ->
                                            productsInFavorites.forEach { productInFavorites ->
                                                if (product.id == productInFavorites.id) {
                                                    product.isLiked = true
                                                }
                                            }
                                        }
                                    }

                                    is ViewState.Error -> {
                                        requireContext().showToast(response.error)
                                    }
                                }
                            }
                    }
                }
            }
        )

    private val itemHomeCategoriesAdapter =
        SingleRecyclerAdapter<ItemHomeCategoriesBinding, String>(
            { inflater, _, _ ->
                ItemHomeCategoriesBinding.inflate(
                    inflater,
                    binding.rvProducts,
                    false
                )
            },
            { binding, _ ->
                binding.apply {
                    val list = setCategoriesChips(resources)
                    addChips(binding, list)

                    tvViewAll.setOnClickListener {
                        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
                    }
                }
            }
        )

    private val productListingAdapter = SingleRecyclerAdapter<ItemProductsBinding, String>(
        { inflater, _, _ ->
            ItemProductsBinding.inflate(
                inflater,
                binding.rvProducts,
                false
            )
        },
        { binding, _ ->
            binding.apply {
                rvItemProducts.apply {
                    layoutManager = GridLayoutManager(binding.root.context, 2)
                    adapter = itemProductListingAdapter
                    setHasFixedSize(true)
                }
            }
        }
    )

    private val itemProductListingAdapter =
        SingleRecyclerAdapter<ItemProductsViewBinding, Product>(
            { inflater, _, _ ->
                ItemProductsViewBinding.inflate(
                    inflater,
                    binding.rvProducts,
                    false
                )
            },
            { binding, product ->
                binding.apply {
                    Glide.with(root.context)

                        .load(product.thumbnail)
                        .into(ivProduct)
                    tvProductName.text = product.title
                    tvProductPrice.text = "$${product.price}"
                    tvRating.text = product.rating.toString()
                    tvReviewsCount.text = "(${product.reviews?.size})"

                    if (product.isLiked) {
                        ivLike.setImageResource(R.drawable.ic_like_filled)
                    } else {
                        ivLike.setImageResource(R.drawable.ic_like_empty)
                    }

                    ivLike.setOnClickListener {

                        if (product.isLiked) {
                            viewModel.deleteFromFavorites(product)
                            ivLike.setImageResource(R.drawable.ic_like_empty)
                        } else {
                            viewModel.addToFavorites(product)
                            ivLike.setImageResource(R.drawable.ic_like_filled)
                        }
                        product.isLiked = !product.isLiked

                    }

                    root.setOnClickListener {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                                null,
                                product
                            )
                        )
                    }
                }

            }
        )


    private fun addChips(binding: ItemHomeCategoriesBinding, categories: List<Category>) {
        val inflater = LayoutInflater.from(requireContext())
        for (category in categories) {
            val chip = inflater.inflate(
                R.layout.item_vertical_chip,
                binding.chipGroup,
                false
            ) as LinearLayout

            val chipIcon = chip.findViewById<ImageView>(R.id.chipIcon)
            val chipText = chip.findViewById<TextView>(R.id.chipText)

            chipIcon.setImageResource(category.iconResId)
            chipText.text = category.text

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(category.color)
                setSize(48.dpToPx(resources), 48.dpToPx(resources))
            }
            chipIcon.background = drawable
            chipIcon.setPadding(
                12.dpToPx(resources),
                12.dpToPx(resources),
                12.dpToPx(resources),
                12.dpToPx(resources)
            )

            chip.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                        category.text,
                        category.slug
                    )
                )
            }

            binding.chipGroup.addView(chip)
        }
    }


    private fun initClick() {
        with(binding) {
            toolbar.ivHamburgerMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            toolbar.ivCart.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
            }

            toolbar.ivNotification.setOnClickListener {
                requireContext().showToast("Coming soon")
            }

            navView.setNavigationItemSelectedListener { menuItem ->
                menuItem.isChecked = true

                when (menuItem.itemId) {
                    R.id.nav_categories -> {
                        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
                        drawerLayout.closeDrawers()
                    }

                    R.id.nav_my_orders -> {
                        findNavController().navigate(R.id.action_homeFragment_to_ordersFragment)
                        drawerLayout.closeDrawers()
                    }

                    R.id.nav_favorites -> {
                        findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
                        drawerLayout.closeDrawers()
                    }

                    R.id.nav_logout -> {
                        userManager.clearUser()
                        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                        drawerLayout.closeDrawers()
                    }

                    else -> {
                        requireContext().showToast("Coming soon")
                    }
                }
                drawerLayout.closeDrawers()
                true
            }

            val headerView = navView.getHeaderView(0)
            headerView.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                drawerLayout.closeDrawers()
            }

        }
    }

    private fun initListener() {
        binding.rvProducts.adapter = concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        itemSearchAdapter,
        itemHomeCategoriesAdapter,
        productListingAdapter
    )

    private fun initAdapters() {
        binding.apply {
            itemSearchAdapter.data = listOf("itemSearchAdapter")
            itemHomeCategoriesAdapter.data = listOf("itemHomeCategoriesAdapter")
            productListingAdapter.data = listOf("productListingAdapter")
        }
    }
}