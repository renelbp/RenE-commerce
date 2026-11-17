package com.reneprojects.components.productcard.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.reneprojects.components.discountbadge.view.DiscountBadge
import com.reneprojects.components.productcard.model.ProductCardData
import com.reneprojects.ui.theme.OrangeRating
import com.reneprojects.ui.theme.RenEcommerceTheme

/**
 * Product card designed for the filtered two-column grid.
 *
 * Height is driven by the content: the image keeps a 4:5 ratio and the text block grows as needed.
 *
 * @param data Display-ready product data.
 * @param onClick Called with the product id when the card is tapped.
 * @param modifier Applied to the card root.
 */
@Composable
internal fun ProductCard(
    data: ProductCardData,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onClick(data.id) },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f),
                model = data.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = ColorPainter(MaterialTheme.colorScheme.onSurfaceVariant),
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                data.discountPercentage?.let { discount ->
                    Spacer(modifier = Modifier.height(4.dp))
                    DiscountBadge(discountPercentage = discount)
                }

                data.brand?.let { brand ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    Text(
                        text = data.formattedPrice,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    data.formattedOriginalPrice?.let { originalPrice ->
                        Text(
                            text = originalPrice,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = OrangeRating,
                    )
                    Text(
                        text = data.formattedRating,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProductCardPreview() {
    RenEcommerceTheme(dynamicColor = false) {
        Surface {
            ProductCard(
                data = ProductCardData(
                    id = 1,
                    title = "Essence Mascara Lash Princess",
                    formattedPrice = "$8.49",
                    formattedOriginalPrice = "$9.99",
                    discountPercentage = 15,
                    formattedRating = "4.9",
                    brand = "Essence",
                    imageUrl = "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp",
                ),
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun ProductCardWithoutDiscountPreview() {
    RenEcommerceTheme(dynamicColor = false) {
        Surface {
            ProductCard(
                data = ProductCardData(
                    id = 2,
                    title = "Eyeshadow Palette with Mirror",
                    formattedPrice = "$19.99",
                    formattedOriginalPrice = null,
                    discountPercentage = null,
                    formattedRating = "3.9",
                    brand = "Glamour Beauty",
                    imageUrl = "https://cdn.dummyjson.com/product-images/beauty/eyeshadow-palette-with-mirror/thumbnail.webp",
                ),
                onClick = {},
            )
        }
    }
}
