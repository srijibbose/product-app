package com.assessment.repository

import com.assessment.model.Discount
import com.assessment.model.Product
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters

class DiscountRepository(mongoDatabase: MongoDatabase) {

    private val collection: MongoCollection<Discount> = mongoDatabase
        .getCollection("discounts", Discount::class.java)


    suspend fun findByDiscountID(id: String): Discount? {
        return collection.find(Filters.eq("discountId", id)).firstOrNull()
    }
}