package com.reneprojects.components.productfilter.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reneprojects.R
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
    selectedIconType: IconType,
    onFilterSelected: (IconType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filters, key = { it.iconType }) { filter ->
            ProductFilterChip(
                filter = filter,
                selected = filter.iconType == selectedIconType,
                onClick = { onFilterSelected(filter.iconType) }
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
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = if (selected) {
            Color(0xFFFF1F1F)
        } else {
            Color.White
        },
        border = if (selected) {
            null
        } else {
            BorderStroke(
                width = 1.dp,
                color = Color(0xFFE1E5EA)
            )
        },
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 18.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = getFilterIcon(filter.iconType)),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (selected) {
                    Color.White
                } else {
                    Color(0xFF1D2433)
                }
            )

            Text(
                text = filter.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = if (selected) {
                    Color.White
                } else {
                    Color(0xFF1D2433)
                }
            )
        }
    }
}

@Composable
private fun getFilterIcon(iconType: IconType): Int {
    return when (iconType) {
        FURNITURE -> R.drawable.ic_chair_furniture
        BEAUTY -> R.drawable.ic_lipstick
        FRAGRANCES -> R.drawable.ic_fragance_perfume
        GROCERIES -> R.drawable.ic_groceries
    }
}

@Preview
@Composable
private fun ProductFilterSectionPreview() {
    RenEcommerceTheme(dynamicColor = false) {
        Surface {
            ProductFilterSection(
                filters = listOf(
                    ItemFilterData(label = "Furniture", iconType = FURNITURE),
                    ItemFilterData(label = "Beauty", iconType = BEAUTY),
                    ItemFilterData(label = "Fragrances", iconType = FRAGRANCES),
                    ItemFilterData(label = "Groceries", iconType = GROCERIES),
                ),
                selectedIconType = FURNITURE,
                onFilterSelected = {}
            )
        }
    }
}
