package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import kotlin.reflect.KParameter

/**
 * Specialized [JsonMappingException] sub-class used to indicate that a mandatory Kotlin constructor
 * parameter was missing or null.
 */
public class MissingKotlinParameterException internal constructor(
    public val parameter: KParameter,
    processor: JsonParser? = null,
    msg: String
) : MismatchedInputException(processor, msg)
