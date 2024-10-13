package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.repositories.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.time.ZoneOffset


fun Application.configureSecurity() {
    val jwtConfig = environment.config.config("jwt")
    val secret = jwtConfig.property("secret").getString()
    val issuer = jwtConfig.property("issuer").getString()
    val audience = jwtConfig.property("audience").getString()
    val jwtRealm = jwtConfig.property("realm").getString()
    val userRepository = UserRepository()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                val tokenIssuedAt = credential.payload.issuedAt?.toInstant()
                if (username != null && tokenIssuedAt != null) {
                    val user = userRepository.findUserByUsername(username)
                    if (user != null) {
                        val lastLogoutTime = user.lastLogoutTime?.toInstant(ZoneOffset.UTC)
                        if (lastLogoutTime == null || tokenIssuedAt.isAfter(lastLogoutTime)) {
                            JWTPrincipal(credential.payload)
                        }
                        else {
                            null // Token is invalid: user logged out
                        }
                    } else {
                        null // User not found
                    }
                } else {
                    null
                }
            }
        }
    }
}
