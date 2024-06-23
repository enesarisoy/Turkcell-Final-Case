package com.ns.turkcellfinal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
}