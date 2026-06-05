package com.civicfix.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.civicfix.ui.theme.*

data class NavItem(val route: String, val label: String, val icon: ImageVector)

val navItems = listOf(
    NavItem("home",      "Home",   Icons.Default.Home),
    NavItem("map",       "Map",    Icons.Default.Map),
    NavItem("my_issues", "Mine",   Icons.Default.List),
    NavItem("profile",   "Profile",Icons.Default.Person)
)

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = CivicDeepBlue, tonalElevation = 0.dp) {
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(item.icon, contentDescription = item.label,
                        modifier = Modifier.size(22.dp))
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = CivicAccent,
                    selectedTextColor   = CivicAccent,
                    unselectedIconColor = CivicMuted,
                    unselectedTextColor = CivicMuted,
                    indicatorColor      = CivicAccent.copy(alpha = 0.1f)
                )
            )
        }
    }
}
