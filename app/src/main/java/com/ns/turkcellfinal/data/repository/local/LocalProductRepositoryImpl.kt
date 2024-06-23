package com.ns.turkcellfinal.data.repository.local

import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.data.local.ProductsDao
import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.di.IoDispatcher
import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalProductRepositoryImpl @Inject constructor(
    private val productsDao: ProductsDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalProductRepository {

    override fun getProductsFromFavorites(): Flow<List<ProductEntity>> {
        return productsDao.getProductsFromFavorites()
            .catch { e ->
                throw Exception(e.message ?: "Error")
            }
            .flowOn(ioDispatcher)
    }


    override suspend fun addToFavorites(product: ProductEntity) {
        withContext(ioDispatcher) {
            productsDao.addToFavorites(product)
        }
    }

    override suspend fun deleteFromFavorites(id: Int) {
        withContext(ioDispatcher) {
            productsDao.deleteFromFavorites(id)
        }
    }

    override suspend fun deleteAllItemsInFavorites() {
        withContext(ioDispatcher) {
            productsDao.deleteAllItemsInFavorites()
        }
    }

    override fun checkProductIsFavorite(id: Int): Flow<Boolean> {
        return productsDao.checkProductIsFavorite(id)
    }
}