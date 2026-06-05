package com.civicfix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civicfix.ui.theme.*

@Composable
fun CategoryChip(category: String) {
    val (bg, text) = when (category) {
        "Pothole"     -> CatPothole.copy(0.15f)    to CatPothole
        "Garbage"     -> CatGarbage.copy(0.15f)    to CatGarbage
        "Streetlight" -> CatStreetlight.copy(0.15f) to CatStreetlight
        "Water Leak"  -> CatWater.copy(0.15f)      to CatWater
        else          -> CatOther.copy(0.15f)       to CatOther
    }
    Text(
        text = category, fontSize = 10.sp, color = text,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
