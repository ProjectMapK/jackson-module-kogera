package io.github.projectmapk.jackson.module.kogera._ported.test.github.failing

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.github.projectmapk.jackson.module.kogera.registerKotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithub396 {
    /**
     * Succeeds in Jackson 2.11.x, but fails in Jackson 2.12.0.
     * But succeeds again in 2.15.0.
     *
     * See https://github.com/FasterXML/jackson-module-kotlin/issues/396
     */
    @Test
    fun testMissingConstructor() {
        val mapper = XmlMapper().registerKotlinModule()

        val xml = "<product><stuff></stuff></product>"
        val product: Product = mapper.readValue(xml, Product::class.java)

        assertEquals(Product(Stuff(null)), product)
    }

    private data class Stuff(val str: String?)
    private data class Product(val stuff: Stuff?)
}
