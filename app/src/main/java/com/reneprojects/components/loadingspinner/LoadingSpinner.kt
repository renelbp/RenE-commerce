package com.reneprojects.components.loadingspinner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


/**
 * Shows the visual indicator for pull-to--refresh gesture.
 *
 * The spinner reacts to [PullToRefreshState.distanceFraction]:
 * Displays partial progress while the user is dragging and witches to an indeterminate indicator while refreshing is active
 */
@Composable
internal fun LoadingSpinner(
    state: PullToRefreshState,
    isRefreshing: Boolean
) {
    val progress = state.distanceFraction.coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .graphicsLayer {
                translationY = state.distanceFraction * 40.dp.toPx()
                alpha = progress
                scaleX = progress
                scaleY = progress
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            isRefreshing -> CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            )

            progress > 0f -> CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(28.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            )
        }
    }
}