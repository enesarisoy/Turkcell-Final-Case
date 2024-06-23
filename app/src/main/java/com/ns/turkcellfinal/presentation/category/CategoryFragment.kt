package com.ns.turkcellfinal.presentation.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.model.category.CategoryItem
import com.ns.turkcellfinal.databinding.FragmentCategoryBinding
import com.ns.turkcellfinal.databinding.ItemCategoriesBinding
import com.ns.turkcellfinal.databinding.ItemCategoriesViewBinding
import com.ns.turkcellfinal.presentation.adapter.SingleRecyclerAdapter
import kotlinx.coroutines.launch

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(
    FragmentCategoryBinding::inflate
) {

    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initAdapters()
        initClick()
        getData()

    }

    private fun getData() {
        with(binding) {
            viewModel.getCategories()

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.categories.collect { response ->
                    when (response) {
                        is ViewState.Loading -> {
                            // Handle loading
                        }

                        is ViewState.Success -> {
                            // Handle success
                            val result = response.result as BaseResponse.Success
                            val data = result.data

                            itemCategoryAdapter.data = data
                        }

                        is ViewState.Error -> {
                            // Handle error
                        }
                    }
                }

            }
        }
    }


    private val categoryAdapter = SingleRecyclerAdapter<ItemCategoriesBinding, String>(
        { inflater, _, _ ->
            ItemCategoriesBinding.inflate(
                inflater,
                binding.rvCategories,
                false
            )
        },
        { binding, _ ->
            binding.apply {
                rvItemCategories.apply {
                    layoutManager = GridLayoutManager(root.context, 3)
                    adapter = itemCategoryAdapter
                    setHasFixedSize(true)
                }
            }
        }
    )

    private val itemCategoryAdapter =
        SingleRecyclerAdapter<ItemCategoriesViewBinding, CategoryItem>(
            { inflater, _, _ ->
                ItemCategoriesViewBinding.inflate(
                    inflater,
                    binding.rvCategories,
                    false
                )
            },
            { binding, category ->
                binding.apply {
                    tvCategoryName.text = category.name

                    val categoryImageMap = getCategoryImageMap()

                    categoryImageMap[category.name]?.let { drawableRes ->
                        Glide.with(root).load(drawableRes).into(ivCategoryImage)
                    }

                    root.setOnClickListener {
                        val action =
                            CategoryFragmentDirections.actionCategoryFragmentToSearchFragment(
                                category.name,
                                category.slug
                            )
                        findNavController().navigate(action)
                    }
                }

            }
        )

    private fun initClick() {
        with(binding) {
            toolbar.root.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun getCategoryImageMap(): Map<String, Int> {
        return mapOf(
            "Beauty" to R.drawable.category_beauty,
            "Fragrances" to R.drawable.category_fragrances,
            "Furniture" to R.drawable.category_furniture,
            "Groceries" to R.drawable.category_groceries,
            "Home Decoration" to R.drawable.category_home_decoration,
            "Kitchen Accessories" to R.drawable.category_kitchen_accessories,
            "Laptops" to R.drawable.category_laptop,
            "Mens Shirts" to R.drawable.category_mens_shirt,
            "Mens Shoes" to R.drawable.category_mens_shoes,
            "Mens Watches" to R.drawable.category_mens_watches,
            "Mobile Accessories" to R.drawable.category_mobile_accessories,
            "Motorcycle" to R.drawable.category_motorcycle,
            "Skin Care" to R.drawable.category_skin_care,
            "Smartphones" to R.drawable.category_smartphones,
            "Sports Accessories" to R.drawable.category_sport_accessories,
            "Sunglasses" to R.drawable.category_sunglasses,
            "Tablets" to R.drawable.category_tablets,
            "Tops" to R.drawable.category_tops,
            "Vehicle" to R.drawable.category_vehicle,
            "Womens Bags" to R.drawable.category_womens_bags,
            "Womens Dresses" to R.drawable.category_womens_dresses,
            "Womens Jewellery" to R.drawable.category_womens_jewellery,
            "Womens Shoes" to R.drawable.category_womens_shoes,
            "Womens Watches" to R.drawable.category_womens_watches
        )
    }

    private fun initListener() {
        binding.rvCategories.adapter = concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        categoryAdapter,
    )

    private fun initAdapters() {
        binding.apply {
            categoryAdapter.data = listOf("CategoryAdapter")
        }
    }
}