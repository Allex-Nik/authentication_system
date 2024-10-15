package com.example.database

import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.Table

/**
 * The "Users" object represents the "users" table in the database.
 */
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val firstName = varchar("first_name", 50).nullable()
    val last_name = varchar("last_name", 50).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    val lastLogoutTime = datetime("last_logout_time").nullable()

    // Defines the primary key for the table
    override val primaryKey = PrimaryKey(id)
}
