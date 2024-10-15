package com.example.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json


/**
 * Configures JSON serialization and content negotiation for the application.
 * Sets up the "ContentNegotiation" plugin with the Kotlinx JSON serializer,
 * allowing to automatically handle the serialization and deserialization.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        // Configure Kotlinx JSON serialization with specific settings
        json(Json {
            ignoreUnknownKeys = true // Ignore unknown keys in JSON to avoid exceptions
            isLenient = true // Allow more lenient parsing of JSON
        })
    }
}
