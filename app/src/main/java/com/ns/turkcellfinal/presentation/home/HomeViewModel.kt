package com.ns.turkcellfinal.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.data.mapper.toProductEntity
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.data.model.product.ProductResponse
import com.ns.turkcellfinal.domain.usecase.GetProductsUseCase
import com.ns.turkcellfinal.domain.usecase.local.AddToFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.local.DeleteFromFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.local.GetProductsFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val getProductsFromFavoritesUseCase: GetProductsFromFavoritesUseCase,
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase
) : ViewModel() {

    private var _products: MutableStateFlow<ViewState<BaseResponse<ProductResponse>>> =
        MutableStateFlow(ViewState.Loading)
    val products = _products.asStateFlow()

    private val _productsFromFavorites = MutableLiveData<List<ProductEntity>>()
    val productsFromFavorites: LiveData<List<ProductEntity>> get() = _productsFromFavorites

    fun getProducts() {
        viewModelScope.launch {
            _products.value = ViewState.Loading

            try {
                getProductsUseCase().map { response ->
                    when (response) {
                        is BaseResponse.Success -> {
                            ViewState.Success(response)
                        }

                        is BaseResponse.Error -> {
                            ViewState.Error(response.message)
                        }
                    }
                }.onEach { data ->
                    _products.emit(data)
                }.launchIn(viewModelScope)
            } catch (e: Exception) {
                _products.value = ViewState.Error(e.message.toString())
            }

        }
    }



    fun getProductsFromFavorites() {
        viewModelScope.launch {
            getProductsFromFavoritesUseCase().collect { products ->
                _productsFromFavorites.value = products
            }
        }
    }
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

}