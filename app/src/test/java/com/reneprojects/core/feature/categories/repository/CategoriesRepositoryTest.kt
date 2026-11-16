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
import com.reneprojects.core.feature.categories.remote.datasource.CategoriesRemoteDataSource
import com.reneprojects.core.feature.categories.remote.dto.ProductCategoryDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyList
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.anyString
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
internal class CategoriesRepositoryTest {

    @Mock
    private lateinit var remoteDataSource: CategoriesRemoteDataSource

    @Mock
    private lateinit var categoryDao: CategoryDao

    @Mock
    private lateinit var cacheManager: CacheManager

    @Mock
    private lateinit var categoryEntityMapper: CategoryEntityMapper

    private lateinit var repository: CategoriesRepositoryImpl

    private val validCache = CacheStatus(
        exists = true,
        isValid = true,
        fetchedAtMillis = 1_000L,
        expiredAtMillis = 2_000L,
        eTag = "\"valid-etag\""
    )

    private val expiredCache = CacheStatus(
        exists = true,
        isValid = false,
        fetchedAtMillis = 1_000L,
        expiredAtMillis = 2_000L,
        eTag = "\"expired-etag\""
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = CategoriesRepositoryImpl(
            remoteDataSource = remoteDataSource,
            categoryDao = categoryDao,
            cacheManager = cacheManager,
            categoryEntityMapper = categoryEntityMapper
        )
    }

    @Test
    fun loadCategoriesData_returnsSuccess_withoutRemoteCall_whenCacheIsValid() {
        runBlocking {
            `when`(cacheManager.getStatus(CacheKeys.CATEGORIES)).thenReturn(validCache)

            val result = repository.loadCategoriesData(forceRefresh = false)

            assertTrue(result is RenEcommerceResult.Success)
            verify(remoteDataSource, never()).fetchCategories(any())
        }
    }

    @Test
    fun loadCategoriesData_bypassesValidCache_whenForceRefreshIsTrue() {
        runBlocking {
            `when`(cacheManager.getStatus(CacheKeys.CATEGORIES)).thenReturn(validCache)
            `when`(remoteDataSource.fetchCategories(eTag = validCache.eTag))
                .thenReturn(RemoteResult.NotModified)

            repository.loadCategoriesData(forceRefresh = true)

            verify(remoteDataSource).fetchCategories(eTag = validCache.eTag)
        }
    }

    @Test
    fun loadCategoriesData_renewsCacheWithoutTouchingDatabase_whenRemoteIsNotModified() {
        runBlocking {
            `when`(cacheManager.getStatus(CacheKeys.CATEGORIES)).thenReturn(expiredCache)
            `when`(remoteDataSource.fetchCategories(eTag = expiredCache.eTag))
                .thenReturn(RemoteResult.NotModified)

            val result = repository.loadCategoriesData(forceRefresh = false)

            assertTrue(result is RenEcommerceResult.Success)
            verify(cacheManager).updateCache(
                key = CacheKeys.CATEGORIES,
                expirationTimeMillis = CachePolicy.CATEGORIES_TTL,
                eTag = expiredCache.eTag
            )
            verify(categoryDao, never()).replaceCategories(anyList())
        }
    }

    @Test
    fun loadCategoriesData_replacesCategoriesAndUpdatesCache_whenRemoteSucceeds() {
        runBlocking {
            val beautyDto = ProductCategoryDto(slug = "beauty", name = "Beauty")
            val invalidDto = ProductCategoryDto(slug = null, name = null)
            val beautyEntity = CategoryEntity(slug = "beauty", name = "Beauty")

            `when`(cacheManager.getStatus(CacheKeys.CATEGORIES)).thenReturn(expiredCache)
            `when`(remoteDataSource.fetchCategories(eTag = expiredCache.eTag)).thenReturn(
                RemoteResult.Success(
                    data = listOf(beautyDto, invalidDto),
                    eTag = "\"new-etag\""
                )
            )
            `when`(categoryEntityMapper.toCategoryEntity(beautyDto)).thenReturn(beautyEntity)
            `when`(categoryEntityMapper.toCategoryEntity(invalidDto)).thenReturn(null)

            val result = repository.loadCategoriesData(forceRefresh = false)

            assertTrue(result is RenEcommerceResult.Success)
            verify(categoryDao).replaceCategories(categories = listOf(beautyEntity))
            verify(cacheManager).updateCache(
                key = CacheKeys.CATEGORIES,
                expirationTimeMillis = CachePolicy.CATEGORIES_TTL,
                eTag = "\"new-etag\""
            )
        }
    }

    @Test
    fun loadCategoriesData_returnsError_andKeepsLocalData_whenRemoteFails() {
        runBlocking {
            `when`(cacheManager.getStatus(CacheKeys.CATEGORIES)).thenReturn(expiredCache)
            `when`(remoteDataSource.fetchCategories(eTag = expiredCache.eTag))
                .thenReturn(RemoteResult.Error(code = 500, message = "Server error"))

            val result = repository.loadCategoriesData(forceRefresh = false)

            assertTrue(result is RenEcommerceResult.Error)
            assertEquals(
                "Error 500: Server error",
                (result as RenEcommerceResult.Error).exception.message
            )
            verify(categoryDao, never()).replaceCategories(anyList())
            verify(cacheManager, never()).updateCache(anyString(), anyLong(), any())
        }
    }
}
