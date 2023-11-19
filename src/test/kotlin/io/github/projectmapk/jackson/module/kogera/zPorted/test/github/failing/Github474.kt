package io.github.projectmapk.jackson.module.kogera.zPorted.test.github.failing

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.zPorted.test.expectFailure
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub474 {
    @Test
    fun jsonPropertyAnnotationRespectedOnParentClass() {
        open class Parent(@JsonProperty("parent-prop") val parent: String)
        class Child(@JsonProperty("child-prop") val child: String) : Parent(child)

        expectFailure<AssertionError>("GitHub #474 has been fixed!") {
            assertEquals(
                """{"child-prop":"foo","parent-prop":"foo"}""",
                jacksonObjectMapper().writeValueAsString(Child("foo"))
            )
        }
    }
}
