package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.repositories.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.time.ZoneOffset

/**
 * This function sets up JWT (JSON Web Token) authentication, defining how tokens are validated and ensuring
 * that only valid users can access protected routes.
 */
fun Application.configureSecurity() {
    // Retrieve JWT configuration parameters from application environment
    val jwtConfig = environment.config.config("jwt")
    val secret = jwtConfig.property("secret").getString()
    val issuer = jwtConfig.property("issuer").getString()
    val audience = jwtConfig.property("audience").getString()
    val jwtRealm = jwtConfig.property("realm").getString()

    // Create an instance of UserRepository to interact with user data
    val userRepository = UserRepository()

    // Install the Authentication plugin with JWT configuration
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm

            // Configure JWT verification using the secret, issuer, and audience from the config
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )

            // Define how the JWT should be validated
            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                val tokenIssuedAt = credential.payload.issuedAt?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                if (username != null && tokenIssuedAt != null) {
                    val user = userRepository.findUserByUsername(username)
                    if (user != null) {
                        val lastLogoutTime = user.lastLogoutTime

//                        application.log.debug("Validating token for user $username")
//                        application.log.debug("tokenIssuedAt: $tokenIssuedAt")
//                        application.log.debug("lastLogoutTime: $lastLogoutTime")

                        // Ensure the token was issued after the user's last logout time
                        if (lastLogoutTime == null || !tokenIssuedAt.isBefore(lastLogoutTime)) {
                            JWTPrincipal(credential.payload)
                        } else {
                            application.log.debug("Token is invalid, user has logged out")
                            null
                        }
                    } else {
                        application.log.debug("User not found")
                        null
                    }
                } else {
                    application.log.debug("Invalid token data")
                    null
                }
            }
        }
    }
}
