package com.example.models

import com.example.serialization.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val firstName: String? = null,
    val lastName: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class) val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class) val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class) val lastLogoutTime: LocalDateTime? = null
)
