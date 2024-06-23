package com.ns.turkcellfinal.domain.usecase.local

import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import javax.inject.Inject

class GetProductsFromFavoritesUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) {

    operator fun invoke() =
        localProductRepository.getProductsFromFavorites()
}