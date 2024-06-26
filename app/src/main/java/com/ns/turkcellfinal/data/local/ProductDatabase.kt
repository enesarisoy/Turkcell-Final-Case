package com.ns.turkcellfinal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ns.turkcellfinal.data.local.model.CartEntity
import com.ns.turkcellfinal.data.local.model.ProductEntity

@Database(entities = [ProductEntity::class, CartEntity::class], version = 1, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDao
}