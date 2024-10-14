package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.*
import com.example.repositories.UserRepository
import com.example.routes.loginRoute
import com.example.routes.registerRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*



fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    configureMonitoring()
    configureSerialization()
//    configureDatabases()
    configureSecurity()
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
        //anyHost()
    }
    configureRouting()

    DatabaseFactory.init(environment)
    val userRepository = UserRepository()
    routing {
        registerRoute(userRepository)
        loginRoute(userRepository)
    }
}

