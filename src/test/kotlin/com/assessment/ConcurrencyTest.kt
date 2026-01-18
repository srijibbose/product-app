package com.assessment

import com.assessment.model.DiscountRequest
import com.assessment.model.Product
import com.assessment.route.configureRouting
import com.assessment.plugin.configureSerialization
import com.assessment.route.ApplicationResponse
import com.assessment.service.ProductResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.junit.Test
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConcurrencyTest {

//    companion object {
//        @Container
//        val mongoDBContainer = MongoDBContainer("mongo:6.0").apply {
//            start()
//        }
//    }

    @Test
    fun testConcurrentDiscountApplication() = testApplication {
        environment {
            config = MapApplicationConfig(
                "db.mongo.host" to "127.0.0.1",
                "db.mongo.port" to "27017",
                "db.mongo.database.name" to "Products"
            )
        }
        
        application {
//            configureDatabases()
            configureSerialization()
            configureRouting()
        }
        
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // 1. Create a product
        val product = Product(
            name = "Test Product",
            basePrice = 100.0,
            country = "Sweden"
        )
        
        val createResponse = client.post("/products") {
            contentType(ContentType.Application.Json)
            setBody(product)
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val responseBody = createResponse.body<Map<String, String>>()
        val productId = responseBody["id"] ?: throw IllegalStateException("No ID returned")

        // 2. Apply the same discount concurrently
        val discount = DiscountRequest(
            discountId = "abc"
        )

        val numberOfThreads = 20
        runBlocking {
            val jobs = (1..numberOfThreads).map {
                async(Dispatchers.IO) {
                    client.put("/products/$productId/discount") {
                        contentType(ContentType.Application.Json)
                        setBody(discount)
                    }
                }
            }
            jobs.awaitAll()
        }

        // 3. Verify the product has the discount applied exactly once
        val getResponse = client.get("/products?country=Sweden")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        
        val body = getResponse.bodyAsText()
        println(body)
        val productsResponse = jacksonObjectMapper().readValue<ApplicationResponse>(body)
        println(productsResponse.data!!.toJSON())
        val products = jacksonObjectMapper().readValue<List<ProductResponse>>(productsResponse.data!!.toJSON())
        println(products)
        val targetProduct = products.find { it.id == productId }

        assertTrue(targetProduct != null, "Product not found")

        val discounts = targetProduct.discounts
        assertEquals(1, discounts.size, "Invalid product or discount already applied")
        assertEquals("abc", discounts[0].discountId)
//        val data = products["data"] as List<Map<String, Any>>
//        val targetProduct = data.find { it["id"] == productId }
//
//        assertTrue(targetProduct != null, "Product not found")
//
//        val discounts = targetProduct!!["discounts"] as List<Map<String, Any>>
//        assertEquals(1, discounts.size, "Invalid product or discount already applied")
//        assertEquals("SUMMER2025", discounts[0]["discountId"])
    }
}

fun Any.toJSON(): String
{
    return jacksonObjectMapper().writeValueAsString(this)
}