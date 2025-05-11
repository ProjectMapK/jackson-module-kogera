package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

data class ClassWithPrimitivesWithDefaults(val i: Int = 5, val x: Int)

class TestGithub26 {
    @Test
    fun testConstructorWithPrimitiveTypesDefaultedExplicitlyAndImplicitly() {
        val check1: ClassWithPrimitivesWithDefaults = defaultMapper
            .readValue("""{"i":3,"x":2}""")
        assertEquals(3, check1.i)
        assertEquals(2, check1.x)

        val check2: ClassWithPrimitivesWithDefaults = defaultMapper
            .readValue("""{}""")
        assertEquals(5, check2.i)
        assertEquals(0, check2.x)

        val check3: ClassWithPrimitivesWithDefaults = defaultMapper
            .readValue("""{"i": 2}""")
        assertEquals(2, check3.i)
        assertEquals(0, check3.x)
    }
}
