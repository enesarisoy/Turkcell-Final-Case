package com.ns.turkcellfinal.domain.usecase.local.cart

import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import javax.inject.Inject

class IncrementUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) {
    suspend operator fun invoke(productId: Int) =
        localProductRepository.incrementQuantity(productId)
}