package com.assessment.route

import com.assessment.connectToMongoDB
import com.assessment.model.Discount
import com.assessment.model.DiscountRequest
import com.assessment.model.Product
import com.assessment.repository.DiscountRepository
import com.assessment.repository.ProductRepository
import com.assessment.service.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val mongoDatabase = connectToMongoDB()
    val productRepository = ProductRepository(mongoDatabase)
    val discountRepository = DiscountRepository(mongoDatabase)
    val productService = ProductService(productRepository, discountRepository)


    routing {
        route("/products") {
            get {
                val country = call.request.queryParameters["country"]
                if (country == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing country parameter")
                    return@get
                }
                val products = productService.getProductsByCountry(country)
                call.fireHttpResponse(products)
            }

            put("/{id}/discount") {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing product ID")
                    return@put
                }
                val discount = call.receive<DiscountRequest>()
                val success = productService.applyDiscount(id, discount)
                if (success) {
                    call.respond(HttpStatusCode.OK, "Discount applied")
                } else {
                    call.respond(HttpStatusCode.OK, "Discount not processed")
                }
            }

            // Helper endpoint to create products for testing
            post {
                try {
                    val product = call.receive<Product>()
                    val id = productService.create(product)
                    call.respond(HttpStatusCode.Created, mapOf("id" to id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid body")
                }
            }
        }

        route("/discounts") {
            post {
                try {
                    val discount = call.receive<Discount>()
                    discountRepository.create(discount)
                    call.respond(HttpStatusCode.Created, "Discount created")
                } catch (e: Exception) {
                    // Handle duplicate key error or other issues
                    call.respond(HttpStatusCode.BadRequest, "Invalid body or discount already exists")
                }
            }
        }
    }
}

data class ApplicationResponse(
    val data: Any?,
    val message: String = "Success"
)

private suspend fun RoutingCall.fireHttpResponse(data: Any?) {
    this.respond(message = ApplicationResponse(data = data))
}
