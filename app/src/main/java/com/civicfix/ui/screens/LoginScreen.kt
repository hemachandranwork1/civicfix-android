package com.civicfix.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.civicfix.ui.theme.*
import com.civicfix.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    vm: AuthViewModel = hiltViewModel()
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    val uiState by vm.uiState.collectAsState()
    val token   by vm.token.collectAsState()

    LaunchedEffect(Unit) { vm.clearError() }

    LaunchedEffect(token) {
        if (!token.isNullOrBlank()) {
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicNavy)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.radialGradient(
                        listOf(CivicAccent.copy(0.08f), Color.Transparent),
                        radius = 600f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.linearGradient(listOf(CivicAccent, CivicGreen)),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationCity, null,
                    tint = CivicNavy, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text("Welcome back", fontSize = 26.sp,
                fontWeight = FontWeight.Bold, color = Color.White)
            Text("Sign in to CivicFix", fontSize = 14.sp, color = CivicMuted)
            Spacer(Modifier.height(36.dp))

            CivicTextField(value = email, onValueChange = { email = it },
                label = "Email", leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            CivicTextField(value = password, onValueChange = { password = it },
                label = "Password", leadingIcon = Icons.Default.Lock,
                isPassword = true, showPassword = showPass,
                onTogglePassword = { showPass = !showPass })

            Spacer(Modifier.height(8.dp))
            AnimatedVisibility(uiState.error != null) {
                Text(uiState.error ?: "", color = StatusRejected, fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 4.dp))
            }
            Spacer(Modifier.height(20.dp))

            GradientButton(
                text    = if (uiState.isLoading) "Signing in..." else "Sign In",
                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
                onClick = { vm.login(email, password) }
            )
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ", color = CivicMuted, fontSize = 14.sp)
                Text("Register", color = CivicAccent, fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { navController.navigate("register") })
            }
        }
    }
}
