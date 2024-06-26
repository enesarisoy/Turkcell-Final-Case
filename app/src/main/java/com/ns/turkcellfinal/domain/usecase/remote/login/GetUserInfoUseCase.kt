package com.ns.turkcellfinal.domain.usecase.remote.login

import com.ns.turkcellfinal.domain.repository.ProductRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val productRepository: ProductRepository
){
    operator fun invoke(token: String) = productRepository.getUserInfo(token)
}