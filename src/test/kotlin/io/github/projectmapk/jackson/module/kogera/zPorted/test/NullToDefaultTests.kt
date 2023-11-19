package io.github.projectmapk.jackson.module.kogera.zPorted.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.github.projectmapk.jackson.module.kogera.KotlinFeature.NullIsSameAsDefault
import io.github.projectmapk.jackson.module.kogera.kotlinModule
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestNullToDefault {

    private fun createMapper(allowDefaultingByNull: Boolean) = ObjectMapper()
        .registerModule(
            kotlinModule {
                configure(
                    NullIsSameAsDefault,
                    allowDefaultingByNull
                )
            }
        )

    private data class TestClass(
        val sku: Int = -1,
        val text: String,
        val name: String = "",
        val images: String?,
        val language: String = "uk",
        val attribute: Int = 0,
        val order: Int = -1
    )

    @Test
    fun shouldUseNullAsDefault() {
        val item = createMapper(true).readValue<TestClass>(
            """{
					"sku": "974",
					"text": "plain",
					"name": null,
					"images": null,
					"attribute": "19"     
				}"""
        )

        assertTrue(item.sku == 974)
        assertTrue(item.text == "plain")
        @Suppress("SENSELESS_COMPARISON")
        assertTrue(item.name != null)
        assertTrue(item.images == null)
        assertTrue(item.language == "uk")
        assertTrue(item.attribute == 19)
        assertTrue(item.order == -1)
    }

    @Test
    fun shouldNotUseNullAsDefault() {
        assertThrows<MismatchedInputException> {
            createMapper(false).readValue<TestClass>(
                """{
					"sku": "974",
					"text": "plain",
					"name": null,
					"images": null,
					"attribute": "19"     
				}"""
            )
        }
    }

    // @Test(expected = MissingKotlinParameterException::class)
    @Test
    fun errorIfNotDefault() {
        assertThrows<MismatchedInputException> {
            createMapper(true).readValue<TestClass>(
                """{
						"sku": "974",
						"text": null,
						"attribute": "19",
						"name": null     
 				}"""
            )
        }
    }
}
