package com.ns.turkcellfinal.domain.usecase.local.favorites

import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import javax.inject.Inject

class DeleteAllItemsInFavoritesUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) {

    suspend operator fun invoke() =
        localProductRepository.deleteAllItemsInFavorites()
}