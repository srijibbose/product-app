package com.assessment.service

import com.assessment.model.BusinessException
import com.assessment.model.Discount
import com.assessment.model.DiscountRequest
import com.assessment.model.Product
import com.assessment.repository.DiscountRepository
import com.assessment.repository.ProductRepository
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val basePrice: Double,
    val country: String,
    val discounts: List<Discount>,
    val finalPrice: Double
)

class ProductService(
    private val productRepository: ProductRepository,
    private val discountRepository: DiscountRepository
) {

    private val vatRates = mapOf(
        "Sweden" to 0.25,
        "Germany" to 0.19,
        "France" to 0.20
    )

    fun create(product: Product): String {
        return productRepository.create(product)
    }

    fun getProductsByCountry(country: String): List<ProductResponse> {
        val products = productRepository.findByCountry(country)
        println(products)
        return products.map { product ->
            calculateFinalPrice(product)
        }
    }

    suspend fun applyDiscount(productId: String, discountRequest: DiscountRequest): Boolean {
        val discount = discountRepository.findByDiscountID(id = discountRequest.discountId)
        if(discount == null){
            throw BusinessException("Invalid DiscountID provided")
        }
        val success =  productRepository.addDiscount(productId, discount)
        if(!success){
            throw BusinessException("Invalid product or discount already applied")
        }
        return true
    }

    private fun calculateFinalPrice(product: Product): ProductResponse {
        val vatRate = vatRates[product.country] ?: 0.0
        var totalDiscountPercent = product.discounts.sumOf { it.percent } / 100.0

        // finalPrice = basePrice * (1 - totalDiscount%) * (1 + VAT%)
        if(totalDiscountPercent >100){
            totalDiscountPercent = 100.0
        }
        val priceAfterDiscount = product.basePrice * (1 - totalDiscountPercent)
        val finalPrice = priceAfterDiscount * (1 + vatRate)

        return ProductResponse(
            id = product.id,
            name = product.name,
            basePrice = product.basePrice,
            country = product.country,
            discounts = product.discounts,
            finalPrice = finalPrice
        )
    }
}
