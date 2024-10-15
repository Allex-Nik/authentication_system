package com.example.plugins

import com.example.repositories.UserRepository
import com.example.routes.authRoutes
import com.example.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

/**
 * Configures the routing for the application, defining available routes and serving static files.
 */
fun Application.configureRouting() {
    val userRepository = UserRepository()

    routing {
        authRoutes(userRepository) // Routes related to authentication (register, login, logout)
        userRoutes(userRepository) // Routes related to user management (get, update, delete user info)

        // Serves static files from the frontend directory, with index.html as the default file
        staticFiles("/", File("src/main/resources/frontend")) {
            default("index.html")
        }
    }
}
