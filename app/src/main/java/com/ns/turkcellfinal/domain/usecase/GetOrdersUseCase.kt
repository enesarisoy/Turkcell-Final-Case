package com.ns.turkcellfinal.domain.usecase

import com.ns.turkcellfinal.domain.repository.ProductRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val productRepository: ProductRepository
){
    operator fun invoke() = productRepository.getOrders()
}