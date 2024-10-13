package com.example.plugins

import com.example.repositories.UserRepository
import com.example.routes.authRoutes
import com.example.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.respondText

fun Application.configureRouting() {
    val userRepository = UserRepository()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        authRoutes(userRepository)
        userRoutes(userRepository)
    }
}
