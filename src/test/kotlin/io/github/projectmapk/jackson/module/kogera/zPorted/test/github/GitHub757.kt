package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.databind.json.JsonMapper
import io.github.projectmapk.jackson.module.kogera.KotlinFeature
import io.github.projectmapk.jackson.module.kogera.KotlinModule
import io.github.projectmapk.jackson.module.kogera.convertValue
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class GitHub757 {
    @Test
    fun test() {
        val kotlinModule = KotlinModule.Builder()
            .enable(KotlinFeature.StrictNullChecks)
            .build()
        val mapper = JsonMapper.builder()
            .addModule(kotlinModule)
            .build()
        val convertValue = mapper.convertValue<String?>(null)
        assertNull(convertValue)
    }
}
