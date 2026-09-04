package com.reneprojects.core.common.cachemanager.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.reneprojects.core.common.cachemanager.entity.CacheMetaDataEntity

@Dao
internal interface CacheMetadataDao {
    @Query("SELECT * FROM cache_metadata where `key` = :key")
    suspend fun getCacheMetaDataEntityByKey(key: String): CacheMetaDataEntity?

    @Upsert
    suspend fun upsert(metadata: CacheMetaDataEntity)

    @Query("DELETE FROM cache_metadata WHERE `key` = :key")
    suspend fun deleteByKey(key: String)

    @Query("DELETE FROM cache_metadata")
    suspend fun deleteAll()
}