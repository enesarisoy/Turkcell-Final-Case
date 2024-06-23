package com.ns.turkcellfinal.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.mapper.toProductEntity
import com.ns.turkcellfinal.data.model.buy.BuyResponse
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.domain.usecase.BuyProductUseCase
import com.ns.turkcellfinal.domain.usecase.GetSingleProductUseCase
import com.ns.turkcellfinal.domain.usecase.local.AddToFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.local.CheckProductIsFavoriteUseCase
import com.ns.turkcellfinal.domain.usecase.local.DeleteFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val checkProductIsFavoriteUseCase: CheckProductIsFavoriteUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase,
    private val getSingleProductUseCase: GetSingleProductUseCase,
    private val buyProductUseCase: BuyProductUseCase
) : ViewModel() {

    private var _product: MutableStateFlow<ViewState<BaseResponse<Product>>> =
        MutableStateFlow(ViewState.Loading)
    val product = _product.asStateFlow()

    private var _buyProduct: MutableStateFlow<ViewState<BaseResponse<BuyResponse>>> =
        MutableStateFlow(ViewState.Loading)
    val buyProduct = _buyProduct.asStateFlow()

    fun getSingleProduct(id: Int) {
        _product.value = ViewState.Loading

        getSingleProductUseCase(id).map { response ->
            when (response) {
                is BaseResponse.Success -> ViewState.Success(response)
                is BaseResponse.Error -> ViewState.Error(response.message)
            }
        }.onEach { data ->
            _product.emit(data)
        }.launchIn(viewModelScope)
    }

    fun checkProductIsFavorite(productId: Int) = checkProductIsFavoriteUseCase(productId)

    fun addToFavorites(product: Product) {
        val entity = product.toProductEntity()
        viewModelScope.launch {
            addToFavoritesUseCase(entity)
        }
    }

    fun deleteFromFavorites(product: Product) {
        viewModelScope.launch {
            deleteFromFavoritesUseCase(product.id)
        }
    }

    fun buyProduct() {
        _buyProduct.value = ViewState.Loading
        try {
            buyProductUseCase().map { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        ViewState.Success(response)
                    }

                    is BaseResponse.Error -> {
                        ViewState.Error(response.message)
                    }
                }
            }.onEach { data ->
                _buyProduct.emit(data)
            }.launchIn(viewModelScope)
        } catch (e: Exception) {
            _buyProduct.value = ViewState.Error(e.message.toString())
        }
    }
}