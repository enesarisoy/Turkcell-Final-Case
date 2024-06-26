package com.ns.turkcellfinal.presentation.search

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
import com.ns.turkcellfinal.domain.usecase.local.favorites.AddToFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.local.favorites.DeleteFromFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.local.favorites.GetProductsFromFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.remote.product.GetProductByCategoryUseCase
import com.ns.turkcellfinal.domain.usecase.remote.product.SearchProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductUseCase: SearchProductUseCase,
    private val getProductByCategoryUseCase: GetProductByCategoryUseCase,
    private val getProductsFromFavoritesUseCase: GetProductsFromFavoritesUseCase,
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase
) : ViewModel() {

    private var _products: MutableStateFlow<ViewState<BaseResponse<ProductResponse>>> =
        MutableStateFlow(ViewState.Loading)
    val products = _products.asStateFlow()

    private val _productsFromFavorites = MutableLiveData<List<ProductEntity>>()
    val productsFromFavorites: LiveData<List<ProductEntity>> get() = _productsFromFavorites

    fun getProductByCategory(title: String) {
        _products.value = ViewState.Loading

        getProductByCategoryUseCase(title).map { response ->
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
    }

    fun addToFavorites(product: Product) {
        val productEntity = product.toProductEntity()
        viewModelScope.launch {
            addToFavoritesUseCase(productEntity)
        }
    }

    fun deleteFromFavorites(product: Product) {
        viewModelScope.launch {
            deleteFromFavoritesUseCase(product.id)
        }
    }

    fun getProductsFromFavorites() {
        viewModelScope.launch {
            getProductsFromFavoritesUseCase().collect { products ->
                _productsFromFavorites.value = products
            }
        }
    }
}