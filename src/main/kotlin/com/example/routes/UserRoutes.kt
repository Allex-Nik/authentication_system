package com.example.routes

import com.example.repositories.UserRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UpdateUserRequest(
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

fun Route.userRoutes(userRepository: UserRepository) {
    authenticate("auth-jwt") {
        route("/user") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val user = userRepository.findUserByUsername(username)
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            put {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val user = userRepository.findUserByUsername(username)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@put
                }

                val request = call.receive<UpdateUserRequest>()
                val updatedUser = user.copy(
                    email = request.email ?: user.email,
                    firstName = request.firstName ?: user.firstName,
                    lastName = request.lastName ?: user.lastName,
                    updatedAt = LocalDateTime.now()
                )

                userRepository.updateUser(updatedUser)
                call.respond(HttpStatusCode.OK, "User information updated")
            }

            delete {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val user = userRepository.findUserByUsername(username)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@delete
                }

                userRepository.deleteUser(user.id)
                call.respond(HttpStatusCode.OK, "User account deleted")
            }
        }
    }
}
