package com.reneprojects.feature.products.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.reneprojects.feature.products.model.ProductUiModel

@Composable
internal fun ProductsSection(
    productList: List<ProductUiModel>,
    modifier: Modifier,
) {
    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        items(items = productList, key = { it.id }) { product ->
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = product.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = product.title.orEmpty(), style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "$${product.formattedPrice}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}