package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class Github738 {
    data class D(@JsonSetter(nulls = Nulls.FAIL) val v: Int)

    @Test
    fun test() {
        val mapper = jacksonObjectMapper()
        // nulls = FAIL is reflected if it is primitive and missing
        assertThrows<MismatchedInputException> { mapper.readValue<D>("{}") }
    }
}
