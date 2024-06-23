package com.ns.turkcellfinal.di

import com.ns.turkcellfinal.data.local.ProductsDao
import com.ns.turkcellfinal.data.remote.ApiService
import com.ns.turkcellfinal.data.repository.local.LocalProductRepositoryImpl
import com.ns.turkcellfinal.data.repository.remote.ProductRepositoryImpl
import com.ns.turkcellfinal.domain.repository.LocalProductRepository
import com.ns.turkcellfinal.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ApiService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): ProductRepository {
        return ProductRepositoryImpl(apiService, dispatcher)
    }

    @Provides
    @Singleton
    fun provideLocalRepository(
        productsDao: ProductsDao,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): LocalProductRepository {
        return LocalProductRepositoryImpl(productsDao, dispatcher)
    }

}