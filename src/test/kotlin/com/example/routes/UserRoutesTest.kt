package com.example.routes

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureSecurity
import com.example.database.DatabaseFactory
import com.example.database.Users

import io.ktor.server.testing.*
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.*
import kotlin.test.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * This class tests the user-related routes: getting user info,
 * updating user info, and deleting the user account.
 */
class UserRoutesTest {

    // JWT token used for authentication in the tests
    private lateinit var token: String

    /**
     * Setup method to initialize the test environment before each test.
     * Creates a test database configuration and clears the "users" table.
     */
    @BeforeTest
    fun setup() {
        val testConfig = MapApplicationConfig(
            "ktor.database.driver" to "org.postgresql.Driver",
            "ktor.database.url" to "jdbc:postgresql://localhost:5432/auth_system_test",
            "ktor.database.user" to "test_user",
            "ktor.database.password" to "test_password",
            "secret" to "rFv64RUkSIzK4HAYJqeApVVVYL/RevxKfSaE9+tby4c=",
            "issuer" to "com.example",
            "audience" to "com.example.audience",
            "realm" to "Access to 'com.example'"
        )

        // Initialize the test database
        DatabaseFactory.init(environment = createTestEnvironment {
            config = testConfig
        })

        // Clear the users table before each test
        transaction {
            Users.deleteAll()
        }
    }

    /**
     * Method to register a user and log in to get a JWT token.
     * The token is stored in the "token" variable for use in other tests.
     *
     * @param client The HTTP client used to make the registration and login requests.
     */
    private suspend fun registerAndLogin(client: HttpClient) {

        // Register a user
        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "password": "password123",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        // Log in to get the JWT token
        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)

        // Extract the token from the response
        token = loginResponse.bodyAsText().substringAfter("token\":\"").substringBefore("\"")
    }

    /**
     * Test to get the user information.
     * Registers and logs in a user, then sends a GET request to fetch the user info.
     */
    @Test
    fun `test get user info`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ktor.database.driver" to "org.postgresql.Driver",
                "ktor.database.url" to "jdbc:postgresql://localhost:5432/auth_system_test",
                "ktor.database.user" to "test_user",
                "ktor.database.password" to "test_password",
                "jwt.secret" to "rFv64RUkSIzK4HAYJqeApVVVYL/RevxKfSaE9+tby4c=",
                "jwt.issuer" to "com.example",
                "jwt.audience" to "com.example.audience",
                "jwt.realm" to "Access to 'com.example'"
            )
        }

        application {
            configureSerialization()
            configureSecurity()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        // Register and log in to get a token
        registerAndLogin(client)

        // Get user info using the JWT token
        val userInfoResponse = client.get("/user") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        // Verify that the response is 200 OK and contains the username
        assertEquals(HttpStatusCode.OK, userInfoResponse.status)
        val userInfo = userInfoResponse.bodyAsText()
        assertTrue(userInfo.contains("testuser"), "User info should contain username")
    }

    /**
     * Test to update user information.
     * Registers and logs in a user, then sends a PUT request to update the user's email.
     */
    @Test
    fun `test update user info`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ktor.database.driver" to "org.postgresql.Driver",
                "ktor.database.url" to "jdbc:postgresql://localhost:5432/auth_system_test",
                "ktor.database.user" to "test_user",
                "ktor.database.password" to "test_password",
                "jwt.secret" to "rFv64RUkSIzK4HAYJqeApVVVYL/RevxKfSaE9+tby4c=",
                "jwt.issuer" to "com.example",
                "jwt.audience" to "com.example.audience",
                "jwt.realm" to "Access to 'com.example'"
            )
        }

        application {
            configureSerialization()
            configureSecurity()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        // Register and log in to get a token
        registerAndLogin(client)

        // Send a PUT request to update the user's email
        val updateResponse = client.put("/user") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "email": "newemail@example.com"
                }
                """.trimIndent()
            )
        }

        // Verify that the response is 200 OK and the update message is correct
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertEquals("User information updated", updateResponse.bodyAsText())
    }

    /**
     * Test to delete the user account.
     * Registers and logs in a user, then sends a DELETE request to delete the user's account.
     */
    @Test
    fun `test delete user account`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ktor.database.driver" to "org.postgresql.Driver",
                "ktor.database.url" to "jdbc:postgresql://localhost:5432/auth_system_test",
                "ktor.database.user" to "test_user",
                "ktor.database.password" to "test_password",
                "jwt.secret" to "rFv64RUkSIzK4HAYJqeApVVVYL/RevxKfSaE9+tby4c=",
                "jwt.issuer" to "com.example",
                "jwt.audience" to "com.example.audience",
                "jwt.realm" to "Access to 'com.example'"
            )
        }

        application {
            configureSerialization()
            configureSecurity()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        // Register and log in to get a token
        registerAndLogin(client)

        // Send a DELETE request to delete the user's account
        val deleteResponse = client.delete("/user") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        // Verify that the response is 200 OK and the account deletion message is correct
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
        assertEquals("User account deleted", deleteResponse.bodyAsText())
    }
}
