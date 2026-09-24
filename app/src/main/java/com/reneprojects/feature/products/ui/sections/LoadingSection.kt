package com.reneprojects.feature.products.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.reneprojects.utils.extension.shimmerEffect

@Composable
internal fun LoadingSection() {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        repeat(3) {
            LoadingHeader()
            Spacer(modifier = Modifier.height(12.dp))
            LoadingCardsRow()
        }
    }
}

@Composable
private fun LoadingHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .shimmerEffect(CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(24.dp)
                    .shimmerEffect(RoundedCornerShape(6.dp))
            )
        }
        Box(
            modifier = Modifier
                .width(68.dp)
                .height(20.dp)
                .shimmerEffect(RoundedCornerShape(6.dp))
        )
    }
}

@Composable
private fun LoadingCardsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(2) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
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
                        .fillMaxWidth(0.45f)
                        .height(16.dp)
                        .shimmerEffect(RoundedCornerShape(6.dp))
                )
            }
        }
    }
}