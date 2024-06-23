package com.ns.turkcellfinal.data.mapper

import com.ns.turkcellfinal.data.local.model.ProductEntity
import com.ns.turkcellfinal.data.model.product.Product

fun ProductEntity.toProduct(): Product {
    return Product(
        id = this.id,
        brand = this.brand,
        category = this.category,
        description = this.description,
        price = this.price,
        rating = this.rating,
        thumbnail = this.thumbnail,
        title = this.title,
        availabilityStatus = null,
        dimensions = null,
        discountPercentage = null,
        images = null,
        meta = null,
        minimumOrderQuantity = null,
        returnPolicy = null,
        reviews = null,
        shippingInformation = null,
        sku = null,
        stock = null,
        tags = null,
        warrantyInformation = null,
        weight = null
    )
}