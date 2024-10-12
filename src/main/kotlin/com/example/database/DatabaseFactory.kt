package com.example.database

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(environment: ApplicationEnvironment) {
        val config = environment.config.config("ktor.database")
        val driverClassName = config.property("driver").getString()
        val jdbcURL = config.property("url").getString()
        val dbUser = config.property("user").getString()
        val dbPassword = config.property("password").getString()

        Database.connect(
            url = jdbcURL,
            driver = driverClassName,
            user = dbUser,
            password = dbPassword
        )

        transaction {
            SchemaUtils.create(Users)
        }
    }
}