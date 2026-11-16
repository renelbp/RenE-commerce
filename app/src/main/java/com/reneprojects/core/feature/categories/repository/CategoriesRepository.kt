package com.reneprojects.core.feature.categories.repository

import com.reneprojects.core.common.cachemanager.manager.CacheManager
import com.reneprojects.core.common.cachemanager.model.CacheStatus
import com.reneprojects.core.common.cachemanager.constants.CacheKeys
import com.reneprojects.core.common.cachemanager.constants.CachePolicy
import com.reneprojects.core.common.remote.RemoteResult
import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.feature.categories.local.dao.CategoryDao
import com.reneprojects.core.feature.categories.local.entity.CategoryEntity
import com.reneprojects.core.feature.categories.mapper.CategoryEntityMapper
import com.reneprojects.core.feature.categories.mapper.CategoryEntityMapperModule
import com.reneprojects.core.feature.categories.remote.datasource.CategoriesRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** region module */
@Module(
    includes = [
        CategoryEntityMapperModule::class
    ]
)
@InstallIn(SingletonComponent::class)
internal interface CategoriesRepositoryModule {
    @Binds
    @Singleton
    fun bindCategoriesRepository(
        implementation: CategoriesRepositoryImpl
    ): CategoriesRepository
}
/** endregion module */

/** region abstraction */
interface CategoriesRepository {
    /**
     * Exposes the locally stored categories and keeps the UI in sync with Room.
     *
     * @return A stream of categories read from the database.
     */
    fun observeCategories(): Flow<List<CategoryEntity>>

    /**
     * Synchronizes local categories with the remote endpoint, honoring the cache policy.
     *
     * @param forceRefresh Whether to bypass local cache validation.
     * @return The result of the load operation.
     */
    suspend fun loadCategoriesData(forceRefresh: Boolean = false): RenEcommerceResult<Unit>
}
/** endregion abstraction */

/** region implementation */
internal class CategoriesRepositoryImpl @Inject constructor(
    private val remoteDataSource: CategoriesRemoteDataSource,
    private val categoryDao: CategoryDao,
    private val cacheManager: CacheManager,
    private val categoryEntityMapper: CategoryEntityMapper
) : CategoriesRepository {

    override fun observeCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.observeCategories()
    }

    override suspend fun loadCategoriesData(forceRefresh: Boolean): RenEcommerceResult<Unit> =
        withContext(Dispatchers.IO) {
            val cacheKey = CacheKeys.CATEGORIES
            val cacheStatus = cacheManager.getStatus(cacheKey)

            if (!forceRefresh && cacheStatus.isValid) {
                RenEcommerceResult.Success(Unit)
            } else {
                updateCategories(
                    cacheKey = cacheKey,
                    cacheStatus = cacheStatus
                )
            }
        }

    /**
     * Synchronizes local categories with the remote response using `ETag`\-based cache control.
     *
     * Behavior:
     * \- `NotModified`: refreshes cache validity without changing local data.
     * \- `Success`: maps and replaces local categories, then updates cache metadata.
     * \- `Error`: returns a failure with the remote code and message.
     *
     * @param cacheKey Cache key for categories.
     * @param cacheStatus Current cache status, including validity and `ETag`.
     * @return `RenEcommerceResult.Success(Unit)` when synchronization or cache renewal succeeds;
     * `RenEcommerceResult.Error` when the remote request fails.
     */
    private suspend fun updateCategories(
        cacheKey: String,
        cacheStatus: CacheStatus
    ): RenEcommerceResult<Unit> {
        return when (val remoteResult = remoteDataSource.fetchCategories(eTag = cacheStatus.eTag)) {
            is RemoteResult.NotModified -> {
                cacheManager.updateCache(
                    key = cacheKey,
                    expirationTimeMillis = CachePolicy.CATEGORIES_TTL,
                    eTag = cacheStatus.eTag
                )
                RenEcommerceResult.Success(Unit)
            }

            is RemoteResult.Success -> {
                val categories = remoteResult.data.mapNotNull {
                    categoryEntityMapper.toCategoryEntity(it)
                }

                categoryDao.replaceCategories(
                    categories = categories
                )

                cacheManager.updateCache(
                    key = cacheKey,
                    expirationTimeMillis = CachePolicy.CATEGORIES_TTL,
                    eTag = remoteResult.eTag
                )
                RenEcommerceResult.Success(Unit)
            }

            is RemoteResult.Error -> {
                RenEcommerceResult.Error(
                    exception = Exception("Error ${remoteResult.code}: ${remoteResult.message}")
                )
            }
        }
    }
}
/** endregion implementation */
