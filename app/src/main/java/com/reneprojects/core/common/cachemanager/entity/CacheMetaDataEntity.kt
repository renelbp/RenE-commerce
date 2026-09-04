package com.reneprojects.core.common.cachemanager.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_metadata")
data class CacheMetaDataEntity(
    @PrimaryKey
    val key: String,
    val fetchedAtMillis: Long,
    val expiresAtMillis: Long,
    val eTag: String?
)
