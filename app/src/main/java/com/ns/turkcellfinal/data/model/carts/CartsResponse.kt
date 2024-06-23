package com.ns.turkcellfinal.data.model.carts

data class CartsResponse(
    val carts: List<Cart>,
    val limit: Int,
    val skip: Int,
    val total: Int
)