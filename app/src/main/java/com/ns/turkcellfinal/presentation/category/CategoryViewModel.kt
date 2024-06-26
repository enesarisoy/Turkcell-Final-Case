package com.ns.turkcellfinal.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.model.category.CategoryResponse
import com.ns.turkcellfinal.domain.usecase.remote.product.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private var _categories: MutableStateFlow<ViewState<BaseResponse<CategoryResponse>>> =
        MutableStateFlow(ViewState.Loading)
    val categories = _categories.asStateFlow()

    fun getCategories() {
        _categories.value = ViewState.Loading

        try {
            getCategoriesUseCase().map { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        ViewState.Success(response)
                    }

                    is BaseResponse.Error -> {
                        ViewState.Error(response.message)
                    }
                }
            }.onEach { data ->
                _categories.emit(data)
            }.launchIn(viewModelScope)
        } catch (e: Exception) {
            _categories.value = ViewState.Error(e.message.toString())
        }
    }
}
