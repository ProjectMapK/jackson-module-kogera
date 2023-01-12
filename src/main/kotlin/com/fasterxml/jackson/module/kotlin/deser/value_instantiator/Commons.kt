package com.fasterxml.jackson.module.kotlin.deser.value_instantiator

internal fun calcMaskSize(argumentSize: Int): Int = (argumentSize + Integer.SIZE - 1) / Integer.SIZE
