package com.ns.turkcellfinal.data.repository.remote

import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.data.model.buy.BuyResponse
import com.ns.turkcellfinal.data.model.carts.CartsResponse
import com.ns.turkcellfinal.data.model.category.CategoryResponse
import com.ns.turkcellfinal.data.model.login.LoginRequest
import com.ns.turkcellfinal.data.model.login.LoginResponse
import com.ns.turkcellfinal.data.model.product.Product
import com.ns.turkcellfinal.data.model.product.ProductResponse
import com.ns.turkcellfinal.data.remote.ApiService
import com.ns.turkcellfinal.data.remote.Callback
import com.ns.turkcellfinal.di.IoDispatcher
import com.ns.turkcellfinal.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {

    override fun getProducts(): Flow<BaseResponse<ProductResponse>> = callbackFlow {
        apiService.getProducts()
            .enqueue(Callback(this.channel))
        awaitClose { close() }
    }.flowOn(ioDispatcher)

    override fun getCategories(): Flow<BaseResponse<CategoryResponse>> = callbackFlow {
        apiService.getCategories()
            .enqueue(Callback(this.channel))
        awaitClose { close() }
    }.flowOn(ioDispatcher)

    override fun searchProduct(query: String): Flow<BaseResponse<ProductResponse>> = callbackFlow {
        apiService.searchProduct(query)
            .enqueue(Callback(this.channel))
        awaitClose { close() }
    }.flowOn(ioDispatcher)

    override fun getProductByCategory(title: String): Flow<BaseResponse<ProductResponse>> =
        callbackFlow {
            apiService.getProductByCategory(title)
                .enqueue(Callback(this.channel))
            awaitClose { close() }
        }.flowOn(ioDispatcher)

    override fun getSingleProduct(id: Int): Flow<BaseResponse<Product>> = callbackFlow {
        apiService.getSingleProduct(id)
            .enqueue(Callback(this.channel))
        awaitClose { close() }
    }.flowOn(ioDispatcher)

    override fun getOrders(): Flow<BaseResponse<CartsResponse>> = callbackFlow {
        apiService.getOrders()
            .enqueue(Callback(this.channel))
        awaitClose { close() }
    }.flowOn(ioDispatcher)

    override fun buyProduct(): Flow<BaseResponse<BuyResponse>> = callbackFlow {
        apiService.buyProduct()
            .enqueue(Callback(this.channel))
        awaitClose { close() }
    }.flowOn(ioDispatcher)

    override fun login(username: String, password: String): Flow<LoginResponse> = flow {
        val request = LoginRequest(username, password)
        val response = apiService.login(request).execute()
        if (response.isSuccessful) {
            response.body()?.let {
                emit(it)
            }
        } else {
            throw Exception("Login failed")
        }
    }
}
