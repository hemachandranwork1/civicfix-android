package com.civicfix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.civicfix.ui.components.BottomNavBar
import com.civicfix.ui.theme.*

@Composable
fun MainScreen(rootNav: NavController) {
    val innerNav = rememberNavController()
    val backstackEntry by innerNav.currentBackStackEntryAsState()
    val currentRoute = backstackEntry?.destination?.route ?: "home"

    Scaffold(
        containerColor = CivicNavy,
        bottomBar = {
            BottomNavBar(currentRoute) { route ->
                innerNav.navigate(route) {
                    popUpTo(innerNav.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState    = true
                }
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(CivicAccent, CivicGreen)))
                    .clickable { rootNav.navigate("report") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Report Issue",
                    tint     = CivicNavy,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        NavHost(
            navController  = innerNav,
            startDestination = "home",
            modifier       = Modifier.padding(padding)
        ) {
            composable("home")      { HomeScreen(rootNav) }
            composable("map")       { MapScreen() }
            composable("my_issues") { MyIssuesScreen(rootNav) }
            composable("profile")   { ProfileScreen(rootNav) }
        }
    }
}
