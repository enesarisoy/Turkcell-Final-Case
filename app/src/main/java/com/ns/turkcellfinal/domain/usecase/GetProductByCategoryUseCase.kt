package com.ns.turkcellfinal.domain.usecase

import com.ns.turkcellfinal.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductByCategoryUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(title: String) = productRepository.getProductByCategory(title)
}