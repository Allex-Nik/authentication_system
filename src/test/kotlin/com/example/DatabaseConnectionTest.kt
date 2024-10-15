package com.example

import io.ktor.server.config.*
import kotlin.test.assertTrue
import org.junit.Test
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Verifies the connection to the PostgreSQL test database.
 * Attempts to connect to the database using the provided configuration and
 * executes a simple query to ensure the connection works.
 */
class DatabaseConnectionTest {

    /**
     * Checks if the connection to the test database is successful.
     * Connects to the database and runs a transaction that performs a query.
     */
    @Test
    fun `test database connection`() {
        // Define the database configuration
        val config = MapApplicationConfig().apply {
            put("ktor.database.driver", "org.postgresql.Driver")
            put("ktor.database.url", "jdbc:postgresql://localhost:5432/auth_system_test")
            put("ktor.database.user", "test_user")
            put("ktor.database.password", "test_password")
        }

        try {
            // Retrieve the database configuration values
            val driverClassName = config.property("ktor.database.driver").getString()
            val jdbcURL = config.property("ktor.database.url").getString()
            val databaseUser = config.property("ktor.database.user").getString()
            val databasePassword = config.property("ktor.database.password").getString()

            // Connect to the database
            Database.connect(
                url = jdbcURL,
                driver = driverClassName,
                user = databaseUser,
                password = databasePassword
            )

            // Execute a simple query to verify the connection
            transaction {
                exec("SELECT 1;") { rs ->
                    if (rs.next()) {
                        val result = rs.getInt(1)
                        assertTrue(result == 1)
                    } else {
                        throw AssertionError("No result returned from SELECT 1;")
                    }
                }
            }
        } catch (e: Exception) {
            // If the connection fails, print the error and fail the test
            e.printStackTrace()
            throw AssertionError("Failed to connect to the test database: ${e.message}")
        }
    }
}
