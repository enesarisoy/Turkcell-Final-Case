package com.ns.turkcellfinal.data.remote

import com.ns.turkcellfinal.data.model.buy.BuyResponse
import com.ns.turkcellfinal.data.model.carts.CartsResponse
import com.ns.turkcellfinal.data.model.category.CategoryResponse
import com.ns.turkcellfinal.data.model.login.LoginRequest
import com.ns.turkcellfinal.data.model.login.LoginResponse
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.data.model.product.ProductResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("products")
    fun getProducts(): Call<ProductResponse>

    @GET("products/categories")
    fun getCategories(): Call<CategoryResponse>

    @GET("search")
    fun searchProduct(
        @Query("q") query: String
    ): Call<ProductResponse>

    @GET("products/category/{title}")
    fun getProductByCategory(
        @Path("title") title: String
    ): Call<ProductResponse>

    @GET("products/{id}")
    fun getSingleProduct(
        @Path("id") id: Int
    ): Call<Product>

    @GET("carts/user/6")
    fun getOrders(): Call<CartsResponse>

    @POST("products/add")
    fun buyProduct(): Call<BuyResponse>

    @POST("auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>
}
