package com.ns.turkcellfinal.domain.usecase

import com.ns.turkcellfinal.domain.repository.ProductRepository
import javax.inject.Inject

class SearchProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
){
    operator fun invoke(query: String) = productRepository.searchProduct(query)
}