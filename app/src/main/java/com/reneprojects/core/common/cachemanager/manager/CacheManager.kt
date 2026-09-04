package com.reneprojects.core.common.cachemanager.manager

import com.reneprojects.core.common.cachemanager.dao.CacheMetadataDao
import com.reneprojects.core.common.cachemanager.entity.CacheMetaDataEntity
import com.reneprojects.core.common.cachemanager.model.CacheStatus
import com.reneprojects.core.common.cachemanager.timeprovider.TimeProvider
import com.reneprojects.core.common.cachemanager.timeprovider.TimeProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface CacheManagerModule {
    @Binds
    @Singleton
    fun bindCacheManager(
        implementation: CacheManagerImpl
    ): CacheManager

    @Binds
    @Singleton
    fun bindTimeProvider(
        implementation: TimeProviderImpl
    ): TimeProvider
}

internal interface CacheManager {
    suspend fun getStatus(cacheKey: String): CacheStatus
    suspend fun updateCache(
        key: String,
        expirationTimeMillis: Long,
        eTag: String?
    )

    suspend fun invalidate(key: String)
    suspend fun clear()
}

internal class CacheManagerImpl @Inject constructor(
    private val cacheMetadataDao: CacheMetadataDao,
    private val timeProvider: TimeProvider
) : CacheManager {
    override suspend fun getStatus(cacheKey: String): CacheStatus {
        val metaDataEntity =
            cacheMetadataDao.getCacheMetaDataEntityByKey(key = cacheKey) ?: return CacheStatus(
                exists = false,
                isValid = false,
                fetchedAtMillis = null,
                expiredAtMillis = null,
                eTag = null
            )
        val currentTime = timeProvider.currentTimeMillis()
        return CacheStatus(
            exists = true,
            isValid = currentTime < metaDataEntity.expiresAtMillis,
            fetchedAtMillis = metaDataEntity.fetchedAtMillis,
            expiredAtMillis = metaDataEntity.expiresAtMillis,
            eTag = metaDataEntity.eTag
        )
    }

    override suspend fun updateCache(
        key: String,
        expirationTimeMillis: Long,
        eTag: String?
    ) {
        require(key.isNotBlank()) {
            "Cache key cannot be blank"
        }
        require(expirationTimeMillis > 0) {
            "Cache expiration time must be greater than zero"
        }
        val currentTime = timeProvider.currentTimeMillis()
        cacheMetadataDao.upsert(
            CacheMetaDataEntity(
                key = key,
                fetchedAtMillis = currentTime,
                expiresAtMillis =
                    currentTime + expirationTimeMillis,
                eTag = eTag
            )
        )
    }

    override suspend fun invalidate(key: String) {
        cacheMetadataDao.deleteByKey(key)
    }

    override suspend fun clear() {
        cacheMetadataDao.deleteAll()
    }
}