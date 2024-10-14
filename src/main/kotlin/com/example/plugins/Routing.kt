package com.example.plugins

import com.example.repositories.UserRepository
import com.example.routes.authRoutes
import com.example.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    val userRepository = UserRepository()

    routing {
        authRoutes(userRepository)
        userRoutes(userRepository)
//        staticFiles("/", File("frontend")) {
//            default("index.html")
//        }

        static("/") {
            staticRootFolder = File("src/main/resources/frontend")
            files(".")
            default("index.html")
        }

    }
}
