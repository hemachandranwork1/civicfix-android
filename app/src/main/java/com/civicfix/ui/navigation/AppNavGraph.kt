package com.civicfix.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.civicfix.ui.screens.*
import com.civicfix.ui.viewmodel.AuthViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authVm: AuthViewModel = hiltViewModel()
    val token by authVm.token.collectAsState()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController, token = token)
        }
        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("main") {
            MainScreen(rootNav = navController)
        }
        composable("report") {
            ReportIssueScreen(navController = navController)
        }
        composable("issue/{id}") { back ->
            val id = back.arguments?.getString("id")?.toIntOrNull()
                ?: return@composable
            IssueDetailScreen(navController = navController, issueId = id)
        }
    }
}
