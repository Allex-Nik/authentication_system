package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.*
import com.example.repositories.UserRepository
import com.example.routes.loginRoute
import com.example.routes.registerRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database



fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    configureMonitoring()
    configureSerialization()
    configureDatabases()
    configureSecurity()
    configureRouting()

    DatabaseFactory.init(environment)
    val userRepository = UserRepository()
    routing {
        registerRoute(userRepository)
        loginRoute(userRepository)
    }
}

