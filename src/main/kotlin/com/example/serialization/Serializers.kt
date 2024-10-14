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


class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneOffset.UTC)

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val instant = value.toInstant(ZoneOffset.UTC)
        val formatted = formatter.format(instant)
        encoder.encodeString(formatted)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        val instant = Instant.parse(string)
        return instant.atZone(ZoneOffset.UTC).toLocalDateTime()
    }
}
