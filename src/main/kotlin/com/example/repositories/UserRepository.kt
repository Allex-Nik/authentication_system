package com.example.repositories

import com.example.database.Users
import com.example.models.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * UserRepository provides methods for interacting with the Users table in the database.
 * It allows to add, retrieve, update, delete users, and manage user-specific data
 * like their last logout time.
 */
class UserRepository {

    /**
     * Adds a new user to the database.
     *
     * @param user The User object to be added.
     * @return The added user with the newly generated ID.
     */
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
                it[lastLogoutTime] = user.lastLogoutTime
            } get Users.id
        }
        return user.copy(id = id)
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The user if found, null if not found.
     */
    fun findUserByUsername(username: String): User? {
        return transaction {
            Users.selectAll()
                .where { Users.username eq username }
                .mapNotNull { toUser(it) }
                .singleOrNull()
        }
    }

    /**
     * Finds a user by their email.
     *
     * @param email The email to search for.
     * @return The user if found, null if not found.
     */
    fun findUserByEmail(email: String): User? {
        return transaction {
            Users.selectAll()
                .where { Users.email eq email }
                .mapNotNull { toUser(it) }
                .singleOrNull()
        }
    }

    /**
     * Updates the user's information in the database.
     *
     * @param user The updated User object.
     */
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

    /**
     * Updates the last logout time for a user.
     *
     * @param userId The ID of the user.
     * @param lastLogoutTime The time of the last logout.
     */
    fun updateLastLogoutTime(userId: Int, lastLogoutTime: LocalDateTime) {
        transaction {
            Users.update({ Users.id eq userId}) {
                it[Users.lastLogoutTime] = lastLogoutTime
            }
        }
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param userId The ID of the user to delete.
     */
    fun deleteUser(userId: Int) {
        transaction {
            Users.deleteWhere { Users.id eq userId }
        }
    }

    /**
     * Converts a ResultRow from the database into a User object.
     *
     * @param row The database result row.
     * @return The User object mapped from the row.
     */
    private fun toUser(row: ResultRow) : User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            firstName = row[Users.firstName],
            lastName = row[Users.last_name],
            createdAt = row[Users.createdAt],
            updatedAt = row[Users.updatedAt],
            lastLogoutTime = row[Users.lastLogoutTime]
        )
    }
}
