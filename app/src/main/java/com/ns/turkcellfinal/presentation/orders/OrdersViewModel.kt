package com.ns.turkcellfinal.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.model.carts.CartsResponse
import com.ns.turkcellfinal.domain.usecase.remote.product.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private var _orders: MutableStateFlow<ViewState<BaseResponse<CartsResponse>>> =
        MutableStateFlow(ViewState.Loading)
    val orders = _orders.asStateFlow()

    fun getOrders() {
        _orders.value = ViewState.Loading
        try {
            getOrdersUseCase().map { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        ViewState.Success(response)
                    }

                    is BaseResponse.Error -> {
                        ViewState.Error(response.message)
                    }
                }
            }.onEach { data ->
                _orders.emit(data)
            }.launchIn(viewModelScope)
        } catch (e: Exception) {
            _orders.value = ViewState.Error(e.message.toString())
        }
    }

}