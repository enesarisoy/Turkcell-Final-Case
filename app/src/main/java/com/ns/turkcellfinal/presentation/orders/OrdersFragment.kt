package com.ns.turkcellfinal.presentation.orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.core.util.getCurrentDate
import com.ns.turkcellfinal.core.util.gone
import com.ns.turkcellfinal.core.util.showToast
import com.ns.turkcellfinal.core.util.visible
import com.ns.turkcellfinal.databinding.FragmentOrdersBinding
import com.ns.turkcellfinal.databinding.ItemOrdersBinding
import com.ns.turkcellfinal.databinding.ItemOrdersViewBinding
import com.ns.turkcellfinal.presentation.adapter.SingleRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : BaseFragment<FragmentOrdersBinding>(
    FragmentOrdersBinding::inflate
) {

    private val viewModel: OrdersViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initAdapters()

        getData()
    }


    private fun getData() {
        with(binding) {
            viewModel.getOrders()

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.orders.collect { response ->
                    when (response) {
                        is ViewState.Loading -> {
                            progressBar.visible()
                        }

                        is ViewState.Success -> {
                            val result = response.result as BaseResponse.Success
                            val data = result.data
                            progressBar.gone()

                            itemOrdersAdapter.data = data.carts[0].products
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

    private val orderFragmentAdapter = SingleRecyclerAdapter<ItemOrdersBinding, String>(
        { inflater, _, _ ->
            ItemOrdersBinding.inflate(
                inflater,
                binding.rvOrders,
                false
            )
        },
        { binding, _ ->
            binding.apply {
                rvItemOrders.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = itemOrdersAdapter
                    setHasFixedSize(true)
                }
            }
        }
    )

    private val itemOrdersAdapter =
        SingleRecyclerAdapter<ItemOrdersViewBinding, com.ns.turkcellfinal.data.model.carts.Product>(
            { inflater, _, _ ->
                ItemOrdersViewBinding.inflate(
                    inflater,
                    binding.rvOrders,
                    false
                )
            },
            { binding, order ->
                binding.apply {

                    Glide.with(root.context)
                        .load(order.thumbnail)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(ivOrderImage)
                    tvOrderName.text = order.title
                    tvOrderPrice.text = "$${order.price}"
                    val date = getCurrentDate()
                    tvOrderDate.text = date

                    llShipping.setOnClickListener {
                        TransitionManager.beginDelayedTransition(rootLayout)
                        if (llOrderDetails.visibility == View.GONE) {
                            llOrderDetails.visibility = View.VISIBLE
                        } else {
                            llOrderDetails.visibility = View.GONE
                        }
                    }

                    btnCancel.setOnClickListener {
                        requireContext().showToast("Order is canceled")
                    }

                    btnTrack.setOnClickListener {
                        requireContext().showToast("Order is tracking")
                    }

                }

            }
        )

    private fun initListener() {
        binding.rvOrders.adapter = concatAdapter
    }

    private val concatAdapter = ConcatAdapter(
        orderFragmentAdapter,
    )

    private fun initAdapters() {
        binding.apply {
            orderFragmentAdapter.data = listOf("Orders")
        }
    }
}