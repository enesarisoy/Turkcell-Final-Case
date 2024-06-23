package com.ns.turkcellfinal.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.data.mapper.toProductEntity
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.domain.usecase.local.AddToFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.local.DeleteFromFavoritesUseCase
import com.ns.turkcellfinal.domain.usecase.local.GetProductsFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getProductsFromFavoritesUseCase: GetProductsFromFavoritesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase
) : ViewModel() {

    private var _favorites: MutableStateFlow<ViewState<BaseResponse<List<ProductEntity>>>> =
        MutableStateFlow(ViewState.Loading)
    val favorites = _favorites.asStateFlow()

    fun getFavorites() {
        _favorites.value = ViewState.Loading

        viewModelScope.launch {
            try {
                getProductsFromFavoritesUseCase().map { productList ->
                    ViewState.Success(BaseResponse.Success(productList)) as ViewState<BaseResponse<List<ProductEntity>>>
                }.catch { e ->
                    emit(ViewState.Error(e.message ?: "Error"))
                }.collect { viewState ->
                    _favorites.emit(viewState)
                }
            } catch (e: Exception) {
                _favorites.value = ViewState.Error(e.message.toString())
            }
        }
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

}