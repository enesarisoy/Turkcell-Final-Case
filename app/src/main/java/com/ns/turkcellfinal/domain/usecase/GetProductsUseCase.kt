package com.ns.turkcellfinal.domain.usecase

import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.data.model.product.ProductResponse
import com.ns.turkcellfinal.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<BaseResponse<ProductResponse>> =
        productRepository.getProducts()
}
