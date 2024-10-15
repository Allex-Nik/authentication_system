package com.example.database

import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

/**
 * Singleton object responsible for initializing the database connection and handling migrations.
 */
object DatabaseFactory {
    /**
     * Initializes the database connection and applies migrations using Flyway.
     *
     * @param environment The Ktor application environment which contains the configuration details.
     */
    fun init(environment: ApplicationEnvironment) {
        // Retrieve database configuration from application environment
        val config = environment.config.config("ktor.database")
        val driverClassName = config.property("driver").getString()
        val jdbcURL = config.property("url").getString()
        val dbUser = config.property("user").getString()
        val dbPassword = config.property("password").getString()

        // Configure Flyway for database migrations
        val flyway = Flyway.configure()
            .dataSource(jdbcURL, dbUser, dbPassword)
            .driver(driverClassName)
            .baselineOnMigrate(true)
            .load()

        // Apply migrations
        flyway.migrate()

        // Connect to the database using Exposed ORM
        Database.connect(
            url = jdbcURL,
            driver = driverClassName,
            user = dbUser,
            password = dbPassword
        )

        // Set the default transaction isolation level for database operations
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    }
}
