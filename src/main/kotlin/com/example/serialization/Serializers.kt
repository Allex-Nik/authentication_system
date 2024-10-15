package com.example.serialization

import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.KSerializer

import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * A custom serializer for LocalDateTime using Kotlin Serialization.
 * It serializes LocalDateTime in UTC format.
 */
class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneOffset.UTC)

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    /**
     * Serializes a LocalDateTime object in ISO 8601 format.
     *
     * @param encoder The encoder used to encode the LocalDateTime.
     * @param value The LocalDateTime value to serialize.
     */
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val instant = value.toInstant(ZoneOffset.UTC)
        val formatted = formatter.format(instant)
        encoder.encodeString(formatted)
    }

    /**
     * Deserializes a string into a LocalDateTime object.
     *
     * @param decoder The decoder used to decode the string.
     * @return A LocalDateTime object parsed from the string.
     */
    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        val instant = Instant.parse(string)
        return instant.atZone(ZoneOffset.UTC).toLocalDateTime()
    }
}
