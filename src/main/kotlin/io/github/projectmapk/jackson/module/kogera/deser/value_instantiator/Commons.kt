package io.github.projectmapk.jackson.module.kogera.deser.value_instantiator

internal fun calcMaskSize(argumentSize: Int): Int = (argumentSize + Integer.SIZE - 1) / Integer.SIZE
