package com.assessment.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.util.UUID

@Serializable
data class Product @BsonCreator constructor(
    @BsonId
    @BsonProperty("_id")
    val id: String = UUID.randomUUID().toString(),

    @BsonProperty("name")
    val name: String,

    @BsonProperty("basePrice")
    val basePrice: Double,

    @BsonProperty("country")
    val country: String,

    @BsonProperty("discounts")
    val discounts: List<Discount> = emptyList()
)

@Serializable
data class DiscountRequest(
    val discountId: String
)

@Serializable
data class Discount @BsonCreator constructor(
    @BsonProperty("discountId") val discountId: String,
    @BsonProperty("percent") val percent: Double
)
