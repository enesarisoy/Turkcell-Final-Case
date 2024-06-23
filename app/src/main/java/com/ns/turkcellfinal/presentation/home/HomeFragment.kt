package com.ns.turkcellfinal.presentation.home

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.core.util.gone
import com.ns.turkcellfinal.core.util.showToast
import com.ns.turkcellfinal.core.util.visible
import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.databinding.FragmentHomeBinding
import com.ns.turkcellfinal.databinding.ItemHomeCategoriesBinding
import com.ns.turkcellfinal.databinding.ItemHomeSearchBinding
import com.ns.turkcellfinal.databinding.ItemProductsBinding
import com.ns.turkcellfinal.databinding.ItemProductsViewBinding
import com.ns.turkcellfinal.presentation.adapter.SingleRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {

    private val viewModel: HomeViewModel by viewModels()
    private val productsInFavorites = mutableListOf<ProductEntity>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initAdapters()
        initClick()

        getData()


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
                    setCategoriesChips(binding)
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
                setSize(48.dpToPx(), 48.dpToPx())
            }
            chipIcon.background = drawable
            chipIcon.setPadding(12.dpToPx(), 12.dpToPx(), 12.dpToPx(), 12.dpToPx())

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


    private fun setCategoriesChips(binding: ItemHomeCategoriesBinding) {
        val categories = listOf(
            Category(R.drawable.ic_beauty, "Beauty", Color.parseColor("#ECFDF5"), "beauty"),
            Category(
                R.drawable.ic_fragrances,
                "Fragrances",
                Color.parseColor("#FFEDED"),
                "fragrances"
            ),
            Category(
                R.drawable.ic_furniture,
                "Furniture",
                Color.parseColor("#ECFDF5"),
                "furniture"
            ),
            Category(
                R.drawable.ic_groceries,
                "Groceries",
                Color.parseColor("#FFF7ED"),
                "groceries"
            ),
            Category(
                R.drawable.ic_home_decoration,
                "Home\nDecoration",
                Color.parseColor("#FFF7ED"),
                "home-decoration"
            ),
            Category(
                R.drawable.ic_kitchen,
                "Kitchen\nAccessories",
                ResourcesCompat.getColor(resources, R.color.chip_gray_bg, null),
                "kitchen-accessories"
            ),
            Category(
                R.drawable.ic_laptop,
                "Laptops",
                ResourcesCompat.getColor(resources, R.color.chip_orange_bg, null),
                "laptops"
            ),
            Category(
                R.drawable.ic_mens_shirt,
                "Men's\nShirts",
                ResourcesCompat.getColor(resources, R.color.chip_blue_bg, null),
                "mens-shirts"
            ),
            Category(
                R.drawable.ic_men_watch,
                "Men's\nWatches",
                ResourcesCompat.getColor(resources, R.color.chip_blue_bg, null),
                "mens-watches"
            ),
            Category(
                R.drawable.ic_mobile_accessories,
                "Mobile\nAccessories",
                ResourcesCompat.getColor(resources, R.color.chip_gray_bg, null),
                "mobile-accessories"
            ),
            Category(
                R.drawable.ic_motorcycle,
                "Motorcycle",
                ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
                "motorcycle"
            ),
            Category(
                R.drawable.ic_skin_care,
                "Skin\nCare",
                ResourcesCompat.getColor(resources, R.color.chip_orange_bg, null),
                "skin-care"
            ),
            Category(
                R.drawable.ic_smartphone,
                "Smartphones",
                ResourcesCompat.getColor(resources, R.color.chip_gray_bg, null),
                "smartphones"
            ),
            Category(
                R.drawable.ic_sport,
                "Sports\nAccessories",
                ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
                "sports-accessories"
            ),
            Category(
                R.drawable.ic_sunglasses,
                "Sunglasses",
                ResourcesCompat.getColor(resources, R.color.chip_gray_bg, null),
                "sunglasses"
            ),
            Category(
                R.drawable.ic_tablet,
                "Tablets",
                ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
                "tablets"
            ),
            Category(
                R.drawable.ic_tops,
                "Tops",
                ResourcesCompat.getColor(resources, R.color.chip_blue_bg, null),
                "tops"
            ),
            Category(
                R.drawable.ic_vehicle,
                "Vehicle",
                ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
                "vehicle"
            ),
            Category(
                R.drawable.ic_women_bags,
                "Women's\nBags",
                ResourcesCompat.getColor(resources, R.color.chip_red_bg, null),
                "womens-bags"
            ),
            Category(
                R.drawable.ic_tops,
                "Women's\nDresses",
                ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
                "womens-dresses"
            ),
            Category(
                R.drawable.ic_jewellery,
                "Women's\nJewellery",
                ResourcesCompat.getColor(resources, R.color.chip_yellow_bg, null),
                "womens-jewellery"
            ),
            Category(
                R.drawable.ic_women_shoes,
                "Women's\nShoes",
                ResourcesCompat.getColor(resources, R.color.chip_red_bg, null),
                "womens-shoes"
            ),
            Category(
                R.drawable.ic_watch_women,
                "Women's\nWatches",
                ResourcesCompat.getColor(resources, R.color.chip_orange_bg, null),
                "womens-watches"
            )
        )
        addChips(binding, categories)
    }


    data class Category(val iconResId: Int, val text: String, val color: Int, val slug: String)

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun initClick() {
        with(binding) {
            toolbar.ivHamburgerMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
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

                    else -> {
                        requireContext().showToast("Coming soon")
                    }
                }
                drawerLayout.closeDrawers()
                true
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