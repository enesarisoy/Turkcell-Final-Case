package com.ns.turkcellfinal.data.mapper

import com.ns.turkcellfinal.data.local.model.CartEntity
import com.ns.turkcellfinal.data.model.product.Product

fun Product.toCartEntity(): CartEntity {
    return CartEntity(
        id = id,
        brand = brand,
        price = price,
        category = category,
        description = description,
        rating = rating,
        thumbnail = thumbnail,
        title = title,
        discountPercentage = discountPercentage
    )
}