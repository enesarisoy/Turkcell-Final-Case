package com.ns.turkcellfinal.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.model.login.LoginResponse
import com.ns.turkcellfinal.domain.usecase.remote.login.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _user: MutableStateFlow<ViewState<BaseResponse<LoginResponse>>> =
        MutableStateFlow(ViewState.Loading)
    val user = _user.asStateFlow()

    fun getUserInfo(token: String) {
        getUserInfoUseCase(token).map { response ->
            when (response) {
                is BaseResponse.Success -> {
                    ViewState.Success(response)
                }

                is BaseResponse.Error -> {
                    ViewState.Error(response.message)
                }
            }
        }.onEach { data ->
            _user.emit(data)
        }.launchIn(viewModelScope)
    }
}