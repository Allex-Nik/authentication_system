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


/**
 * Data class representing a request to update user information.
 * Fields are nullable, allowing partial updates to user data (email, first name, last name).
 */
@Serializable
data class UpdateUserRequest(
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

/**
 * Route handler for user-related operations.
 * Includes routes for getting, updating, and deleting user information.
 * Each route requires JWT authentication.
 *
 * @param userRepository The repository used to manage user operations.
 */
fun Route.userRoutes(userRepository: UserRepository) {
    authenticate("auth-jwt") {
        route("/user") {

            /**
             * GET /user
             * Retrieves information about the authenticated user.
             * If the user is found in the database, their details are returned.
             * Otherwise, returns a 404 Not Found response.
             */
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

            /**
             * PUT /user
             * Updates the authenticated user's information.
             * The request must include at least one of the updatable fields (email, first name, last name).
             * If the user is not found, returns a 404 Not Found response.
             */
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

            /**
             * DELETE /user
             * Deletes the authenticated user's account from the database.
             * If the user is not found, returns a 404 Not Found response.
             */
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
