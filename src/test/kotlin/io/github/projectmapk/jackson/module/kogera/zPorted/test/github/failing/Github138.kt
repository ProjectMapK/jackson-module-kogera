package io.github.projectmapk.jackson.module.kogera.zPorted.test.github.failing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import io.github.projectmapk.jackson.module.kogera.readValue
import io.github.projectmapk.jackson.module.kogera.registerKotlinModule
import io.github.projectmapk.jackson.module.kogera.zPorted.test.expectFailure
import org.junit.jupiter.api.Test

class TestGithub138 {
    @JacksonXmlRootElement(localName = "sms")
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Sms(
        @JacksonXmlProperty(localName = "Phone", isAttribute = true)
        val phone: String?,

        @JacksonXmlText
        val text: String? = ""
    )

    @Test
    fun testDeserProblem() {
        val xml = """<sms Phone="435242423412" Id="43234324">Lorem ipsum</sms>"""
        val xmlMapper = XmlMapper().registerKotlinModule()
        expectFailure<InvalidDefinitionException>("GitHub #138 has been fixed!") {
            xmlMapper.readValue<Sms>(xml)
        }
    }
}
