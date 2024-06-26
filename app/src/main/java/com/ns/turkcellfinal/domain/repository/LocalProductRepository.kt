package com.ns.turkcellfinal.domain.repository

import com.ns.turkcellfinal.data.local.model.CartEntity
import com.ns.turkcellfinal.data.local.model.ProductEntity
import kotlinx.coroutines.flow.Flow

interface LocalProductRepository {

    suspend fun addToFavorites(product: ProductEntity)

    suspend fun deleteFromFavorites(id: Int)

    suspend fun deleteAllItemsInFavorites()

    fun getProductsFromFavorites(): Flow<List<ProductEntity>>

    fun checkProductIsFavorite(id: Int): Flow<Boolean>

    fun getCart(): Flow<List<CartEntity>>

    suspend fun addToCart(cart: CartEntity)

    suspend fun deleteFromCart(id: Int)

    suspend fun deleteAllItemsInCart()

    suspend fun incrementQuantity(id: Int)

    suspend fun decrementQuantity(id: Int)

    suspend fun getTotalPrice(): Double?

    suspend fun getTotalQuantity(): Int?

    suspend fun getTotalDiscount(): Double?

    suspend fun getCartItemById(id: Int): CartEntity?
}