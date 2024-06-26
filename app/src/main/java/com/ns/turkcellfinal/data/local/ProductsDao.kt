package com.ns.turkcellfinal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ns.turkcellfinal.data.local.model.CartEntity
import com.ns.turkcellfinal.data.local.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(product: ProductEntity)

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun deleteFromFavorites(id: Int)

    @Query("DELETE FROM product")
    suspend fun deleteAllItemsInFavorites()

    @Query("SELECT * FROM product")
    fun getProductsFromFavorites(): Flow<List<ProductEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM product WHERE id =:id)")
    fun checkProductIsFavorite(id: Int): Flow<Boolean>

    @Query("SELECT * FROM cart")
    fun getCart(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cart: CartEntity)

    @Query("DELETE FROM cart WHERE id = :id")
    suspend fun deleteFromCart(id: Int)

    @Query("DELETE FROM cart")
    suspend fun deleteAllItemsInCart()

    @Query("UPDATE cart SET quantity = quantity + 1 WHERE id = :id")
    suspend fun incrementQuantity(id: Int)

    @Query("UPDATE cart SET quantity = quantity - 1 WHERE id = :id AND quantity > 1")
    suspend fun decrementQuantity(id: Int)

    @Query("SELECT SUM(price * quantity) FROM cart")
    suspend fun getTotalPrice(): Double?

    @Query("SELECT SUM(quantity) FROM cart")
    suspend fun getTotalQuantity(): Int?

    @Query("SELECT SUM((price * (discountPercentage / 100)) * quantity) FROM cart WHERE discountPercentage IS NOT NULL")
    suspend fun getTotalDiscount(): Double?

    @Query("SELECT * FROM cart WHERE id = :id")
    suspend fun getCartItemById(id: Int): CartEntity?
}