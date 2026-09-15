package com.reneprojects.feature.products.ui.components.view

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.reneprojects.R
import com.reneprojects.feature.products.ui.components.model.CarouselHeader
import com.reneprojects.feature.products.ui.components.model.IconType
import com.reneprojects.feature.products.ui.components.model.IconType.BEAUTY
import com.reneprojects.feature.products.ui.components.model.IconType.FRAGRANCES
import com.reneprojects.feature.products.ui.components.model.IconType.FURNITURE
import com.reneprojects.feature.products.ui.components.model.IconType.GROCERIES
import com.reneprojects.feature.products.model.Product
import com.reneprojects.feature.products.ui.components.model.ProductCarousel
import com.reneprojects.ui.theme.RenEcommerceTheme

@Composable
internal fun CarouselSection(
    section: ProductCarousel,
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
private fun CarouselHeader(section: ProductCarousel, navigateToProductCategory: (Int) -> Unit) {
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
    section: ProductCarousel
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
    section: ProductCarousel,
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
private fun ProductCard(product: Product, navigateToProductDetails: (Int) -> Unit) {
    Card(
        modifier = Modifier.clickable {
            navigateToProductDetails.invoke(product.id)
        },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    )
    {
        Column(modifier = Modifier.width(150.dp)) {
            AsyncImage(
                modifier = Modifier.size(150.dp),
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = ColorPainter(colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(8.dp))
            //Title label
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = product.title,
                style = typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            //Price Label
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = product.formattedPrice,
                style = typography.titleMedium,
                color = colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))

        }
    }
}

@Preview
@Composable
fun ProductCarouselsPreview() {
    RenEcommerceTheme(dynamicColor = false) {
        val productList = mutableListOf<Product>()
        for (i in 0..5) {
            productList.add(
                Product(
                    id = i,
                    title = "Title $i",
                    formattedPrice = "$$i.00",
                    imageUrl = "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp",
                    category = "Category"
                )
            )

        }
        Surface() {
            CarouselSection(
                ProductCarousel(
                    header = CarouselHeader(
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