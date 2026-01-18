package com.assessment.model

import io.ktor.http.HttpStatusCode

data class BusinessException(
    override val message: String,
    val statusCode: HttpStatusCode = HttpStatusCode.BadRequest
): Exception(message)
