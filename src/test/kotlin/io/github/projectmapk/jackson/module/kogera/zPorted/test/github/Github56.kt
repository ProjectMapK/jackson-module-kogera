package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private class TestGithub56 {

    private data class TestGalleryWidget_BAD(
        val widgetReferenceId: String,
        @JsonUnwrapped var gallery: TestGallery
    )

    private data class TestGalleryWidget_GOOD(val widgetReferenceId: String) {
        @JsonUnwrapped lateinit var gallery: TestGallery
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private data class TestGallery(
        val id: String? = null,
        val headline: String? = null,
        val intro: String? = null,
        val role: String? = null,
        val images: List<TestImage>? = null
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private data class TestImage(
        val id: String? = null,
        val escenicId: String? = null,
        val caption: String? = null,
        val copyright: String? = null,
        val crops: Map<String, String>? = null
    )

    private val gallery = TestGallery(
        id = "id",
        headline = "headline",
        intro = "intro",
        role = "role",
        images = listOf(
            TestImage(id = "testImage1"),
            TestImage(id = "testImage2")
        )
    )
    val validJson = """
         {"widgetReferenceId":"widgetReferenceId","id":"id","headline":"headline","intro":"intro","role":"role","images":[{"id":"testImage1"},{"id":"testImage2"}]}
    """.trim()

    @Test
    fun serializes() {
        val result = defaultMapper.writeValueAsString(TestGalleryWidget_BAD("widgetReferenceId", gallery))
        assertEquals(validJson, result)
    }

    @Test
    fun deserializesSuccessful() {
        val obj = defaultMapper.readValue<TestGalleryWidget_BAD>(validJson)
        assertEquals("widgetReferenceId", obj.widgetReferenceId)
        assertEquals(gallery, obj.gallery)
    }

    @Test
    fun deserializesCorrectly() {
        defaultMapper.readValue<TestGalleryWidget_GOOD>(validJson)
    }
}
