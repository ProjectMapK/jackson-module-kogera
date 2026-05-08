package io.github.projectmapk.jackson.module.kogera.zPorted.test.github.failing

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.zPorted.test.expectFailure
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub474 {
    open class Parent(@JsonProperty("parent-prop") val parent: String)
    class Child(@JsonProperty("child-prop") val child: String) : Parent(child)

    @Test
    fun jsonPropertyAnnotationRespectedOnParentClass() {
        val assertion = {
            assertEquals(
                """{"child-prop":"foo","parent-prop":"foo"}""",
                defaultMapper.writeValueAsString(Child("foo"))
            )
        }

        if (KotlinVersion(2, 4) <= KotlinVersion.CURRENT) {
            assertion()
        } else {
            expectFailure<AssertionError>("GitHub #474 has been fixed!") {
                assertion()
            }
        }
    }
}
