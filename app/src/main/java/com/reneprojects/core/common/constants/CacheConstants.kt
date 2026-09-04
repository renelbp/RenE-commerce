package com.reneprojects.core.common.constants

import kotlin.time.Duration.Companion.minutes

internal object CacheKeys {
    const val PRODUCTS = "products:all"
}

internal object CachePolicy {
    val PRODUCTS_TTL =
        30.minutes.inWholeMilliseconds
}