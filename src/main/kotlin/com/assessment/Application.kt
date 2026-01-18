package com.assessment

import com.assessment.plugin.configureMonitoring
import com.assessment.plugin.configureSerialization
import com.assessment.plugin.configureStatusPages
import com.assessment.route.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
//    configureMonitoring()
    configureRouting()
    configureStatusPages()
}
