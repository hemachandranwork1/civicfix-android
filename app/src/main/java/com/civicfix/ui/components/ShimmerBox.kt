package com.civicfix.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerBox(modifier: Modifier = Modifier, height: Dp = 80.dp, cornerRadius: Dp = 16.dp) {
    val shimmerColors = listOf(
        Color(0xFF0F2040), Color(0xFF162A52), Color(0xFF0F2040)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer_anim"
    )
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(translateAnim - 500f, 0f),
                    end = Offset(translateAnim, 0f)
                )
            )
    )
}

@Composable
fun ShimmerList(count: Int = 5) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(count) { ShimmerBox() }
    }
}
