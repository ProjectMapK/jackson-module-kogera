package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.deser.value_instantiator.creator.ValueParameter

/**
 * Specialized [JsonMappingException] sub-class used to indicate that a mandatory Kotlin constructor
 * parameter was missing or null.
 */
public class MissingKotlinParameterException internal constructor(
    internal val parameter: ValueParameter,
    processor: JsonParser? = null,
    msg: String
) : MismatchedInputException(processor, msg)
