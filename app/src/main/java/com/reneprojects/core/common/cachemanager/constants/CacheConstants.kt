package com.reneprojects.core.common.cachemanager.constants

import kotlin.time.Duration.Companion.minutes

internal object CacheKeys {
    const val PRODUCTS = "products:all"
    const val CATEGORIES = "categories:all"
}

internal object CachePolicy {
    val PRODUCTS_TTL =
        30.minutes.inWholeMilliseconds

    val CATEGORIES_TTL =
        30.minutes.inWholeMilliseconds
}
