package com.reneprojects.feature.products.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reneprojects.R
import com.reneprojects.ui.theme.RenEcommerceTheme
import com.reneprojects.utils.extension.shimmerEffect

private const val GRID_COLUMNS = 2
private const val SKELETON_ITEM_COUNT = 6
private const val SCREEN_HORIZONTAL_PADDING = 16
private const val CARD_SPACING = 8

/**
 * Shimmer placeholder shown while the filtered product grid is being prepared.
 *
 * Mirrors the real grid geometry (two columns, 16.dp side margins, 8.dp spacing, 4:5 image) so the
 * transition to the results does not move the layout.
 *
 * @param modifier Applied to the grid root.
 */
@Composable
internal fun LoadingFilteredProductsSkeleton(modifier: Modifier = Modifier) {
    val loadingMessage = stringResource(R.string.ren_ecomme_products_loading)

    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = modifier
            .fillMaxSize()
            .clearAndSetSemantics { contentDescription = loadingMessage },
        contentPadding = PaddingValues(
            start = SCREEN_HORIZONTAL_PADDING.dp,
            end = SCREEN_HORIZONTAL_PADDING.dp,
            top = 8.dp,
            bottom = 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(CARD_SPACING.dp),
        verticalArrangement = Arrangement.spacedBy(CARD_SPACING.dp),
        userScrollEnabled = false,
    ) {
        items(SKELETON_ITEM_COUNT) {
            SkeletonProductCard()
        }
    }
}

@Composable
private fun SkeletonProductCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .shimmerEffect(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(16.dp)
                .shimmerEffect(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(24.dp)
                .shimmerEffect(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(16.dp)
                .shimmerEffect(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(12.dp)
                .shimmerEffect(RoundedCornerShape(6.dp))
        )
    }
}

@Preview
@Composable
private fun LoadingFilteredProductsSkeletonPreview() {
    RenEcommerceTheme(dynamicColor = false) {
        Surface {
            LoadingFilteredProductsSkeleton(
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
