package com.ns.turkcellfinal.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.data.model.login.LoginResponse
import com.ns.turkcellfinal.domain.usecase.remote.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userManager: UserManager
) : ViewModel() {

    private val _loginState =
        MutableStateFlow<ViewState<BaseResponse<LoginResponse>>>(ViewState.Loading)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ViewState.Loading

            try {
                loginUseCase(username, password).map { response ->
                    when (response) {
                        is BaseResponse.Success -> {
                            userManager.setUser(response.data)
                            ViewState.Success(response)
                        }

                        is BaseResponse.Error -> {
                            ViewState.Error(response.message)
                        }
                    }
                }.onEach { data ->
                    _loginState.emit(data)
                }.launchIn(viewModelScope)
            } catch (e: Exception) {
                _loginState.value = ViewState.Error(e.message.toString())
            }
        }
    }
}