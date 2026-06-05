@file:OptIn(ExperimentalMaterial3Api::class)

package com.civicfix.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.civicfix.domain.model.TimelineEntry
import com.civicfix.ui.components.*
import com.civicfix.ui.theme.*
import com.civicfix.ui.viewmodel.IssueViewModel
import com.civicfix.util.Constants
import com.civicfix.util.PdfExporter
import com.civicfix.util.QrGenerator

@Composable
fun IssueDetailScreen(
    navController: NavController,
    issueId: Int,
    vm: IssueViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val issue by vm.selectedIssue.collectAsState()
    val timeline by vm.timeline.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    var comment by remember { mutableStateOf("") }
    var showQr by remember { mutableStateOf(false) }

    LaunchedEffect(issueId) { vm.loadIssueDetail(issueId) }

    Scaffold(
        containerColor = CivicNavy,
        topBar = {
            TopAppBar(
                title = { Text("Issue Detail", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { issue?.let { PdfExporter.export(context, it) } }) {
                        Icon(Icons.Default.PictureAsPdf, null, tint = CivicAccent)
                    }
                    IconButton(onClick = { showQr = true }) {
                        Icon(Icons.Default.QrCode, null, tint = CivicAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CivicDeepBlue, titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isLoading && issue == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CivicAccent)
            }
            return@Scaffold
        }

        val iss = issue ?: return@Scaffold

        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            iss.imageUrl?.let {
                AsyncImage(model = "${Constants.BASE_URL}$it", contentDescription = "Issue image",
                    modifier = Modifier.fillMaxWidth().height(220.dp), contentScale = ContentScale.Crop)
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        CategoryChip(iss.category)
                        Spacer(Modifier.height(6.dp))
                        Text(iss.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    StatusChip(iss.status)
                }

                Text(iss.description, fontSize = 14.sp, color = CivicMuted, lineHeight = 22.sp)

                Surface(color = CivicDeepBlue, shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(0.5.dp, CivicBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetaRow(Icons.Default.Person, "Reporter", iss.reporterName ?: "Unknown")
                        MetaRow(Icons.Default.LocationOn, "Location", iss.address ?: "${iss.latitude}, ${iss.longitude}")
                        MetaRow(Icons.Default.Schedule, "Reported", iss.createdAt.take(10))
                        MetaRow(Icons.Default.ThumbUp, "Votes", "${iss.voteCount}")
                        MetaRow(Icons.Default.PriorityHigh, "Priority", iss.priority)
                    }
                }

                Button(onClick = { vm.voteIssue(iss.id) }, modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CivicAccent.copy(0.1f), contentColor = CivicAccent),
                    border = BorderStroke(1.dp, CivicAccent.copy(0.3f))) {
                    Icon(Icons.Default.ThumbUp, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Upvote this issue (${iss.voteCount})")
                }

                if (timeline.isNotEmpty()) {
                    SectionHeader("Status Timeline")
                    TimelineView(timeline)
                }

                SectionHeader("Comments")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = comment, onValueChange = { comment = it },
                        placeholder = { Text("Add a comment...", color = CivicMuted) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CivicAccent, unfocusedBorderColor = CivicBorder,
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedContainerColor = CivicDeepBlue, unfocusedContainerColor = CivicDeepBlue
                        ))
                    IconButton(onClick = { if (comment.isNotBlank()) { vm.addComment(iss.id, comment); comment = "" } },
                        modifier = Modifier.clip(CircleShape).background(CivicAccent).size(48.dp)) {
                        Icon(Icons.Default.Send, null, tint = CivicNavy)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        if (showQr) {
            AlertDialog(
                onDismissRequest = { showQr = false },
                containerColor = CivicDeepBlue,
                title = { Text("Share Issue", color = Color.White) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val bmp = QrGenerator.generate("civicfix://issue/${iss.id}")
                        if (bmp != null) {
                            Image(bitmap = bmp.asImageBitmap(), contentDescription = "QR Code",
                                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(8.dp)))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Issue #${iss.id}", color = CivicMuted, fontSize = 13.sp)
                    }
                },
                confirmButton = { TextButton(onClick = { showQr = false }) { Text("Close", color = CivicAccent) } }
            )
        }
    }
}

@Composable
fun MetaRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = CivicAccent, modifier = Modifier.size(16.dp))
        Text("$label:", fontSize = 13.sp, color = CivicMuted)
        Text(value, fontSize = 13.sp, color = Color.White, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TimelineView(entries: List<TimelineEntry>) {
    val statusColors = mapOf(
        "Reported" to StatusReported, "Under Review" to StatusReview,
        "In Progress" to StatusProgress, "Resolved" to StatusResolved, "Rejected" to StatusRejected
    )
    Surface(color = CivicDeepBlue, shape = RoundedCornerShape(14.dp),
        border = BorderStroke(0.5.dp, CivicBorder), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            entries.forEachIndexed { index, entry ->
                val color = statusColors[entry.newStatus] ?: CivicAccent
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                        if (index < entries.lastIndex) {
                            Box(modifier = Modifier.width(2.dp).height(36.dp)
                                .background(Brush.verticalGradient(listOf(color.copy(0.4f), Color.Transparent))))
                        }
                    }
                    Column(modifier = Modifier.padding(bottom = if (index < entries.lastIndex) 4.dp else 0.dp)) {
                        Text(entry.newStatus, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = color)
                        Text("${entry.changedByName} · ${entry.createdAt.take(10)}", fontSize = 11.sp, color = CivicMuted)
                        entry.note?.let { Text(it, fontSize = 12.sp, color = CivicMuted.copy(0.7f)) }
                    }
                }
            }
        }
    }
}
