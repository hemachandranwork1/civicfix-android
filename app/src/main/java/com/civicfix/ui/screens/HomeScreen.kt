package com.civicfix.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.civicfix.ui.components.*
import com.civicfix.ui.theme.*
import com.civicfix.ui.viewmodel.AuthViewModel
import com.civicfix.ui.viewmodel.IssueViewModel
import com.civicfix.util.Constants
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

private val homeCategories = listOf("All","Pothole","Garbage","Streetlight","Water Leak","Other")
private val homeStatuses   = listOf("All","Reported","Under Review","In Progress","Resolved")

@Composable
fun HomeScreen(
    navController: NavController,
    issueVm: IssueViewModel = hiltViewModel(),
    authVm:  AuthViewModel  = hiltViewModel()
) {
    val issues       by issueVm.filteredIssues.collectAsState()
    val isRefreshing by issueVm.isRefreshing.collectAsState()
    val isLoading    by issueVm.isLoading.collectAsState()
    val userName     by authVm.userName.collectAsState()
    val searchQuery  by issueVm.searchQuery.collectAsState()
    val selCat       by issueVm.selectedCategory.collectAsState()
    val selStat      by issueVm.selectedStatus.collectAsState()
    val swipeState   = rememberSwipeRefreshState(isRefreshing)

    Box(Modifier.fillMaxSize().background(CivicNavy)) {
        SwipeRefresh(
            state     = swipeState,
            onRefresh = { issueVm.refresh() },
            modifier  = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hero
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(listOf(CivicDeepBlue, CivicNavy))
                            )
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        Column {
                            Text("Good ${greeting()},", fontSize = 14.sp, color = CivicMuted)
                            Text(
                                userName ?: "Citizen",
                                fontSize   = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${issues.size} issues in your area",
                                fontSize = 13.sp,
                                color    = CivicAccent
                            )
                        }
                    }
                }

                // Search
                item {
                    OutlinedTextField(
                        value         = searchQuery,
                        onValueChange = { issueVm.searchQuery.value = it },
                        placeholder   = { Text("Search issues...", color = CivicMuted) },
                        leadingIcon   = {
                            Icon(Icons.Default.Search, null,
                                tint = CivicMuted, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = if (searchQuery.isNotBlank()) {{
                            IconButton(onClick = { issueVm.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, null,
                                    tint = CivicMuted, modifier = Modifier.size(16.dp))
                            }
                        }} else null,
                        modifier   = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape      = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors     = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = CivicAccent,
                            unfocusedBorderColor    = CivicBorder,
                            focusedTextColor        = Color.White,
                            unfocusedTextColor      = Color.White,
                            focusedContainerColor   = CivicDeepBlue,
                            unfocusedContainerColor = CivicDeepBlue
                        )
                    )
                }

                // Category chips
                item {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(homeCategories) { cat ->
                            val sel = (selCat == null && cat == "All") || selCat == cat
                            FilterChip(
                                selected = sel,
                                onClick  = {
                                    issueVm.selectedCategory.value =
                                        if (cat == "All") null else cat
                                },
                                label  = { Text(cat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CivicAccent.copy(0.15f),
                                    selectedLabelColor     = CivicAccent,
                                    containerColor         = CivicDeepBlue,
                                    labelColor             = CivicMuted
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled             = true,
                                    selected            = sel,
                                    selectedBorderColor = CivicAccent.copy(0.4f),
                                    borderColor         = CivicBorder
                                )
                            )
                        }
                    }
                }

                // Status chips
                item {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(homeStatuses) { stat ->
                            val sel = (selStat == null && stat == "All") || selStat == stat
                            FilterChip(
                                selected = sel,
                                onClick  = {
                                    issueVm.selectedStatus.value =
                                        if (stat == "All") null else stat
                                },
                                label  = { Text(stat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CivicGreen.copy(0.12f),
                                    selectedLabelColor     = CivicGreen,
                                    containerColor         = CivicDeepBlue,
                                    labelColor             = CivicMuted
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled             = true,
                                    selected            = sel,
                                    selectedBorderColor = CivicGreen.copy(0.4f),
                                    borderColor         = CivicBorder
                                )
                            )
                        }
                    }
                }

                // Shimmer while loading
                if (isLoading && issues.isEmpty()) {
                    item {
                        Column(
                            modifier            = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(4) { ShimmerBox() }
                        }
                    }
                } else if (issues.isEmpty()) {
                    item {
                        Column(
                            modifier            = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.SearchOff, null,
                                tint = CivicMuted, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("No issues found", color = CivicMuted, fontSize = 15.sp)
                        }
                    }
                } else {
                    items(issues, key = { it.id }) { issue ->
                        IssueCard(
                            issue    = issue,
                            onClick  = { navController.navigate("issue/${issue.id}") },
                            onVote   = { issueVm.voteIssue(issue.id) },
                            baseUrl  = Constants.BASE_URL,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun greeting(): String =
    when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
        in 5..11  -> "morning"
        in 12..16 -> "afternoon"
        else      -> "evening"
    }
