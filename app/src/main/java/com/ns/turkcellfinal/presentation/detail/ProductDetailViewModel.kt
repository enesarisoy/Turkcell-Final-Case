package com.ns.turkcellfinal.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.mapper.toCartEntity
import com.ns.turkcellfinal.data.mapper.toProductEntity
import com.ns.turkcellfinal.data.model.buy.BuyResponse
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.domain.usecase.local.cart.AddToCartUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.GetCartItemByIdUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.GetTotalQuantityUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.IncrementUseCase
import com.ns.turkcellfinal.domain.usecase.local.favorites.AddToFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.local.favorites.CheckProductIsFavoriteUseCase
import com.ns.turkcellfinal.domain.usecase.local.favorites.DeleteFromFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.remote.product.BuyProductUseCase
import com.ns.turkcellfinal.domain.usecase.remote.product.GetSingleProductUseCase
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
    private val buyProductUseCase: BuyProductUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val getTotalQuantityUseCase: GetTotalQuantityUseCase,
    private val getCartItemByIdUseCase: GetCartItemByIdUseCase,
    private val incrementUseCase: IncrementUseCase
) : ViewModel() {

    private var _product: MutableStateFlow<ViewState<BaseResponse<Product>>> =
        MutableStateFlow(ViewState.Loading)
    val product = _product.asStateFlow()

    private var _buyProduct: MutableStateFlow<ViewState<BaseResponse<BuyResponse>>> =
        MutableStateFlow(ViewState.Loading)
    val buyProduct = _buyProduct.asStateFlow()

    private var _totalQuantity: MutableLiveData<Int?> = MutableLiveData(0)
    val totalQuantity: LiveData<Int?>
        get() = _totalQuantity

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

    fun addToCart(product: Product) {
        val cart = product.toCartEntity()
        viewModelScope.launch {
            val isExists = getCartItemByIdUseCase(cart.id)
            isExists?.let {
                incrementUseCase(cart.id)
//                _totalQuantity.value = _totalQuantity.value?.plus(1)
            } ?: run {
//                _totalQuantity.value = _totalQuantity.value?.plus(1)
                addToCartUseCase(cart)
            }
            getTotalQuantity()
        }
    }

    fun getTotalQuantity() {
        viewModelScope.launch {
            val totalQuantity = getTotalQuantityUseCase()
            _totalQuantity.value = totalQuantity
        }
    }
}