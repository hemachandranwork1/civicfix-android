package com.civicfix.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civicfix.ui.theme.*

@Composable
fun StatusChip(status: String) {
    val (bg, text) = when (status) {
        "Reported"     -> Color(0xFF00D4FF).copy(0.1f) to StatusReported
        "Under Review" -> Color(0xFFFFB347).copy(0.1f) to StatusReview
        "In Progress"  -> Color(0xFFA855F7).copy(0.1f) to StatusProgress
        "Resolved"     -> Color(0xFF00FF88).copy(0.1f) to StatusResolved
        "Rejected"     -> Color(0xFFFF6B6B).copy(0.1f) to StatusRejected
        else           -> CivicBlue to CivicMuted
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.border(0.5.dp, text.copy(0.3f), RoundedCornerShape(20.dp))
    ) {
        Text(
            text = status,
            color = text,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
