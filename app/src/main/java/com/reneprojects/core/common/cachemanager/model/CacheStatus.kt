package com.reneprojects.core.common.cachemanager.model

internal data class CacheStatus(
    val exists: Boolean,
    val isValid: Boolean,
    val fetchedAtMillis: Long?,
    val expiredAtMillis: Long?,
    val eTag: String?
)
