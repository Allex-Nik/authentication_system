package com.example.models

import com.example.serialization.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable


/**
 * The "User" data class represents a user in the system.
 *
 * @property id The unique ID of the user.
 * @property username The user's username, which must be unique.
 * @property email The user's email address, which must be unique.
 * @property passwordHash The hash of the user's password for secure storage.
 * @property firstName The first name of the user (optional).
 * @property lastName The last name of the user (optional).
 * @property createdAt The timestamp when the user was created.
 * @property updatedAt The timestamp when the user was last updated.
 * @property lastLogoutTime The timestamp of the user's last logout (optional).
 */
@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val firstName: String? = null,
    val lastName: String? = null,

    // Custom serializer is used for LocalDateTime fields
    @Serializable(with = LocalDateTimeSerializer::class) val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class) val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class) val lastLogoutTime: LocalDateTime? = null
)
