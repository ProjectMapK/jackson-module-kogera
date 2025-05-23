package io.github.projectmapk.jackson.module.kogera.zPorted.test

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import kotlin.properties.Delegates

private data class DataClassPerson(val name: String, val age: Int)

private class TestM11Changes {
    val mapper = jacksonObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, false)

    private class Class_With_One_Constructor(val name: String, val age: Int)

    @Test
    fun testNormalClass_One_Constructor() {
        val expectedJson = """{"name":"John Smith","age":30}"""
        val expectedPerson = Class_With_One_Constructor("John Smith", 30)

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<Class_With_One_Constructor>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson.name, newPerson.name)
        assertEquals(expectedPerson.age, newPerson.age)
    }

    private data class Class_Data_Annotation_With_One_Constructor(val name: String, val age: Int)

    @Test
    fun testDataClass_One_Constructor() {
        val expectedJson = """{"name":"John Smith","age":30}"""
        val expectedPerson = Class_Data_Annotation_With_One_Constructor("John Smith", 30)

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<Class_Data_Annotation_With_One_Constructor>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson, newPerson)
    }

    private data class Class_With_Init_Constructor(val name: String, val age: Int) {
        val otherThing: String
        init {
            otherThing = "franky"
        }
    }

    @Test
    fun testDataClass_Init_Constructor() {
        val expectedJson = """{"name":"John Smith","age":30,"otherThing":"franky"}"""
        val expectedPerson = Class_With_Init_Constructor("John Smith", 30)

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<Class_With_Init_Constructor>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson, newPerson)
    }

    private data class Class_With_Init_Constructor_And_Ignored_Property(val name: String, val age: Int) {
        @JsonIgnore val otherThing: String
        init {
            otherThing = "franky"
        }
    }

    @Test
    fun testDataClass_Init_Constructor_And_Ignored_Property() {
        val expectedJson = """{"name":"John Smith","age":30}"""
        val expectedPerson = Class_With_Init_Constructor_And_Ignored_Property("John Smith", 30)

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<Class_With_Init_Constructor_And_Ignored_Property>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson, newPerson)
    }

    private class Class_With_No_Field_Parameters_But_Field_Declared_Inside_initialized_from_parameter(val name: String, age: Int) {
        val age: Int = age
    }

    @Test
    fun testDataClass_With_No_Field_Parameters_But_Field_Declared_Inside_initialized_from_parameter() {
        val expectedJson = """{"name":"John Smith","age":30}"""
        val expectedPerson = Class_With_No_Field_Parameters_But_Field_Declared_Inside_initialized_from_parameter("John Smith", 30)

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<Class_With_No_Field_Parameters_But_Field_Declared_Inside_initialized_from_parameter>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson.name, newPerson.name)
        assertEquals(expectedPerson.age, newPerson.age)
    }

    private class ClassFor_testDataClass_WithOnlySecondaryConstructor {
        val name: String
        val age: Int
        constructor(name: String, age: Int) {
            this.name = name
            this.age = age
        }
    }

    @Test fun testDataClass_WithOnlySecondaryConstructor() {
        val expectedJson = """{"name":"John Smith","age":30}"""
        val expectedPerson = ClassFor_testDataClass_WithOnlySecondaryConstructor("John Smith", 30)

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<ClassFor_testDataClass_WithOnlySecondaryConstructor>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson.name, newPerson.name)
        assertEquals(expectedPerson.age, newPerson.age)
    }

    private class Class_WithPrimaryAndSecondaryConstructor(val name: String, val age: Int) {
        constructor(nameAndAge: String) : this(nameAndAge.substringBefore(':'), nameAndAge.substringAfter(':').toInt()) {
        }
    }

    @Test fun testDataClass_WithPrimaryAndSecondaryConstructor() {
        val expectedJson = """{"name":"John Smith","age":30}"""
        val expectedPerson = Class_WithPrimaryAndSecondaryConstructor("John Smith", 30)

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<Class_WithPrimaryAndSecondaryConstructor>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson.name, newPerson.name)
        assertEquals(expectedPerson.age, newPerson.age)
    }

    private class Class_WithPrimaryAndSecondaryConstructorAnnotated(name: String) {
        val name: String = name
        var age: Int = 0

        @JsonCreator constructor(name: String, age: Int) : this(name) {
            this.age = age
        }
    }

    @Test fun testDataClass_WithPrimaryAndSecondaryConstructorBothCouldBeUsedToDeserialize() {
        val expectedJson = """{"name":"John Smith","age":30}"""
        val expectedPerson = Class_WithPrimaryAndSecondaryConstructorAnnotated("John Smith", 30)

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<Class_WithPrimaryAndSecondaryConstructorAnnotated>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson.name, newPerson.name)
        assertEquals(expectedPerson.age, newPerson.age)

        val jsonWithNoAge = """{"name":"John Smith"}"""
        val personNoAge = mapper.readValue<Class_WithPrimaryAndSecondaryConstructorAnnotated>(jsonWithNoAge)

        assertEquals(0, personNoAge.age)
        assertEquals("John Smith", personNoAge.name)
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    class Class_WithPartialFieldsInConstructor(val name: String, @JsonProperty("age") val years: Int) {
        @JsonProperty("address")
        var primaryAddress: String = ""
        var phone: String by Delegates.notNull()
    }

    @Test fun testClass_WithPartialFieldsInConstructor() {
        val expectedJson = """{"name":"John Smith","age":30,"phone":"1234567890"}"""
        val expectedPerson = Class_WithPartialFieldsInConstructor("John Smith", 30)
        expectedPerson.phone = "1234567890"

        val actualJson = mapper.writeValueAsString(expectedPerson)
        val newPerson = mapper.readValue<Class_WithPartialFieldsInConstructor>(actualJson)

        assertEquals(expectedJson, actualJson)
        assertEquals(expectedPerson.name, newPerson.name)
        assertEquals(expectedPerson.years, newPerson.years)
        assertEquals(expectedPerson.phone, newPerson.phone)
        assertEquals(expectedPerson.primaryAddress, newPerson.primaryAddress)

        val jsonWithNullPhone = """{"name":"John Smith","age":30}"""
        val person = mapper.readValue<Class_WithPartialFieldsInConstructor>(jsonWithNullPhone)

        try {
            person.phone
            fail("While person can be deserialized without a phone, phone must be set before attempting to access it")
        } catch (_: IllegalStateException) { // expected
        }
    }

    @Test fun testNullableType() {
        val newPerson = mapper.readValue<Class_WithPartialFieldsInConstructor?>("null")
        assertNull(newPerson)
    }
}
