package com.ns.turkcellfinal.domain.repository

import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.data.local.model.ProductEntity
import kotlinx.coroutines.flow.Flow

interface LocalProductRepository {

    suspend fun addToFavorites(product: ProductEntity)

    suspend fun deleteFromFavorites(id: Int)

    suspend fun deleteAllItemsInFavorites()

    fun getProductsFromFavorites(): Flow<List<ProductEntity>>

    fun checkProductIsFavorite(id: Int): Flow<Boolean>
}