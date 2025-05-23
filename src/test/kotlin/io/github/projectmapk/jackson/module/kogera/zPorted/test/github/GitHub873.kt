package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test

class GitHub873 {
    @Test
    fun `should serialize value class`() {

        val person = Person(
            mapOf(
                "id" to "123",
                "updated" to "2023-11-22 12:11:23",
                "login" to "2024-01-15",
            ),
        )

        val serialized = defaultMapper.writeValueAsString(
            TimestampedPerson(
                123L,
                Person(person.properties),
            )
        )

        val deserialized = defaultMapper.readValue<TimestampedPerson>(serialized)

        assert(
            deserialized == TimestampedPerson(
                123L,
                Person(person.properties),
            )
        )
    }

    @JvmInline
    value class Person(
        val properties: Map<String, Any>,
    )

    data class TimestampedPerson(
        val timestamp: Long,
        val person: Person,
    )
}
