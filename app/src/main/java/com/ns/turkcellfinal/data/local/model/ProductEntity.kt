package com.ns.turkcellfinal.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class ProductEntity(
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
)