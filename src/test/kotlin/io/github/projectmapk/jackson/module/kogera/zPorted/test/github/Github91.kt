package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub91 {
    data class DataClass1(val name: String, val content: DataClass2)

    data class DataClass2
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    constructor(@JsonValue val content: String)

    private val jsonData = """
        {
            "name": "my name",
            "content": "some value"
        }
        """

    @Test
    fun testJsonParsing() {
        val dataClass1 = defaultMapper.readValue<DataClass1>(jsonData)
        assertEquals(DataClass1("my name", DataClass2("some value")), dataClass1)
        assertEquals("{\"name\":\"my name\",\"content\":\"some value\"}", defaultMapper.writeValueAsString(dataClass1))
    }
}
