package com.ns.turkcellfinal.domain.usecase.local.cart

import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import javax.inject.Inject

class DeleteFromCartUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) {
    suspend operator fun invoke(id: Int) = localProductRepository.deleteFromCart(id)
}
