package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.*
import io.ktor.server.application.*

/**
 * The main entry point for the application.
 */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureRouting()
    DatabaseFactory.init(environment)
}
