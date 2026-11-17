package com.reneprojects.components.productfilter.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reneprojects.R
import com.reneprojects.common.constants.CategorySlug
import com.reneprojects.components.productfilter.model.ItemFilterData
import com.reneprojects.components.productfilter.model.ItemFilterData.IconType
import com.reneprojects.components.productfilter.model.ItemFilterData.IconType.BEAUTY
import com.reneprojects.components.productfilter.model.ItemFilterData.IconType.FRAGRANCES
import com.reneprojects.components.productfilter.model.ItemFilterData.IconType.FURNITURE
import com.reneprojects.components.productfilter.model.ItemFilterData.IconType.GROCERIES
import com.reneprojects.ui.theme.RenEcommerceTheme

@Composable
internal fun ProductFilterSection(
    filters: List<ItemFilterData>,
    selectedSlug: String?,
    onFilterSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
) {
    LazyRow(
        modifier = modifier.background(colorScheme.background),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filters, key = { it.slug }) { filter ->
            ProductFilterChip(
                filter = filter,
                selected = filter.slug == selectedSlug,
                onClick = { onFilterSelected(filter.slug) }
            )
        }
    }
}

@Composable
private fun ProductFilterChip(
    filter: ItemFilterData,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = if (selected) {
            colorScheme.primary
        } else {
            Color.White
        },
        border = if (selected) {
            null
        } else {
            BorderStroke(
                width = 1.dp,
                color = colorScheme.onPrimary
            )
        },
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val iconRes = getFilterIcon(filter.iconType)
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (selected) {
                        Color.White
                    } else {
                        Color(0xFF1D2433)
                    }
                )
            }

            Text(
                text = filter.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (selected) {
                    Color.White
                } else {
                    Color(0xFF1D2433)
                }
            )
        }
    }
}

private fun getFilterIcon(iconType: IconType?): Int? {
    return when (iconType) {
        FURNITURE -> R.drawable.ic_chair_furniture
        BEAUTY -> R.drawable.ic_lipstick
        FRAGRANCES -> R.drawable.ic_fragance_perfume
        GROCERIES -> R.drawable.ic_groceries
        null -> null
    }
}

@Preview
@Composable
private fun ProductFilterSectionPreview() {
    RenEcommerceTheme(dynamicColor = false) {
        Surface {
            ProductFilterSection(
                filters = listOf(
                    ItemFilterData(
                        slug = CategorySlug.FURNITURE,
                        label = "Furniture",
                        iconType = FURNITURE
                    ),
                    ItemFilterData(
                        slug = CategorySlug.BEAUTY,
                        label = "Beauty",
                        iconType = BEAUTY
                    ),
                    ItemFilterData(
                        slug = CategorySlug.FRAGRANCES,
                        label = "Fragrances",
                        iconType = FRAGRANCES
                    ),
                    ItemFilterData(
                        slug = CategorySlug.GROCERIES,
                        label = "Groceries",
                        iconType = GROCERIES
                    ),
                    ItemFilterData(
                        slug = CategorySlug.OTHERS,
                        label = "Others",
                        iconType = null
                    ),
                ),
                selectedSlug = CategorySlug.FURNITURE,
                onFilterSelected = {}
            )
        }
    }
}
