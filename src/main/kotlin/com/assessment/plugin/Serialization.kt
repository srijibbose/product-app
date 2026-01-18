package com.assessment.plugin

import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization() {

    install(ContentNegotiation) {
        jackson {
            registerModule(KotlinModule.Builder().build())
            dateFormat = StdDateFormat()
        }
    }
}
