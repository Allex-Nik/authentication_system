package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.repositories.UserRepository
import com.example.security.Hashing
import com.example.models.User

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

// User registration
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

fun Route.registerRoute(userRepository: UserRepository) {
    post("/register") {
        val request = call.receive<RegisterRequest>()

        val existingUser = userRepository.findUserByUsername(request.username)
            ?: userRepository.findUserByEmail(request.email)

        if (existingUser != null) {
            call.respond(HttpStatusCode.BadRequest,
                "There already exists a user with the same username or email")
        }

        val passwordHash = Hashing.hashPassword(request.password)

        val user = User(
            id = 0,
            username = request.username,
            email = request.email,
            passwordHash = passwordHash,
            firstName = request.firstName,
            lastName = request.lastName,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        userRepository.addUser(user)

        call.respond(HttpStatusCode.Created, "The user is successfully registered")
    }
}

//User login
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String
)

fun Route.loginRoute(userRepository: UserRepository) {
    post("/login") {
        val request = call.receive<LoginRequest>()

        val user = userRepository.findUserByUsername(request.username)

        if (user == null || !Hashing.verifyPassword(request.password, user.passwordHash)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong username or password")
            return@post
        }

        //JWT token generation
        val jwtConfig = call.application.environment.config.config("jwt")
        val secret = jwtConfig.property("secret").getString()
        val issuer = jwtConfig.property("issuer").getString()
        val audience = jwtConfig.property("audience").getString()
        val expiresIn = System.currentTimeMillis() + 60000 * 60 * 24 // 24 hours

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", user.username)
            .withExpiresAt(java.util.Date(expiresIn))
            .sign(Algorithm.HMAC256(secret))

        call.respond(AuthResponse(token))
    }
}

fun Route.authRoutes(userRepository: UserRepository) {
    route("/auth") {
        registerRoute(userRepository)
        loginRoute(userRepository)
    }
}
