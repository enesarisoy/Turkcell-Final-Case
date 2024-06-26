package com.ns.turkcellfinal.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val dbId: Int = 0,
    val id: Int,
    val brand: String?,
    val category: String?,
    val description: String?,
    val price: Double?,
    val rating: Double?,
    val thumbnail: String?,
    val title: String?,
    val discountPercentage: Double?,
    var quantity: Int = 1
)