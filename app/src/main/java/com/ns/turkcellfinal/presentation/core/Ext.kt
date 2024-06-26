package com.ns.turkcellfinal.presentation.core

import android.annotation.SuppressLint
import com.ns.turkcellfinal.data.local.model.CartEntity
import com.ns.turkcellfinal.data.model.product.Product

@SuppressLint("DefaultLocale")
fun Product.calculatePrice(): String? {
    return discountPercentage?.let {
        val calculatedPrice =
            price?.plus((price * it / 100)) ?: price
        String.format("%.2f", calculatedPrice)
    }
}

@SuppressLint("DefaultLocale")
fun CartEntity.calculatePrice(): String? {
    return discountPercentage?.let {
        val calculatedPrice =
            price?.plus((price * it / 100)) ?: price
        String.format("%.2f", calculatedPrice)
    }
}