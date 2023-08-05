package io.github.projectmapk.jackson.module.kogera._integration.ser

import io.github.projectmapk.jackson.module.kogera.ReflectionCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private class InheritanceTest {
    abstract class A { val a: Int get() = 0 }

    interface I { val i: Int get() = -1 }

    interface J : I { val j: Int get() = -2 }

    private class X : A(), J

    val cache = ReflectionCache(5)

    @Test
    fun test() {
        val c = cache.getJmClass(X::class.java)!!
        val props = c.properties.associateBy { it.name }

        assertEquals(3, props.size)
        assertTrue(props.containsKey("a"))
        assertTrue(props.containsKey("i"))
        assertTrue(props.containsKey("j"))
    }
}
