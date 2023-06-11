package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter

// This `printer` is used to match the output from Jackson to the newline char of the source code.
// If this is removed, comparisons will fail in a Windows-like platform.
internal val LF_PRINTER: DefaultPrettyPrinter =
    DefaultPrettyPrinter().withObjectIndenter(DefaultIndenter().withLinefeed("\n"))

internal fun ObjectMapper.testPrettyWriter(): ObjectWriter = this.writer(LF_PRINTER)

internal fun Class<*>.isKotlinClass() = declaredAnnotations.any { it is Metadata }
