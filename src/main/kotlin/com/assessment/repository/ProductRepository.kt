package com.assessment.repository

import com.assessment.model.Discount
import com.assessment.model.Product
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.Document

class ProductRepository(mongoDatabase: MongoDatabase) {
    private val collection: MongoCollection<Product> = mongoDatabase.getCollection("products", Product::class.java)

    fun create(product: Product): String {
        collection.insertOne(product)
        return product.id
    }

    fun findByCountry(country: String): List<Product> {
        return collection.find(Filters.eq("country", country)).toList()
    }

    fun findById(id: String): Product? {
        return collection.find(Filters.eq("_id", id)).firstOrNull()
    }

    /**
     * Applies a discount to a product if the discount ID is not already present.
     * This operation is atomic and ensures idempotency.
     * @return true if the discount was applied, false if it was already present or product not found.
     */
    fun addDiscount(productId: String, discount: Discount): Boolean {
        val filter = Filters.and(
            Filters.eq("_id", productId),
            Filters.ne("discounts.discountId", discount.discountId)
        )
        val update = Updates.push("discounts", discount)
        
        val result = collection.updateOne(filter, update)
        return result.modifiedCount > 0
    }
}
