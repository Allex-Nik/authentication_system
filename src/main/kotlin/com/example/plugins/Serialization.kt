package com.example.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.datetime.Instant


fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            serializersModule = kotlinx.serialization.modules.SerializersModule {

            }
        })
    }
//    install(ContentNegotiation) {
//        json(Json {
//            serializersModule = SerializersModule {
//                contextual(Instant::class, InstantIso8601Serializer)
//            }
//        })
//    }
    routing {
        get("/json/kotlinx-serialization") {
                call.respond(mapOf("hello" to "world"))
            }
    }
}

//object InstantIso8601Serializer : KSerializer<Instant> {
//    override val descriptor: SerialDescriptor =
//        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: Instant) {
//        encoder.encodeString(value.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): Instant {
//        return Instant.parse(decoder.decodeString())
//    }
//}
