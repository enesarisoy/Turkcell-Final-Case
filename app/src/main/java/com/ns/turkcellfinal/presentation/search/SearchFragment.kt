package com.ns.turkcellfinal.presentation.search

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.core.util.showToast
import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.databinding.FragmentSearchBinding
import com.ns.turkcellfinal.databinding.ItemProductsBinding
import com.ns.turkcellfinal.databinding.ItemProductsViewBinding
import com.ns.turkcellfinal.presentation.adapter.SingleRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(
    FragmentSearchBinding::inflate
) {

    private val viewModel: SearchViewModel by activityViewModels()
    private val args: SearchFragmentArgs by navArgs()
    private val productsFromFavorite = mutableListOf<ProductEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initAdapters()
        initClick()
        getData()


    }

    private val productListingAdapter = SingleRecyclerAdapter<ItemProductsBinding, String>(
        { inflater, _, _ ->
            ItemProductsBinding.inflate(
                inflater,
                binding.rvSearch,
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
                    binding.rvSearch,
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
                        val action =
                            SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(
                                null,
                                product
                            )
                        findNavController().navigate(action)
                    }
                }

            }
        )


    private fun getData() {

        viewModel.getProductsFromFavorites()

        viewModel.productsFromFavorites.observe(viewLifecycleOwner) { products ->
            productsFromFavorite.clear()
            productsFromFavorite.addAll(products)
        }

        viewModel.getProductByCategory(args.slug)
        val formattedText = args.query?.replace("\n", " ")
        binding.toolbarSearch.tvTitle.text = formattedText

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { response ->
                when (response) {
                    is ViewState.Success -> {
                        val result = response.result as BaseResponse.Success
                        val products = result.data.products

                        itemProductListingAdapter.data = products

                        products.forEach { product ->
                            productsFromFavorite.forEach { productInFavorites ->
                                if (product.id == productInFavorites.id) {
                                    product.isLiked = true
                                }
                            }
                        }
                    }

                    is ViewState.Error -> {
                        requireContext().showToast(response.error)
                    }

                    is ViewState.Loading -> {
                        // Handle loading state
                    }
                }
            }
        }


    }

    private fun initClick() {
        with(binding) {
            toolbarSearch.ivBack.setOnClickListener {
                findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
            }
        }
    }

    private fun initListener() {
        binding.rvSearch.adapter = concatAdapter
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