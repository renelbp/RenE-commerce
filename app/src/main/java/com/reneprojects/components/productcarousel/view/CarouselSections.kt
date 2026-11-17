package com.reneprojects.components.productcarousel.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.reneprojects.R
import com.reneprojects.components.discountbadge.view.DiscountBadge
import com.reneprojects.components.productcarousel.model.CardCarouselData
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.BEAUTY
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.FRAGRANCES
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.FURNITURE
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.GROCERIES
import com.reneprojects.components.productcarousel.model.CardCarouselData.ItemCard
import com.reneprojects.ui.theme.OrangeRating
import com.reneprojects.ui.theme.RenEcommerceTheme

@Composable
internal fun CarouselSection(
    section: CardCarouselData,
    navigateToProductCategory: (Int) -> Unit,
    navigateToProductDetails: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        CarouselHeader(section, navigateToProductCategory)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = CenterVertically
        ) {
            items(section.products, key = { it.id }) {
                ProductCard(product = it, navigateToProductDetails = navigateToProductDetails)
            }
        }
    }

}

@Composable
private fun CarouselHeader(section: CardCarouselData, navigateToProductCategory: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = CenterVertically
    ) {
        val linkButtonLabel = stringResource(R.string.ren_ecomme_carousel_view_all_label)
        HeaderTitleSection(section = section)

        ViewAllSection(
            navigateToProductCategory = navigateToProductCategory,
            section = section,
            linkButtonLabel = linkButtonLabel
        )
    }
}

@Composable
private fun HeaderTitleSection(
    section: CardCarouselData
) {
    Row(
        modifier = Modifier
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = CenterVertically
    ) {
        section.header.iconType?.let {
            val iconAndColor: Pair<Int, Color> = getIconAndColor(it)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .background(color = iconAndColor.second, shape = RoundedCornerShape(16.dp))
            ) {
                Icon(
                    painter = painterResource(id = iconAndColor.first), // or Icons.Filled.Add, Icons.Rounded.Menu
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorScheme.onPrimary
                )
            }

        }

        Text(
            text = section.header.label.replaceFirstChar { it.uppercase() },
            style = typography.titleLarge
        )
    }
}

@Composable
private fun getIconAndColor(iconType: IconType): Pair<Int, Color> {
    return when (iconType) {
        FURNITURE -> Pair(R.drawable.ic_chair_furniture, colorScheme.primary)
        BEAUTY -> Pair(R.drawable.ic_lipstick, colorScheme.secondary)
        FRAGRANCES -> Pair(R.drawable.ic_fragance_perfume, colorScheme.tertiary)
        GROCERIES -> Pair(R.drawable.ic_groceries, colorScheme.secondaryContainer)
    }
}

@Composable
private fun ViewAllSection(
    navigateToProductCategory: (Int) -> Unit,
    section: CardCarouselData,
    linkButtonLabel: String
) {
    Row(
        modifier = Modifier
            .semantics(mergeDescendants = true) {}
            .clickable {
                navigateToProductCategory.invoke(section.header.linkId)
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = CenterVertically
    ) {
        Text(
            text = linkButtonLabel,
            style = typography.titleMedium,
            color = colorScheme.primary
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_right_arrow_next), // or Icons.Filled.Add, Icons.Rounded.Menu
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = colorScheme.primary
        )
    }
}

@Composable
private fun ProductCard(product: ItemCard, navigateToProductDetails: (Int) -> Unit) {
    with(product){
        Card(
            modifier = Modifier.clickable {
                navigateToProductDetails.invoke(id)
            },
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        )
        {
            Column(modifier = Modifier.width(150.dp)) {
                Box {
                    AsyncImage(
                        modifier = Modifier
                            .size(150.dp)
                            // Only the top corners follow the card radius; the image sits at the top.
                            .clip(
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp
                                )
                            ),
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = ColorPainter(colorScheme.onSurfaceVariant)
                    )

                    discountPercentage?.let { discount ->
                        DiscountBadge(
                            discountPercentage = discount,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                //Title label
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = title,
                    style = typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                //Price labels
                PriceSection(
                    formattedPrice = formattedPrice,
                    formattedOriginalPrice = formattedOriginalPrice
                )

                formattedRating?.let { rating ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = OrangeRating
                        )
                        Text(
                            text = rating,
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun PriceSection(formattedPrice: String, formattedOriginalPrice: String?) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = formattedPrice,
            style = typography.titleMedium,
            color = colorScheme.primary
        )

        formattedOriginalPrice?.let { originalPrice ->
            Text(
                modifier = Modifier.alignByBaseline(),
                text = originalPrice,
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun ProductCarouselsPreview() {
    RenEcommerceTheme(dynamicColor = false) {
        val productList = mutableListOf<ItemCard>()
        for (i in 0..5) {
            val hasDiscount = i % 2 == 0
            productList.add(
                ItemCard(
                    id = i,
                    title = "Title $i",
                    formattedPrice = "$$i.00",
                    formattedOriginalPrice = if (hasDiscount) "$${i + 1}.00" else null,
                    discountPercentage = if (hasDiscount) 20 else null,
                    formattedRating = if (i == 0) null else "4.5",
                    imageUrl = "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp",
                    category = "Category"
                )
            )

        }
        Surface() {
            CarouselSection(
                CardCarouselData(
                    header = CardCarouselData.CarouselHeader(
                        FRAGRANCES,
                        "cellphones",
                        linkId = 1
                    ),
                    products = productList,
                ),
                navigateToProductCategory = {},
                navigateToProductDetails = {}

            )
        }
    }
}