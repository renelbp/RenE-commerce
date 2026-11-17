package com.reneprojects.components.discountbadge.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reneprojects.R
import com.reneprojects.ui.theme.RenEcommerceTheme

/**
 * Pill-style badge that shows a product discount percentage.
 *
 * Uses the same colors and typography as the selected filter chip.
 *
 * @param discountPercentage Rounded discount to display; callers hide the badge when there is no
 * discount.
 * @param modifier Applied to the badge root.
 */
@Composable
internal fun DiscountBadge(
    discountPercentage: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Text(
            text = stringResource(
                R.string.ren_ecomme_discount_badge_label,
                discountPercentage
            ),
            modifier = Modifier.padding(
                vertical = 4.dp,
                horizontal = 8.dp
            ),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun DiscountBadgePreview() {
    RenEcommerceTheme(dynamicColor = false) {
        Surface {
            DiscountBadge(discountPercentage = 12)
        }
    }
}
