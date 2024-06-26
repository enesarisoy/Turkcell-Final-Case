package com.ns.turkcellfinal.domain.usecase.remote.login

import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.data.model.login.LoginResponse
import com.ns.turkcellfinal.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: ProductRepository
){
    operator fun invoke(username: String, password: String): Flow<BaseResponse<LoginResponse>> {
        return repository.login(username, password)
    }
}