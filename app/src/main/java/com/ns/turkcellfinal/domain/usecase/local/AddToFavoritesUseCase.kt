package com.ns.turkcellfinal.domain.usecase.local

import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import javax.inject.Inject

class AddToFavoritesUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) {

    suspend operator fun invoke(product: ProductEntity) =
        localProductRepository.addToFavorites(product)
}