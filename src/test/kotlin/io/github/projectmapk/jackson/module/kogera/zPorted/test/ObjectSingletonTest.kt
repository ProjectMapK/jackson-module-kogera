package io.github.projectmapk.jackson.module.kogera.zPorted.test

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.SingletonSupport
import io.github.projectmapk.jackson.module.kogera.kotlinModule
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// [module-kotlin#225]: keep Kotlin singletons as singletons
class TestObjectSingleton {
    val mapper: ObjectMapper = ObjectMapper()
        .registerModule(
            kotlinModule {
                enable(
                    SingletonSupport
                )
            }
        )

    object Singleton {
        var content = 1 // mutable state
    }

    @Test
    fun deserializationPreservesSingletonProperty() {
        val js = mapper.writeValueAsString(Singleton)
        val newSingleton = mapper.readValue<Singleton>(js)

        assertEquals(Singleton, newSingleton)
    }

    @Test
    fun deserializationResetsSingletonObjectState() {
        // persist current singleton state
        val js = mapper.writeValueAsString(Singleton)
        val initial = Singleton.content

        // mutate the in-memory singleton state
        val after = initial + 1
        Singleton.content = after
        assertEquals(Singleton.content, after)

        // read back persisted state resets singleton state
        val newSingleton = mapper.readValue<Singleton>(js)
        assertEquals(initial, Singleton.content)
        assertEquals(initial, newSingleton.content)
    }

    @Test
    fun deserializedObjectsBehaveLikeSingletons() {
        val js = mapper.writeValueAsString(Singleton)
        val newSingleton = mapper.readValue<Singleton>(js)
        assertEquals(Singleton.content, newSingleton.content)

        Singleton.content += 1

        assertEquals(Singleton.content, newSingleton.content)
    }
}
