package com.ns.turkcellfinal.presentation.favorite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.data.mapper.toProduct
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.databinding.FragmentFavoritesBinding
import com.ns.turkcellfinal.databinding.ItemProductsBinding
import com.ns.turkcellfinal.databinding.ItemProductsViewBinding
import com.ns.turkcellfinal.presentation.adapter.SingleRecyclerAdapter
import com.ns.turkcellfinal.presentation.search.SearchFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>(
    FragmentFavoritesBinding::inflate
) {

    private val viewModel: FavoritesViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initAdapters()
        initClick()

        getData()
    }

    private fun getData() {
        viewModel.getFavorites()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collect { response ->
                when (response) {
                    is ViewState.Loading -> {
                    }

                    is ViewState.Success -> {
                        val result = response.result as BaseResponse.Success
                        val data = result.data

                        itemProductListingAdapter.data = data

                    }

                    is ViewState.Error -> {
                    }
                }
            }
        }
    }

    private fun initClick() {
        with(binding) {
            toolbarFavorite.root.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private val productListingAdapter = SingleRecyclerAdapter<ItemProductsBinding, String>(
        { inflater, _, _ ->
            ItemProductsBinding.inflate(
                inflater,
                binding.rvFavorites,
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
        SingleRecyclerAdapter<ItemProductsViewBinding, ProductEntity>(
            { inflater, _, _ ->
                ItemProductsViewBinding.inflate(
                    inflater,
                    binding.rvFavorites,
                    false
                )
            },
            { binding, product ->
                binding.apply {
                    Glide.with(root.context)
                        .load(product.thumbnail)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(ivProduct)
                    tvProductName.text = product.title
                    tvProductPrice.text = "$${product.price}"
                    tvRating.text = product.rating.toString()
                    ivLike.setImageResource(R.drawable.ic_like_filled)
                    // TODO Type Converter
//                    tvReviewsCount.text = "(${product.reviews?.size})"

                    root.setOnClickListener {
                        val action =
                            FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                                product.id.toString(),
                                null
                            )
                        findNavController().navigate(action)
                    }
                }

            }
        )

    private fun initListener() {
        binding.rvFavorites.adapter = concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        productListingAdapter,
    )

    private fun initAdapters() {
        binding.apply {
            productListingAdapter.data = listOf("productListingAdapter")
        }
    }


}