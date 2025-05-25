package io.github.projectmapk.jackson.module.kogera.zPorted.test

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.zPorted.test.SealedClassTest.SuperClass.B
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SealedClassTest {
    /**
     * Json of a Serialized B-Object.
     */
    private val jsonB = """{"@type":"SealedClassTest${"$"}SuperClass${"$"}B"}"""

    /**
     * Tests that the @JsonSubTypes-Annotation is not necessary when working with Sealed-Classes.
     */
    @Test
    fun sealedClassWithoutSubTypes() {
        val result = defaultMapper.readValue(jsonB, SuperClass::class.java)
        assertTrue { result is B }
    }

    @Test
    fun sealedClassWithoutSubTypesList() {
        val result = defaultMapper.readValue(
            """[$jsonB, $jsonB]""",
            object : TypeReference<List<SuperClass>>() {}
        )
        assertEquals(2, result.size)
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    sealed class SuperClass {
        class A : SuperClass()
        class B : SuperClass()
    }

    /**
     * Tests that we can use JsonTypeInfo.Id.DEDUCTION to deduct sealed types without the need for explicit fields.
     */
    @Test
    fun sealedClassWithoutTypeDiscriminator() {
        val serializedSingle = """{"request":"single"}"""
        val single = defaultMapper.readValue(serializedSingle, SealedRequest::class.java)
        assertEquals("single", (single as? SealedRequest.SingleRequest)?.request)
    }

    /**
     * Attempting to deserialize a collection by deduction
     */
    @Test
    fun sealedClassWithoutTypeDiscriminatorList() {
        val serializedBatch = """[{"request":"first"},{"request":"second"}]"""
        expectFailure<MismatchedInputException>("Deserializing a list using deduction is fixed!") {
            val batch = defaultMapper.readValue(serializedBatch, SealedRequest::class.java) as SealedRequest.BatchRequest
            assertEquals(2, batch.requests.size)
            assertEquals("first", batch.requests[0].request)
            assertEquals("second", batch.requests[1].request)
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    sealed class SealedRequest {
        data class SingleRequest(val request: String) : SealedRequest()
        data class BatchRequest @JsonCreator constructor(@get:JsonValue val requests: List<SingleRequest>) :
            SealedRequest()
    }
}
