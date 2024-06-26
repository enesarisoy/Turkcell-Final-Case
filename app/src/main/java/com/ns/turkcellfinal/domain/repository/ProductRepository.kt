package com.ns.turkcellfinal.domain.repository

import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.data.model.buy.BuyResponse
import com.ns.turkcellfinal.data.model.carts.CartsResponse
import com.ns.turkcellfinal.data.model.category.CategoryResponse
import com.ns.turkcellfinal.data.model.login.LoginResponse
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.data.model.product.ProductResponse
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<BaseResponse<ProductResponse>>

    fun getCategories(): Flow<BaseResponse<CategoryResponse>>

    fun searchProduct(query: String): Flow<BaseResponse<ProductResponse>>

    fun getProductByCategory(title: String): Flow<BaseResponse<ProductResponse>>

    fun getSingleProduct(id: Int): Flow<BaseResponse<Product>>

    fun getOrders(): Flow<BaseResponse<CartsResponse>>

    fun buyProduct(): Flow<BaseResponse<BuyResponse>>

    fun login(username: String, password: String): Flow<BaseResponse<LoginResponse>>

    fun getUserInfo(token: String): Flow<BaseResponse<LoginResponse>>
}