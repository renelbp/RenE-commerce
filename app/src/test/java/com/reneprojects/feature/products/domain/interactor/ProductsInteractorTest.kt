package com.reneprojects.feature.products.domain.interactor

import com.reneprojects.components.productcarousel.model.CardCarouselData
import com.reneprojects.components.productcarousel.model.CardCarouselData.CarouselHeader
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.BEAUTY
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.FRAGRANCES
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.FURNITURE
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.GROCERIES
import com.reneprojects.components.productcard.model.ProductCardData
import com.reneprojects.components.productfilter.model.ItemFilterData
import com.reneprojects.common.constants.CategorySlug
import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.feature.categories.local.entity.CategoryEntity
import com.reneprojects.core.feature.categories.repository.CategoriesRepository
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.core.feature.products.repository.ProductRepository
import com.reneprojects.feature.products.domain.mapper.ProductMapper
import com.reneprojects.feature.products.domain.mapper.ProductMapperImpl
import com.reneprojects.feature.products.model.LandingPageData
import com.reneprojects.feature.products.model.LandingPageData.ProductCarouselSectionData
import com.reneprojects.feature.products.model.LandingPageData.ProductFilterSectionData
import com.reneprojects.utils.extension.toPriceString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    private lateinit var categoriesRepository: CategoriesRepository

    @Mock
    private lateinit var mapper: ProductMapper

    private lateinit var spyInteractor: ProductsInteractorImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        spyInteractor = spy(
            ProductsInteractorImpl(
                productsRepository = repository,
                categoriesRepository = categoriesRepository,
                mapper = mapper
            )
        )
    }

    @Test
    fun observeProductSections_groupsProductsByCategory_andPassesMappedIconTypes() = runBlocking {
        val beautyProductOne = productEntity(id = 1, category = "beauty")
        val beautyProductTwo = productEntity(id = 2, category = "beauty")
        val furnitureProduct = productEntity(id = 3, category = "FURNITURE")
        val fragrancesProduct = productEntity(id = 4, category = " fragrances ")
        val groceriesProduct = productEntity(id = 5, category = "groceries")
        val otherProduct = productEntity(id = 6, category = "electronics")

        val beautyCarousel = carousel(label = "beauty", linkId = 1, iconType = BEAUTY)
        val furnitureCarousel =
            carousel(label = "FURNITURE", linkId = 3, iconType = FURNITURE)
        val fragrancesCarousel =
            carousel(label = " fragrances ", linkId = 4, iconType = FRAGRANCES)
        val groceriesCarousel =
            carousel(label = "groceries", linkId = 5, iconType = GROCERIES)
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
        `when`(categoriesRepository.observeCategories()).thenReturn(flowOf(emptyList()))
        `when`(mapper.toProductFilterSection(emptyList())).thenReturn(null)
        `when`(
            mapper.toProductSections(
                category = "beauty",
                products = listOf(beautyProductOne, beautyProductTwo),
                iconType = BEAUTY
            )
        ).thenReturn(beautyCarousel)
        `when`(
            mapper.toProductSections(
                category = "FURNITURE",
                products = listOf(furnitureProduct),
                iconType = FURNITURE
            )
        ).thenReturn(furnitureCarousel)
        `when`(
            mapper.toProductSections(
                category = " fragrances ",
                products = listOf(fragrancesProduct),
                iconType = FRAGRANCES
            )
        ).thenReturn(fragrancesCarousel)
        `when`(
            mapper.toProductSections(
                category = "groceries",
                products = listOf(groceriesProduct),
                iconType = GROCERIES
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
    fun observeProductSections_emitsFilterSectionFirst_whenCategoriesAreAvailable() =
        runBlocking {
            val categories = listOf(
                CategoryEntity(
                    slug = CategorySlug.FURNITURE,
                    name = "Furniture"
                )
            )
            val filterSection = ProductFilterSectionData(
                filters = listOf(
                    ItemFilterData(
                        slug = CategorySlug.FURNITURE,
                        label = "Furniture",
                        iconType = ItemFilterData.IconType.FURNITURE
                    )
                )
            )
            val product = productEntity(id = 7, category = "beauty")
            val beautyCarousel = carousel(label = "beauty", linkId = 7, iconType = BEAUTY)

            `when`(repository.observeProducts()).thenReturn(flowOf(listOf(product)))
            `when`(categoriesRepository.observeCategories()).thenReturn(flowOf(categories))
            `when`(mapper.toProductFilterSection(categories)).thenReturn(filterSection)
            `when`(
                mapper.toProductSections(
                    category = "beauty",
                    products = listOf(product),
                    iconType = BEAUTY
                )
            ).thenReturn(beautyCarousel)

            val result = spyInteractor.observeProductSections().first()

            assertEquals(listOf(filterSection, beautyCarousel), result)
        }

    @Test
    fun observeProductSections_mapsDiscountOriginalPriceAndRating() = runBlocking {
        val product = productEntity(
            id = 7,
            category = "beauty",
            price = 100.0,
            discountPercentage = 12.4,
            rating = 4.56
        )
        `when`(repository.observeProducts()).thenReturn(flowOf(listOf(product)))
        `when`(categoriesRepository.observeCategories()).thenReturn(flowOf(emptyList()))

        val result = listingInteractor()
            .observeProductSections()
            .first()
            .filterIsInstance<ProductCarouselSectionData>()
            .single()
            .cardCarouselData
            .products
            .single()

        assertEquals(87.6.toPriceString(), result.formattedPrice)
        assertEquals(100.0.toPriceString(), result.formattedOriginalPrice)
        assertEquals(12, result.discountPercentage)
        assertEquals("4.6", result.formattedRating)
    }

    @Test
    fun observeProductSections_hidesDiscountAndRating_whenTheyAreNotMeaningful() = runBlocking {
        val product = productEntity(
            id = 8,
            category = "beauty",
            discountPercentage = 0.3,
            rating = 0.0
        )
        `when`(repository.observeProducts()).thenReturn(flowOf(listOf(product)))
        `when`(categoriesRepository.observeCategories()).thenReturn(flowOf(emptyList()))

        val result = listingInteractor()
            .observeProductSections()
            .first()
            .filterIsInstance<ProductCarouselSectionData>()
            .single()
            .cardCarouselData
            .products
            .single()

        assertNull(result.formattedOriginalPrice)
        assertNull(result.discountPercentage)
        assertNull(result.formattedRating)
    }

    @Test
    fun observeProductListing_returnsEmpty_whenNoCategorySelected() = runBlocking {
        `when`(repository.observeProducts())
            .thenReturn(flowOf(listOf(productEntity(id = 1, category = "beauty"))))

        val result = listingInteractor()
            .observeProductListing(flowOf(null))
            .first()

        assertEquals(emptyList<ProductCardData>(), result)
    }

    @Test
    fun observeProductListing_filtersByNormalizedCategory_andSortsById() = runBlocking {
        val beautyOne = productEntity(id = 5, category = " BEAUTY ")
        val beautyTwo = productEntity(id = 2, category = "beauty")
        val furniture = productEntity(id = 3, category = "furniture")
        `when`(repository.observeProducts())
            .thenReturn(flowOf(listOf(beautyOne, furniture, beautyTwo)))

        val result = listingInteractor()
            .observeProductListing(flowOf("beauty"))
            .first()

        assertEquals(listOf(2, 5), result.map { it.id })
    }

    @Test
    fun observeProductListing_othersReturnsProductsOutsideSupportedCategories() = runBlocking {
        val beauty = productEntity(id = 1, category = "beauty")
        val electronics = productEntity(id = 6, category = "electronics")
        val smartphones = productEntity(id = 4, category = "smartphones")
        `when`(repository.observeProducts())
            .thenReturn(flowOf(listOf(beauty, electronics, smartphones)))

        val result = listingInteractor()
            .observeProductListing(flowOf(CategorySlug.OTHERS))
            .first()

        assertEquals(listOf(4, 6), result.map { it.id })
    }

    @Test
    fun observeProductListing_mapsDisplayFields() = runBlocking {
        val product = productEntity(
            id = 9,
            category = "beauty",
            rating = 4.5,
            brand = "  Essence  "
        )
        `when`(repository.observeProducts()).thenReturn(flowOf(listOf(product)))

        val result = listingInteractor()
            .observeProductListing(flowOf("beauty"))
            .first()
            .single()

        assertEquals(9, result.id)
        assertEquals("Title 9", result.title)
        assertEquals("4.5", result.formattedRating)
        assertEquals("Essence", result.brand)
        assertEquals(product.thumbnail, result.imageUrl)
    }

    @Test
    fun observeProductListing_roundsDiscount_andShowsBothPrices() = runBlocking {
        val product = productEntity(
            id = 3,
            category = "beauty",
            price = 100.0,
            discountPercentage = 12.4
        )
        `when`(repository.observeProducts()).thenReturn(flowOf(listOf(product)))

        val result = listingInteractor()
            .observeProductListing(flowOf("beauty"))
            .first()
            .single()

        assertEquals(12, result.discountPercentage)
        assertEquals(87.6.toPriceString(), result.formattedPrice)
        assertEquals(100.0.toPriceString(), result.formattedOriginalPrice)
    }

    @Test
    fun observeProductListing_hidesDiscount_whenItRoundsToZero() = runBlocking {
        val product = productEntity(
            id = 4,
            category = "beauty",
            price = 50.0,
            discountPercentage = 0.3
        )
        `when`(repository.observeProducts()).thenReturn(flowOf(listOf(product)))

        val result = listingInteractor()
            .observeProductListing(flowOf("beauty"))
            .first()
            .single()

        assertNull(result.discountPercentage)
        assertNull(result.formattedOriginalPrice)
        assertEquals(50.0.toPriceString(), result.formattedPrice)
    }

    @Test
    fun observeProductListing_hidesDiscount_whenZero() = runBlocking {
        val product = productEntity(
            id = 5,
            category = "beauty",
            discountPercentage = 0.0
        )
        `when`(repository.observeProducts()).thenReturn(flowOf(listOf(product)))

        val result = listingInteractor()
            .observeProductListing(flowOf("beauty"))
            .first()
            .single()

        assertNull(result.discountPercentage)
        assertNull(result.formattedOriginalPrice)
    }

    private fun listingInteractor(): ProductsInteractorImpl {
        return ProductsInteractorImpl(
            productsRepository = repository,
            categoriesRepository = categoriesRepository,
            mapper = ProductMapperImpl()
        )
    }

    @Test
    fun loadProductData_delegatesFalseFlagToRepository() = runBlocking {
        val expectedResult = RenEcommerceResult.Success(Unit)
        `when`(repository.loadProductData(false)).thenReturn(expectedResult)
        `when`(categoriesRepository.loadCategoriesData(false))
            .thenReturn(RenEcommerceResult.Success(Unit))

        val result = spyInteractor.loadProductData(forceRefresh = false)

        assertSame(expectedResult, result)
    }

    @Test
    fun loadProductData_delegatesTrueFlagToRepository() = runBlocking {
        val expectedResult = RenEcommerceResult.Success(Unit)
        `when`(repository.loadProductData(true)).thenReturn(expectedResult)
        `when`(categoriesRepository.loadCategoriesData(true))
            .thenReturn(RenEcommerceResult.Success(Unit))

        val result = spyInteractor.loadProductData(forceRefresh = true)

        assertSame(expectedResult, result)
    }

    @Test
    fun loadProductData_returnsProductsResult_evenWhenCategoriesFail() = runBlocking {
        val expectedResult = RenEcommerceResult.Success(Unit)
        `when`(repository.loadProductData(false)).thenReturn(expectedResult)
        `when`(categoriesRepository.loadCategoriesData(false))
            .thenReturn(RenEcommerceResult.Error(exception = Exception("categories failed")))

        val result = spyInteractor.loadProductData(forceRefresh = false)

        assertSame(expectedResult, result)
    }

    private fun carousel(
        label: String,
        linkId: Int,
        iconType: IconType?
    ): LandingPageData.ProductCarouselSectionData {
        return LandingPageData.ProductCarouselSectionData(
            cardCarouselData = CardCarouselData(
                header = CarouselHeader(
                    iconType = iconType,
                    label = label,
                    linkId = linkId
                ),
                products = emptyList()
            ),
        )
    }

    private fun productEntity(
        id: Int,
        category: String,
        price: Double = id.toDouble(),
        discountPercentage: Double = 0.0,
        rating: Double = 0.0,
        brand: String? = null
    ): ProductEntity {
        return ProductEntity(
            id = id,
            title = "Title $id",
            description = "Description $id",
            category = category,
            price = price,
            discountPercentage = discountPercentage,
            rating = rating,
            stock = id,
            brand = brand,
            thumbnail = "https://example.com/$id"
        )
    }
}
