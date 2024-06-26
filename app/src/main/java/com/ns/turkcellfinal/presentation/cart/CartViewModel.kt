package com.ns.turkcellfinal.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.local.model.CartEntity
import com.ns.turkcellfinal.domain.usecase.local.cart.AddToCartUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.DecrementQuantityUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.DeleteAllItemsInCartUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.DeleteFromCartUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.GetCartUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.GetTotalDiscountUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.GetTotalPriceUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.GetTotalQuantityUseCase
import com.ns.turkcellfinal.domain.usecase.local.cart.IncrementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val deleteFromCartUseCase: DeleteFromCartUseCase,
    private val incrementUseCase: IncrementUseCase,
    private val decrementQuantityUseCase: DecrementQuantityUseCase,
    private val getTotalPriceUseCase: GetTotalPriceUseCase,
    private val getTotalQuantityUseCase: GetTotalQuantityUseCase,
    private val getTotalDiscountUseCase: GetTotalDiscountUseCase,
    private val deleteAllItemsInCartUseCase: DeleteAllItemsInCartUseCase
) : ViewModel() {

    private var _cart: MutableStateFlow<ViewState<BaseResponse<List<CartEntity>>>> =
        MutableStateFlow(ViewState.Loading)
    val cart = _cart.asStateFlow()

    private val _productQuantities = mutableMapOf<Int, MutableLiveData<Int>>()
    val productQuantities: Map<Int, LiveData<Int>> = _productQuantities

    private var _totalPrice: MutableLiveData<Double?> = MutableLiveData(0.0)
    val totalPrice: LiveData<Double?>
        get() = _totalPrice

    private var _totalQuantity: MutableLiveData<Int?> = MutableLiveData(0)
    val totalQuantity: LiveData<Int?>
        get() = _totalQuantity

    private var _totalDiscount: MutableLiveData<Double?> = MutableLiveData(0.0)
    val totalDiscount: LiveData<Double?>
        get() = _totalDiscount

    init {
        getCart()
        getTotalPrice()
        getTotalQuantity()
        getTotalDiscount()
    }

    fun getCart() {
        _cart.value = ViewState.Loading
        viewModelScope.launch {
            try {
                getCartUseCase().map { productList ->
                    productList.forEach { product ->
                        _productQuantities[product.id] = MutableLiveData(product.quantity)
                    }
                    ViewState.Success(BaseResponse.Success(productList)) as ViewState<BaseResponse<List<CartEntity>>>
                }.catch { e ->
                    emit(ViewState.Error(e.message ?: "Error"))
                }.collect { viewState ->
                    _cart.emit(viewState)
                }
            } catch (e: Exception) {
                _cart.value = ViewState.Error(e.message.toString())
            }
        }
    }

    fun addToCart(cartEntity: CartEntity) {
        viewModelScope.launch {
            addToCartUseCase(cartEntity)
        }
    }

    fun deleteFromCart(cartEntity: CartEntity) {
        viewModelScope.launch {
            deleteFromCartUseCase(cartEntity.id)
            _productQuantities.remove(cartEntity.id)
            getTotalPrice()
            getTotalQuantity()
        }
    }

    fun increaseProductQuantity(cartEntity: CartEntity) {
        viewModelScope.launch {
            incrementUseCase(cartEntity.id)
            val newQuantity = (productQuantities[cartEntity.id]?.value ?: cartEntity.quantity) + 1
            _productQuantities[cartEntity.id]?.value = newQuantity
            _totalPrice.value = _totalPrice.value?.plus(cartEntity.price ?: 0.0)
            _totalQuantity.value = _totalQuantity.value?.plus(1)

            val discountPrice =
                (cartEntity.price ?: 0.0) * (cartEntity.discountPercentage ?: 0.0) / 100
            _totalDiscount.value = _totalDiscount.value?.plus(discountPrice)
        }
    }

    fun decreaseProductQuantity(cartEntity: CartEntity) {
        viewModelScope.launch {
            val currentQuantity = productQuantities[cartEntity.id]?.value ?: cartEntity.quantity
            if (currentQuantity == 1) {
                deleteFromCart(cartEntity)
                return@launch
            }
            decrementQuantityUseCase(cartEntity.id)
            val newQuantity = currentQuantity - 1
            _productQuantities[cartEntity.id]?.value = newQuantity
            _totalPrice.value = _totalPrice.value?.minus(cartEntity.price ?: 0.0)
            _totalQuantity.value = _totalQuantity.value?.minus(1)

            val discountPrice =
                (cartEntity.price ?: 0.0) * (cartEntity.discountPercentage ?: 0.0) / 100
            _totalDiscount.value = _totalDiscount.value?.minus(discountPrice)
        }
    }

    fun getTotalPrice() {
        viewModelScope.launch {
            val totalPrice = getTotalPriceUseCase()
            _totalPrice.value = totalPrice
        }
    }

    fun getTotalQuantity() {
        viewModelScope.launch {
            val totalQuantity = getTotalQuantityUseCase()
            _totalQuantity.value = totalQuantity
        }
    }

    fun getTotalDiscount() {
        viewModelScope.launch {
            val totalDiscount = getTotalDiscountUseCase()
            _totalDiscount.value = totalDiscount
        }
    }

    fun deleteAllItemsInCart() {
        viewModelScope.launch {
            deleteAllItemsInCartUseCase()
            _productQuantities.clear()
            _totalPrice.value = 0.0
            _totalQuantity.value = 0
            _totalDiscount.value = 0.0
        }
    }
}