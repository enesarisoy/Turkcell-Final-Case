package com.ns.turkcellfinal.presentation.cart

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.core.util.gone
import com.ns.turkcellfinal.core.util.showStrikeThrough
import com.ns.turkcellfinal.core.util.showToast
import com.ns.turkcellfinal.core.util.visible
import com.ns.turkcellfinal.data.local.model.CartEntity
import com.ns.turkcellfinal.databinding.CustomAlertDialogBinding
import com.ns.turkcellfinal.databinding.FragmentCartBinding
import com.ns.turkcellfinal.databinding.ItemCartBinding
import com.ns.turkcellfinal.databinding.ItemCartPriceDetailsBinding
import com.ns.turkcellfinal.databinding.ItemCartViewBinding
import com.ns.turkcellfinal.databinding.ItemDeliverBinding
import com.ns.turkcellfinal.presentation.adapter.SingleRecyclerAdapter
import com.ns.turkcellfinal.presentation.core.calculatePrice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(
    FragmentCartBinding::inflate
) {

    private val viewModel: CartViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initAdapters()
        initClick()
        getData()
    }


    private fun getData() {
        with(binding) {

            viewModel.getCart()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.cart.collect { response ->
                    when (response) {
                        is ViewState.Loading -> {
                            Log.d("Loading", "Loading")
                        }

                        is ViewState.Success -> {
                            val result = response.result as BaseResponse.Success
                            val cartList = result.data

                            itemCartListingAdapter.data = cartList
                            if (cartList.isEmpty()) {
                                layoutEmptyBasket.visible()
                                rvCart.gone()
                                itemCartTotalAmount.btnContinue.apply {
                                    isEnabled = false
                                    setBackgroundColor(
                                        ResourcesCompat.getColor(
                                            resources,
                                            R.color.disabled,
                                            null
                                        )
                                    )
                                    setTextColor(
                                        ResourcesCompat.getColor(
                                            resources,
                                            R.color.disabled_text,
                                            null
                                        )
                                    )
                                }
                            } else {
                                layoutEmptyBasket.gone()
                                rvCart.visible()
                            }
                        }

                        is ViewState.Error -> {
                            requireContext().showToast(response.error)
                        }
                    }
                }
            }

            viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
                val price = totalPrice?.let {
                    String.format("%.2f", it.plus(8))
                } ?: "0.0"
                binding.itemCartTotalAmount.tvTotalAmountValue.text = "$${price}"
            }

        }
    }

    private val itemDeliverAdapter = SingleRecyclerAdapter<ItemDeliverBinding, String>(
        { inflater, _, _ ->
            ItemDeliverBinding.inflate(
                inflater,
                binding.rvCart,
                false
            )
        },
        { binding, _ ->
            binding.apply {

            }
        }
    )

    private val cartListingAdapter = SingleRecyclerAdapter<ItemCartBinding, String>(
        { inflater, _, _ ->
            ItemCartBinding.inflate(
                inflater,
                binding.rvCart,
                false
            )
        },
        { binding, _ ->
            binding.apply {
                rvItemCart.apply {
                    layoutManager = LinearLayoutManager(binding.root.context)
                    adapter = itemCartListingAdapter
                    setHasFixedSize(true)
                }
            }
        }
    )

    private val itemCartListingAdapter =
        SingleRecyclerAdapter<ItemCartViewBinding, CartEntity>(
            { inflater, _, _ ->
                ItemCartViewBinding.inflate(
                    inflater,
                    binding.rvCart,
                    false
                )
            },
            { binding, product ->
                binding.apply {
                    Glide.with(binding.root.context)
                        .load(product.thumbnail)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivCartImage)

                    tvTitle.text = product.title
                    tvProductPrice.text = "$${product.price}"

                    val tvDiscount = product.calculatePrice()
                    tvDiscountPrice.showStrikeThrough(true)
                    tvDiscountPrice.text = tvDiscount.let { "$${it}" }
                    tvDiscountPercent.text = product.discountPercentage?.let { "${it}% OFF" }

                    observeQuantity(binding, product.id)

                    ivMinus.setOnClickListener {
                        viewModel.decreaseProductQuantity(product)
                    }

                    ivPlus.setOnClickListener {
                        viewModel.increaseProductQuantity(product)
                    }

                    llRemove.setOnClickListener {
                        viewModel.deleteFromCart(product)
                    }
                }

            }
        )


    private val itemCartPriceDetailsAdapter =
        SingleRecyclerAdapter<ItemCartPriceDetailsBinding, String>(
            { inflater, _, _ ->
                ItemCartPriceDetailsBinding.inflate(
                    inflater,
                    binding.rvCart,
                    false
                )
            },
            { binding, _ ->
                binding.apply {
                    viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
                        val price = totalPrice?.let {
                            String.format("%.2f", it)
                        }
                        tvTotalPriceValue.text = "$${price}"
                    }

                    viewModel.totalQuantity.observe(viewLifecycleOwner) { totalQuantity ->
                        tvPriceDetails.text = "Price Details ($totalQuantity items)"
                    }

                    viewModel.totalDiscount.observe(viewLifecycleOwner) { totalDiscount ->
                        val discount = totalDiscount?.let {
                            String.format("%.2f", it)
                        } ?: "0.0"
                        tvDiscountValue.text = "$${discount}"
                    }
                }
            }
        )

    private fun observeQuantity(binding: ItemCartViewBinding, productId: Int) {
        viewModel.productQuantities[productId]?.observe(viewLifecycleOwner) { quantity ->
            binding.tvQuantity.text = quantity.toString()
        }
    }

    private fun initListener() {
        binding.rvCart.adapter = concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        itemDeliverAdapter,
        cartListingAdapter,
        itemCartPriceDetailsAdapter
    )

    private fun initAdapters() {
        binding.apply {
            itemDeliverAdapter.data = listOf("itemDeliverAdapter")
            cartListingAdapter.data = listOf("cartListingAdapter")
            itemCartPriceDetailsAdapter.data = listOf("itemCartPriceDetailsAdapter")
        }
    }

    private fun initClick() {
        with(binding) {
            toolbarCart.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            itemCartTotalAmount.btnContinue.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {

                    progressBar.visible()
                    delay(4000L)
                    progressBar.gone()

                    val alertDialogBinding = CustomAlertDialogBinding.inflate(layoutInflater)


                    val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                        .setView(alertDialogBinding.root)
                        .create()

                    alertDialogBinding.btnOkay.setOnClickListener {
                        viewModel.deleteAllItemsInCart()
                        builder.dismiss()
                        findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
                    }
                    builder.setCanceledOnTouchOutside(false)
                    builder.show()
                }
            }
        }
    }
}
