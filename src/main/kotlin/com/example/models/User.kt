package com.example.models

import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual


@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val firstName: String? = null,
    val lastName: String? = null,
    @Contextual val createdAt: LocalDateTime,
    @Contextual val updatedAt: LocalDateTime
)