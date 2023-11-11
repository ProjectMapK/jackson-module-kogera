package io.github.projectmapk.jackson.module.kogera.deser.valueInstantiator

internal fun calcMaskSize(argumentSize: Int): Int = (argumentSize + Integer.SIZE - 1) / Integer.SIZE
