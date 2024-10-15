package com.example.routes

import io.ktor.server.testing.*
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureSecurity
import com.example.database.DatabaseFactory
import com.example.database.Users
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.repositories.UserRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteAll


/**
 * Tests the authentication routes (register and login) of the application.
 */
class AuthRoutesTest {

    /**
     * Setup method to initialize the test environment before each test.
     * Creates a test database configuration and clears the 'users' table.
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

        DatabaseFactory.init(environment = createTestEnvironment {
            config = testConfig
        })
        transaction {
            Users.deleteAll()
        }
    }

    /**
     * Test the user registration route.
     * Sends a POST request to register a new user and verifies the response.
     */
    @Test
    fun `test user registration`() = testApplication {
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

        // Set up the application with serialization, security, and routing
        application {
            configureSerialization()
            configureSecurity()
            configureRouting()
        }

        // Create a client to send the registration request
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        // Send a POST request to register a new user
        val response = client.post("/auth/register") {
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

        // Verify that the response status is 201 Created
        assertEquals(HttpStatusCode.Created, response.status)

        // Verify the success message
        assertEquals("The user is successfully registered", response.bodyAsText())

        // Check that the user is stored in the database
        val userRepository = UserRepository()
        val user = userRepository.findUserByUsername("testuser")
        assertNotNull(user)
        assertEquals("test@example.com", user!!.email)
    }

    /**
     * Test the user login route.
     * First, a user is registered, then the login request is sent to authenticate the user.
     */
    @Test
    fun `test user login`() = testApplication {
        // Configure the test environment with database and JWT settings
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

        // Set up the application with serialization, security, and routing
        application {
            configureSerialization()
            configureSecurity()
            configureRouting()
        }

        // Create a client to send the registration and login requests
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

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

        // Verify that the registration was successful
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        // Send a POST request to log in the registered user
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

        // Verify that the login was successful and a token was returned
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val token = loginResponse.bodyAsText()
        assertNotNull(token, "Token should not be null")

        // Ensure the response contains a JWT token
        assertTrue(token.contains("token"), "Response should contain JWT token")
    }
}
