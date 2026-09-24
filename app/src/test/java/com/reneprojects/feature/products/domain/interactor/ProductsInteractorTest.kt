package com.reneprojects.feature.products.domain.interactor

import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.core.feature.products.repository.ProductRepository
import com.reneprojects.feature.products.domain.mapper.ProductMapper
import com.reneprojects.feature.products.ui.components.model.CarouselHeader
import com.reneprojects.feature.products.ui.components.model.IconType
import com.reneprojects.feature.products.ui.components.model.ProductCarousel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
internal class ProductsInteractorTest {

    @Mock
    private lateinit var repository: ProductRepository

    @Mock
    private lateinit var mapper: ProductMapper

    private lateinit var spyInteractor: ProductsInteractorImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        spyInteractor = spy(ProductsInteractorImpl(repository = repository, mapper = mapper))
    }

    @Test
    fun observeProductSections_groupsProductsByCategory_andPassesMappedIconTypes() = runBlocking {
        val beautyProductOne = productEntity(id = 1, category = "beauty")
        val beautyProductTwo = productEntity(id = 2, category = "beauty")
        val furnitureProduct = productEntity(id = 3, category = "FURNITURE")
        val fragrancesProduct = productEntity(id = 4, category = " fragrances ")
        val groceriesProduct = productEntity(id = 5, category = "groceries")
        val otherProduct = productEntity(id = 6, category = "electronics")

        val beautyCarousel = carousel(label = "beauty", linkId = 1, iconType = IconType.BEAUTY)
        val furnitureCarousel =
            carousel(label = "FURNITURE", linkId = 3, iconType = IconType.FURNITURE)
        val fragrancesCarousel =
            carousel(label = " fragrances ", linkId = 4, iconType = IconType.FRAGRANCES)
        val groceriesCarousel =
            carousel(label = "groceries", linkId = 5, iconType = IconType.GROCERIES)
        val otherCarousel = carousel(label = "electronics", linkId = 6, iconType = null)

        `when`(repository.observeProducts()).thenReturn(
            flowOf(
                listOf(
                    beautyProductOne,
                    beautyProductTwo,
                    furnitureProduct,
                    fragrancesProduct,
                    groceriesProduct,
                    otherProduct
                )
            )
        )
        `when`(
            mapper.toProductSections(
                category = "beauty",
                products = listOf(beautyProductOne, beautyProductTwo),
                iconType = IconType.BEAUTY
            )
        ).thenReturn(beautyCarousel)
        `when`(
            mapper.toProductSections(
                category = "FURNITURE",
                products = listOf(furnitureProduct),
                iconType = IconType.FURNITURE
            )
        ).thenReturn(furnitureCarousel)
        `when`(
            mapper.toProductSections(
                category = " fragrances ",
                products = listOf(fragrancesProduct),
                iconType = IconType.FRAGRANCES
            )
        ).thenReturn(fragrancesCarousel)
        `when`(
            mapper.toProductSections(
                category = "groceries",
                products = listOf(groceriesProduct),
                iconType = IconType.GROCERIES
            )
        ).thenReturn(groceriesCarousel)
        `when`(
            mapper.toProductSections(
                category = "electronics",
                products = listOf(otherProduct),
                iconType = null
            )
        ).thenReturn(otherCarousel)

        val result = spyInteractor.observeProductSections().first()

        assertEquals(
            listOf(
                beautyCarousel,
                furnitureCarousel,
                fragrancesCarousel,
                groceriesCarousel,
                otherCarousel
            ),
            result
        )
    }

    @Test
    fun loadProductData_delegatesFalseFlagToRepository() = runBlocking {
        val expectedResult = RenEcommerceResult.Success(Unit)
        `when`(repository.loadProductData(false)).thenReturn(expectedResult)

        val result = spyInteractor.loadProductData(forceRefresh = false)

        assertSame(expectedResult, result)
    }

    @Test
    fun loadProductData_delegatesTrueFlagToRepository() = runBlocking {
        val expectedResult = RenEcommerceResult.Success(Unit)
        `when`(repository.loadProductData(true)).thenReturn(expectedResult)

        val result = spyInteractor.loadProductData(forceRefresh = true)

        assertSame(expectedResult, result)
    }

    private fun carousel(
        label: String,
        linkId: Int,
        iconType: IconType?
    ): ProductCarousel {
        return ProductCarousel(
            header = CarouselHeader(
                iconType = iconType,
                label = label,
                linkId = linkId
            ),
            products = emptyList()
        )
    }

    private fun productEntity(
        id: Int,
        category: String
    ): ProductEntity {
        return ProductEntity(
            id = id,
            title = "Title $id",
            description = "Description $id",
            category = category,
            price = id.toDouble(),
            discountPercentage = 0.0,
            rating = 0.0,
            stock = id,
            brand = null,
            thumbnail = "https://example.com/$id"
        )
    }
}
