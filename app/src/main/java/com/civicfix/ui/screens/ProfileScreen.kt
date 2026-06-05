package com.civicfix.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.work.WorkManager
import com.civicfix.ui.theme.*
import com.civicfix.ui.viewmodel.AuthViewModel
import com.civicfix.ui.viewmodel.IssueViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authVm:  AuthViewModel  = hiltViewModel(),
    issueVm: IssueViewModel = hiltViewModel()
) {
    val context  = LocalContext.current
    val userName by authVm.userName.collectAsState()
    val userRole by authVm.userRole.collectAsState()
    val userId   by authVm.userId.collectAsState()
    val myIssues by issueVm.myIssues.collectAsState()

    LaunchedEffect(userId) { userId?.let { issueVm.loadMyIssues(it) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicNavy)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(CivicDeepBlue, CivicNavy)))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(CivicAccent, CivicGreen))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (userName?.firstOrNull()?.uppercaseChar() ?: 'C').toString(),
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color      = CivicNavy
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(userName ?: "Citizen", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Surface(
                    color  = CivicAccent.copy(0.1f),
                    shape  = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        userRole?.replaceFirstChar { it.uppercase() } ?: "Citizen",
                        fontSize = 12.sp, color = CivicAccent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                Triple("Total",    myIssues.size,                          CivicAccent),
                Triple("Resolved", myIssues.count { it.status == "Resolved" }, CivicGreen),
                Triple("Votes",    myIssues.sumOf { it.voteCount },        Color(0xFFA855F7))
            ).forEach { (label, value, color) ->
                Surface(
                    color    = color.copy(0.08f),
                    shape    = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier            = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$value", fontSize = 24.sp,
                            fontWeight = FontWeight.Bold, color = color)
                        Text(label, fontSize = 11.sp, color = CivicMuted)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Account", fontSize = 13.sp, color = CivicMuted,
            modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(8.dp))

        Surface(
            color    = CivicDeepBlue,
            shape    = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column {
                ProfileOption(Icons.Default.Person,        "Edit Profile") {}
                HorizontalDivider(color = CivicBorder, thickness = 0.5.dp)
                ProfileOption(Icons.Default.Notifications, "Notifications") {}
                HorizontalDivider(color = CivicBorder, thickness = 0.5.dp)
                ProfileOption(Icons.Default.Info,          "About CivicFix") {}
            }
        }

        Spacer(Modifier.height(16.dp))

        Surface(
            color    = StatusRejected.copy(0.05f),
            shape    = RoundedCornerShape(16.dp),
            border   = BorderStroke(0.5.dp, StatusRejected.copy(0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable {
                    WorkManager.getInstance(context).cancelUniqueWork("status_check")
                    authVm.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
        ) {
            Row(
                modifier              = Modifier.padding(16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Logout, null,
                    tint = StatusRejected, modifier = Modifier.size(20.dp))
                Text("Logout", color = StatusRejected, fontSize = 15.sp)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun ProfileOption(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = CivicAccent, modifier = Modifier.size(20.dp))
        Text(label, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null,
            tint = CivicMuted, modifier = Modifier.size(16.dp))
    }
}
