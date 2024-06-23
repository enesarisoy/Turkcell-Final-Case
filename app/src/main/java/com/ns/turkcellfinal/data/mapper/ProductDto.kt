package com.ns.turkcellfinal.data.mapper

import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.data.model.product.Product

fun Product.toProductEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        brand = this.brand,
        category = this.category,
        description = this.description,
        price = this.price,
        rating = this.rating,
        thumbnail = this.thumbnail,
        title = this.title
    )
}