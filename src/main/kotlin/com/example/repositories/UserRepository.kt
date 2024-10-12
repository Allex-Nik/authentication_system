package com.example.repositories

import com.example.database.Users
import com.example.models.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class UserRepository {

    fun addUser(user : User): User {
        val id = transaction {
            Users.insert {
                it[username] = user.username
                it[email] = user.email
                it[passwordHash] = user.passwordHash
                it[firstName] = user.firstName
                it[last_name] = user.lastName
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            } get Users.id
        }
        return user.copy(id = id)
    }

    fun findUserByUsername(username: String): User? {
        return transaction {
            Users.selectAll()
                .where { Users.username eq username }
                .mapNotNull { toUser(it) }
                .singleOrNull()
        }
    }

    fun findUserByEmail(email: String): User? {
        return transaction {
            Users.selectAll()
                .where { Users.email eq email }
                .mapNotNull { toUser(it) }
                .singleOrNull()
        }
    }

    fun updateUser(user: User) {
        transaction {
            Users.update({ Users.id eq user.id }) {
                it[username] = user.username
                it[email] = user.email
                it[firstName] = user.firstName
                it[last_name] = user.lastName
                it[updatedAt] = user.updatedAt
            }
        }
    }

    fun deleteUser(userId: Int) {
        transaction {
            Users.deleteWhere { Users.id eq userId }
        }
    }

    private fun toUser(row: ResultRow) : User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            firstName = row[Users.firstName],
            lastName = row[Users.last_name],
            createdAt = row[Users.createdAt],
            updatedAt = row[Users.updatedAt]
        )
    }
}
