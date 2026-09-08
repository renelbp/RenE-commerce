package com.reneprojects.feature.products.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.reneprojects.R
import com.reneprojects.feature.products.model.CarouselHeader
import com.reneprojects.feature.products.model.Product
import com.reneprojects.feature.products.model.ProductCarousel
import com.reneprojects.ui.theme.RenEcommerceTheme

@Composable
internal fun CarouselSection(section: ProductCarousel) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        CarouselHeader(section)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(section.products, key = { it.id }) {
                ProductCard(product = it)
            }
        }
    }

}

@Composable
private fun CarouselHeader(section: ProductCarousel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val linkButtonLabel = stringResource(R.string.ren_ecomme_carousel_view_all_label)
        Text(text = section.header.label, style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = linkButtonLabel, style = MaterialTheme.typography.titleMedium)
            Icon(
                painter = painterResource(id = R.drawable.ic_right_arrow_next), // or Icons.Filled.Add, Icons.Rounded.Menu
                contentDescription = "Home Icon",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProductCard(product: Product) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
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
                error = painterResource(R.drawable.ic_launcher_background)
            )
            Spacer(modifier = Modifier.height(8.dp))
            //Title label
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = product.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            //Price Label
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = product.formattedPrice,
                style = MaterialTheme.typography.bodyMedium
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
                    header = CarouselHeader("Cellphones", linkId = 1),
                    products = productList
                )
            )
        }
    }
}