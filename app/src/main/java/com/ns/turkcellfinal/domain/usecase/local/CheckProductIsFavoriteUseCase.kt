package com.ns.turkcellfinal.domain.usecase.local

import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import javax.inject.Inject

class CheckProductIsFavoriteUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
){
    operator fun invoke(productId: Int) = localProductRepository.checkProductIsFavorite(productId)
}