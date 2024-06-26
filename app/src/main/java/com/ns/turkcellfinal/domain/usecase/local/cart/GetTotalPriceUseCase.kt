package com.ns.turkcellfinal.domain.usecase.local.cart

import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import javax.inject.Inject

class GetTotalPriceUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
){
    suspend operator fun invoke() = localProductRepository.getTotalPrice()
}