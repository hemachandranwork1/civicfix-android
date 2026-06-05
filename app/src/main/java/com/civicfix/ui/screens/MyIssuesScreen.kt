package com.civicfix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

private const val BASE = "http://10.0.2.2:3000"

@Composable
fun MyIssuesScreen(
    navController: NavController,
    issueVm: IssueViewModel = hiltViewModel(),
    authVm: AuthViewModel   = hiltViewModel()
) {
    val userId   by authVm.userId.collectAsState()
    val myIssues by issueVm.myIssues.collectAsState()

    LaunchedEffect(userId) {
        userId?.let {
            issueVm.loadMyIssues(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicNavy)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CivicDeepBlue)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text("My Reports", fontSize = 22.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Text("${myIssues.size} issues reported by you",
                    fontSize = 13.sp, color = CivicMuted)
            }
        }

        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                "Reported"  to StatusReported,
                "Progress"  to StatusProgress,
                "Resolved"  to StatusResolved
            ).forEach { (label, color) ->
                val count = myIssues.count { it.status.contains(label, ignoreCase = true) }
                Surface(
                    color    = color.copy(0.08f),
                    shape    = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier            = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$count", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = color)
                        Text(label, fontSize = 11.sp, color = CivicMuted)
                    }
                }
            }
        }

        if (myIssues.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No issues reported yet", color = CivicMuted, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap + to report your first issue",
                        color = CivicMuted.copy(0.6f), fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myIssues, key = { it.id }) { issue ->
                    IssueCard(
                        issue   = issue,
                        onClick = { navController.navigate("issue/${issue.id}") },
                        onVote  = { issueVm.voteIssue(issue.id) },
                        baseUrl = BASE
                    )
                }
            }
        }
    }
}
