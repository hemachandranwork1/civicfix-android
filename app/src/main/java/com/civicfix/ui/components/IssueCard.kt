package com.civicfix.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.civicfix.domain.model.Issue
import com.civicfix.ui.theme.*

@Composable
fun IssueCard(
    issue: Issue,
    onClick: () -> Unit,
    onVote: () -> Unit,
    baseUrl: String,
    modifier: Modifier = Modifier
) {
    var voteScale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = voteScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "vote_scale",
        finishedListener = { voteScale = 1f }
    )

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CivicDeepBlue,
        border = BorderStroke(0.5.dp, CivicBorder),
        tonalElevation = 0.dp
    ) {
        Column {
            issue.imageUrl?.let { url ->
                AsyncImage(
                    model = "$baseUrl$url",
                    contentDescription = "Issue image",
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(Modifier.padding(14.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        CategoryChip(issue.category)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            issue.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    StatusChip(issue.status)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    issue.description,
                    fontSize = 13.sp,
                    color = CivicMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        issue.address ?: "Location not set",
                        fontSize = 11.sp,
                        color = CivicMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { voteScale = 1.3f; onVote() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = "Vote",
                            tint = CivicAccent,
                            modifier = Modifier.size(14.dp).scale(animatedScale)
                        )
                        Text("${issue.voteCount}", fontSize = 12.sp, color = CivicAccent)
                    }
                }
            }
        }
    }
}
