package io.github.projectmapk.jackson.module.kogera._integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class InitModuleTest {
    @Test
    fun findAndRegisterModulesTest() {
        assertDoesNotThrow { ObjectMapper().findAndRegisterModules() }
    }
}
