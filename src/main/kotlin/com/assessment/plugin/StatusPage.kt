package com.assessment.plugin

import com.assessment.model.BusinessException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureStatusPages(){
    install(StatusPages) {
        exception<BusinessException> { call, cause ->
            cause.printStackTrace()
            call.respondText(text = cause.message , status = cause.statusCode)
        }
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
}