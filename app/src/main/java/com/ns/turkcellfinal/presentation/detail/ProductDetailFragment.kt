package com.ns.turkcellfinal.presentation.detail

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.core.util.formatDate
import com.ns.turkcellfinal.core.util.gone
import com.ns.turkcellfinal.core.util.showSnackbar
import com.ns.turkcellfinal.core.util.showStrikeThrough
import com.ns.turkcellfinal.core.util.showToast
import com.ns.turkcellfinal.core.util.visible
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.databinding.FragmentProductDetailBinding
import com.ns.turkcellfinal.presentation.core.calculatePrice
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::inflate
) {

    private val args: ProductDetailFragmentArgs by navArgs()
    private val viewModel: ProductDetailViewModel by activityViewModels()
    private lateinit var product: Product

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClick()

        args.product?.let {
            showData(it)
            getData(it)
        } ?: run {
            viewModel.getSingleProduct(args.productId!!.toInt())
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.product.collect { response ->
                    when (response) {
                        is ViewState.Success -> {
                            val result = response.result as BaseResponse.Success
                            val data = result.data
                            product = data
                            binding.progressBar.gone()
                            showData(data)
                            getData(data)
                        }

                        is ViewState.Error -> {
                            binding.progressBar.gone()
                            requireContext().showToast(response.error)
                        }

                        ViewState.Loading -> {
                            binding.progressBar.visible()
                        }
                    }
                }
            }
        }

        getTotalQuantity()


    }

    private fun getTotalQuantity() {
        viewModel.getTotalQuantity()
        viewModel.totalQuantity.observe(viewLifecycleOwner) {
            it?.let {
                binding.toolbar.tvBadge.visible()
                binding.toolbar.tvBadge.text = it.toString()
            } ?: run {
                binding.toolbar.tvBadge.gone()
                "0"
            }
        }
    }

    private fun getData(product: Product) {
        with(binding) {
            val favorite = viewModel.checkProductIsFavorite(product.id)
            viewLifecycleOwner.lifecycleScope.launch {
                favorite.collect { isFavorite ->
                    if (isFavorite) {
                        toolbar.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_like_outline_filled
                            )
                        )
                    } else {
                        toolbar.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_like_outline)
                        )
                    }

                    toolbar.ivLike.setOnClickListener {

                        if (product.isLiked) {
                            viewModel.deleteFromFavorites(product)
                            toolbar.ivLike.setImageResource(R.drawable.ic_like_empty)
                        } else {
                            viewModel.addToFavorites(product)
                            toolbar.ivLike.setImageResource(R.drawable.ic_like_filled)
                        }
                        product.isLiked = !product.isLiked

                    }
                }
            }
        }
    }


    private fun initClick() {
        with(binding) {
            toolbar.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            toolbar.ivShare.setOnClickListener {
                requireContext().showToast("Not yet implemented..")
            }

            btnBuyNow.setOnClickListener {
                viewModel.buyProduct()
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.buyProduct.collect { response ->
                        when (response) {
                            is ViewState.Success -> {
                                val result = response.result as BaseResponse.Success
                                val data = result.data
                                requireContext().showToast("Product bought successfully..")
                            }

                            is ViewState.Error -> {
                                requireContext().showToast(response.error)
                            }

                            ViewState.Loading -> {

                            }
                        }
                    }
                }
            }

            btnAddToCart.setOnClickListener {
                viewModel.addToCart(args.product ?: product)
                getTotalQuantity()
                requireView().showSnackbar("Product added to cart..")
            }

            btnRate.setOnClickListener {
                requireContext().showToast("Not yet implemented..")
            }

            toolbar.ivCart.setOnClickListener {
                findNavController().navigate(
                    R.id.action_productDetailFragment_to_cartFragment
                )
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun showData(product: Product) {
        with(binding) {

            tvDiscountPrice.showStrikeThrough(true)
            tvProductName.text = product.title
            tvProductPrice.text = "$${product.price}"
            tvRating.text = product.rating.toString()
            tvReviewsCount.text = "${product.reviews?.size} Reviews"
            tvBrandName.text = product.brand
            tvDescription.text = product.description

            val discountPrice = product.calculatePrice()

            tvDiscountPrice.text = discountPrice.let { "$${it}" }
            tvDiscountPercent.text = product.discountPercentage?.let { "${it}% OFF" }

            val rating = product.rating.toString().take(3)
            tvRating2.text = rating

            tvRatingsCount.text = "${product.reviews?.size} Ratings"
            tvViewAllReviews.text = "View all ${product.reviews?.size} reviews"

            product.reviews?.maxByOrNull {
                it.rating
            }?.let { review ->

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tvReviewerName.text = "${review.reviewerName}, ${review.date.formatDate()}"
                } else {
                    tvReviewerName.text = "${review.reviewerName}"
                }

                tvReviewTitle.text = review.comment
                addStarsToLayout(review.rating.toFloat())
            }

            carouselView.registerLifecycle(lifecycle)

            carouselView.setData(product.images?.map { imageUrl ->
                Glide.with(requireContext())
                    .load(imageUrl)
                    .apply(RequestOptions().placeholder(R.drawable.ic_placeholder))
                    .preload()

                CarouselItem(
                    imageUrl = imageUrl
                )
            } ?: emptyList())
        }
    }

    private fun addStarsToLayout(rating: Float) {
        val starFilled = ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_filled)
        val starEmpty = ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_empty)

        val starContainer: LinearLayout = binding.llStarRating
        starContainer.removeAllViews()

        val fullStars = rating.toInt()

        for (i in 1..fullStars) {
            val imageView = createStarImageView(starFilled)
            starContainer.addView(imageView)
        }

        val totalStars = 5
        val remainingStars = totalStars - fullStars
        for (i in 1..remainingStars) {
            val imageView = createStarImageView(starEmpty)
            starContainer.addView(imageView)
        }
    }

    private fun createStarImageView(drawable: Drawable?): ImageView {
        val imageView = ImageView(requireContext())
        imageView.setImageDrawable(drawable)
        val marginInDp = 4
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, marginInDp.toFloat(), resources.displayMetrics
        ).toInt()
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(marginInPx, 0, marginInPx, 0)
        imageView.layoutParams = layoutParams
        return imageView
    }

}