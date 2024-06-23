package com.ns.turkcellfinal.domain.usecase

import com.ns.turkcellfinal.domain.repository.ProductRepository
import javax.inject.Inject

class BuyProductUseCase @Inject constructor(
    private val repository: ProductRepository
){
    operator fun invoke() = repository.buyProduct()
}