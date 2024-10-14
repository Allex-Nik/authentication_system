package com.example.database

import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection


object DatabaseFactory {
    fun init(environment: ApplicationEnvironment) {
        val config = environment.config.config("ktor.database")
        val driverClassName = config.property("driver").getString()
        val jdbcURL = config.property("url").getString()
        val dbUser = config.property("user").getString()
        val dbPassword = config.property("password").getString()

        val flyway = Flyway.configure()
            .dataSource(jdbcURL, dbUser, dbPassword)
            .driver(driverClassName)
            .baselineOnMigrate(true)
            .load()

        flyway.migrate()

        Database.connect(
            url = jdbcURL,
            driver = driverClassName,
            user = dbUser,
            password = dbPassword
        )

        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    }
}
