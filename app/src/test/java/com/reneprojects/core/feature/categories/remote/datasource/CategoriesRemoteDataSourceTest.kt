package com.reneprojects.core.feature.categories.remote.datasource

import com.reneprojects.core.common.remote.RemoteResult
import com.reneprojects.core.feature.categories.remote.dto.ProductCategoryDto
import com.reneprojects.core.network.api.RenEcommerceApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
internal class CategoriesRemoteDataSourceTest {

    @Mock
    private lateinit var apiService: RenEcommerceApiService

    private lateinit var remoteDataSource: CategoriesRemoteDataSourceImpl

    private val categories = listOf(
        ProductCategoryDto(slug = "beauty", name = "Beauty"),
        ProductCategoryDto(slug = null, name = null)
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        remoteDataSource = CategoriesRemoteDataSourceImpl(apiService = apiService)
    }

    @Test
    fun fetchCategories_returnsSuccess_withBodyAndETag() = runBlocking {
        `when`(apiService.getProductCategoriesResponse(eTag = null)).thenReturn(
            Response.success(
                categories,
                Headers.headersOf("ETag", "\"categories-etag\"")
            )
        )

        val result = remoteDataSource.fetchCategories(eTag = null)

        assertTrue(result is RemoteResult.Success)
        val success = result as RemoteResult.Success
        assertEquals(categories, success.data)
        assertEquals("\"categories-etag\"", success.eTag)
    }

    @Test
    fun fetchCategories_passesETagToTheApi_andKeepsNullWhenHeaderIsMissing() = runBlocking {
        val eTag = "\"cached-etag\""
        `when`(apiService.getProductCategoriesResponse(eTag = eTag)).thenReturn(
            Response.success(categories)
        )

        val result = remoteDataSource.fetchCategories(eTag = eTag)

        assertTrue(result is RemoteResult.Success)
        assertNull((result as RemoteResult.Success).eTag)
    }

    @Test
    fun fetchCategories_returnsNotModified_whenApiAnswers304() = runBlocking {
        @Suppress("UNCHECKED_CAST")
        val notModified = mock(Response::class.java) as Response<List<ProductCategoryDto>>
        `when`(notModified.code()).thenReturn(304)
        `when`(apiService.getProductCategoriesResponse(eTag = null)).thenReturn(notModified)

        val result = remoteDataSource.fetchCategories(eTag = null)

        assertEquals(RemoteResult.NotModified, result)
    }

    @Test
    fun fetchCategories_returnsError_whenResponseIsUnsuccessful() = runBlocking {
        `when`(apiService.getProductCategoriesResponse(eTag = null)).thenReturn(
            Response.error<List<ProductCategoryDto>>(
                500,
                "{}".toResponseBody("application/json".toMediaType())
            )
        )

        val result = remoteDataSource.fetchCategories(eTag = null)

        assertTrue(result is RemoteResult.Error)
        assertEquals(500, (result as RemoteResult.Error).code)
    }

    @Test
    fun fetchCategories_returnsError_whenApiThrows() = runBlocking {
        `when`(apiService.getProductCategoriesResponse(eTag = null)).thenAnswer {
            throw IllegalStateException("boom")
        }

        val result = remoteDataSource.fetchCategories(eTag = null)

        assertTrue(result is RemoteResult.Error)
        assertEquals(-1, (result as RemoteResult.Error).code)
    }
}
