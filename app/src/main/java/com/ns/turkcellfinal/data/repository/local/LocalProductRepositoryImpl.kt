package com.ns.turkcellfinal.data.repository.local

import com.ns.turkcellfinal.data.local.ProductsDao
import com.ns.turkcellfinal.data.local.model.CartEntity
import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.di.IoDispatcher
import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
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

    override fun getCart(): Flow<List<CartEntity>> {
        return productsDao.getCart()
            .catch { e ->
                throw Exception(e.message ?: "Error")
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun addToCart(cart: CartEntity) {
        withContext(ioDispatcher) {
            productsDao.addToCart(cart)
        }
    }

    override suspend fun deleteFromCart(id: Int) {
        withContext(ioDispatcher) {
            productsDao.deleteFromCart(id)
        }
    }

    override suspend fun deleteAllItemsInCart() {
        withContext(ioDispatcher) {
            productsDao.deleteAllItemsInCart()
        }
    }

    override suspend fun incrementQuantity(id: Int) {
        withContext(ioDispatcher) {
            productsDao.incrementQuantity(id)
        }
    }

    override suspend fun decrementQuantity(id: Int) {
        withContext(ioDispatcher) {
            productsDao.decrementQuantity(id)
        }
    }

    override suspend fun getTotalPrice(): Double? {
        return withContext(ioDispatcher) {
            productsDao.getTotalPrice()
        }
    }

    override suspend fun getTotalQuantity(): Int? {
        return withContext(ioDispatcher) {
            productsDao.getTotalQuantity()
        }
    }

    override suspend fun getTotalDiscount(): Double? {
        return withContext(ioDispatcher) {
            productsDao.getTotalDiscount()
        }
    }

    override suspend fun getCartItemById(id: Int): CartEntity? {
        return withContext(ioDispatcher) {
            productsDao.getCartItemById(id)
        }
    }
}