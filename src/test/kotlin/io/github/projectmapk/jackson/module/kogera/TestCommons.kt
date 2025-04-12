package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

// This `printer` is used to match the output from Jackson to the newline char of the source code.
// If this is removed, comparisons will fail in a Windows-like platform.
internal val LF_PRINTER: DefaultPrettyPrinter =
    DefaultPrettyPrinter().withObjectIndenter(DefaultIndenter().withLinefeed("\n"))

internal fun ObjectMapper.testPrettyWriter(): ObjectWriter = this.writer(LF_PRINTER)

internal fun Class<*>.isKotlinClass() = declaredAnnotations.any { it is Metadata }

internal val defaultMapper = jacksonObjectMapper()

internal inline fun <reified T : Any> callPrimaryConstructor(
    mapper: (KParameter) -> Any? = { it.name }
): T = T::class.primaryConstructor!!.run {
    val args = parameters.associateWith { mapper(it) }
    callBy(args)
}

// Function for comparing non-data classes.
internal inline fun <reified T : Any> assertReflectEquals(expected: T, actual: T) {
    T::class.memberProperties.forEach {
        assertEquals(it.get(expected), it.get(actual))
    }
}

internal fun createTempJson(json: String): File {
    val file = File.createTempFile("temp", ".json")
    file.deleteOnExit()
    OutputStreamWriter(
        FileOutputStream(file),
        StandardCharsets.UTF_8
    ).use { writer ->
        writer.write(json)
        writer.flush()
    }
    return file
}
