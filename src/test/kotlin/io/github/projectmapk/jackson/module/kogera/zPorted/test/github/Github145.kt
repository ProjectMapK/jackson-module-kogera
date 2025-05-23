package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test

@Suppress("UNUSED_VARIABLE")
class TestGithub145 {
    @Test
    fun workingTestWithoutKotlinModule() {
        class Person1(
            @JsonProperty("preName") val preName: String,
            @JsonProperty("lastName") val lastName: String
        ) {
            constructor(preNameAndLastName: String) : this(
                preNameAndLastName.substringBefore(","),
                preNameAndLastName.substringAfter(",")
            )
        }

        val objectMapper = ObjectMapper()
        val personA =
            objectMapper.readValue("""{"preName":"TestPreName","lastName":"TestLastname"}""", Person1::class.java)
        val personB = objectMapper.readValue(""""TestPreName,TestLastname"""", Person1::class.java)
    }

    @Test
    fun testPerson2() {
        class Person2(
            @JsonProperty("preName") val preName: String,
            @JsonProperty("lastName") val lastName: String
        ) {
            @JsonCreator
            constructor(preNameAndLastName: String) : this(
                preNameAndLastName.substringBefore(","),
                preNameAndLastName.substringAfter(",")
            )
        }

        val person1String = defaultMapper.readValue<Person2>(""""TestPreName,TestLastname"""")
        val person1Json = defaultMapper.readValue<Person2>("""{"preName":"TestPreName","lastName":"TestLastname"}""")
    }

    @Test
    fun testPerson3() {
        class Person3(val preName: String, val lastName: String) {
            @JsonCreator
            constructor(preNameAndLastName: String) : this(
                preNameAndLastName.substringBefore(","),
                preNameAndLastName.substringAfter(",")
            )
        }

        val person2String = defaultMapper.readValue<Person3>(""""TestPreName,TestLastname"""")
        val person2Json = defaultMapper.readValue<Person3>("""{"preName":"TestPreName","lastName":"TestLastname"}""")
    }

    @Test
    fun testPerson4() {
        class Person4(preNameAndLastName: String) {
            val preName: String
            val lastName: String

            init {
                this.preName = preNameAndLastName.substringBefore(",")
                this.lastName = preNameAndLastName.substringAfter(",")
            }
        }

        val person4String = defaultMapper.readValue<Person4>(""""TestPreName,TestLastname"""")
        // person4 does not have parameter bound constructor, only string
    }

    @Test
    fun testPerson5() {
        class Person5 @JsonCreator constructor(
            @JsonProperty("preName") val preName: String,
            @JsonProperty("lastName") val lastName: String
        ) {
            @JsonCreator
            constructor(preNameAndLastName: String) :
                this(preNameAndLastName.substringBefore(","), preNameAndLastName.substringAfter(","))
        }

        val person5String = defaultMapper.readValue<Person5>(""""TestPreName,TestLastname"""")
        val person5Json = defaultMapper.readValue<Person5>("""{"preName":"TestPreName","lastName":"TestLastname"}""")
    }

    // Cannot have companion object in class declared within function
    class Person6 private constructor(val preName: String, val lastName: String) {
        private constructor(preNameAndLastName: String) : this(
            preNameAndLastName.substringBefore(","),
            preNameAndLastName.substringAfter(",")
        )

        companion object {
            @JsonCreator
            @JvmStatic
            fun createFromJson(preNameAndLastName: String): Person6 {
                return Person6(preNameAndLastName)
            }

            @JsonCreator
            @JvmStatic
            fun createFromData(preName: String, lastName: String): Person6 {
                return Person6(preName, lastName)
            }
        }
    }

    @Test
    fun testPerson6() {
        val person6String = defaultMapper.readValue<Person6>(""""TestPreName,TestLastname"""")
        val person6Json = defaultMapper.readValue<Person6>("""{"preName":"TestPreName","lastName":"TestLastname"}""")
    }

    // Cannot have companion object in class declared within function
    class Person7 constructor(val preName: String, val lastName: String) {
        private constructor(preNameAndLastName: String) : this(
            preNameAndLastName.substringBefore(","),
            preNameAndLastName.substringAfter(",")
        )

        companion object {
            @JsonCreator
            @JvmStatic
            fun createFromJson(preNameAndLastName: String): Person7 {
                return Person7(preNameAndLastName)
            }
        }
    }

    @Test
    fun testPerson7() {
        val person7String = defaultMapper.readValue<Person7>(""""TestPreName,TestLastname"""")
        val person7Json = defaultMapper.readValue<Person7>("""{"preName":"TestPreName","lastName":"TestLastname"}""")
    }
}
