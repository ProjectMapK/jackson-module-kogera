package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub15 {
    @Test
    fun testEnumConstructorWithParm() {
        val one = defaultMapper.readValue("\"ONE\"", TestEnum::class.java)
        assertEquals(TestEnum.ONE, one)
        val two = defaultMapper.readValue("\"TWO\"", TestEnum::class.java)
        assertEquals(TestEnum.TWO, two)
    }

    @Test fun testNormEnumWithoutParam() {
        val one = defaultMapper.readValue("\"ONE\"", TestOther::class.java)
        assertEquals(TestOther.ONE, one)
        val two = defaultMapper.readValue("\"TWO\"", TestOther::class.java)
        assertEquals(TestOther.TWO, two)
    }

    @Test fun testClassWithEnumsNeedingConstruction() {
        val obj: UsingEnum = defaultMapper.readValue("""{"x":"ONE","y":"TWO"}""")
        assertEquals(TestEnum.ONE, obj.x)
        assertEquals(TestOther.TWO, obj.y)
    }
}

private class UsingEnum(val x: TestEnum, val y: TestOther)

private enum class TestEnum(val i: Int) {
    ONE(1),
    TWO(2)
}

private enum class TestOther {
    ONE, TWO
}
