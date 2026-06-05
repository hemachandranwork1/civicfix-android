package com.civicfix.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.civicfix.R
import com.civicfix.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, token: String?) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.splash_lottie)
    )
    val progress by animateLottieCompositionAsState(composition, iterations = 1)

    var textAlpha by remember { mutableFloatStateOf(0f) }
    val animAlpha by animateFloatAsState(
        targetValue   = textAlpha,
        animationSpec = tween(800),
        label         = "text_alpha"
    )

    LaunchedEffect(Unit) {
        delay(300)
        textAlpha = 1f
        delay(2200)
        if (!token.isNullOrBlank()) {
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("onboarding") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0F2040), CivicNavy),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(
                    Brush.radialGradient(
                        listOf(CivicAccent.copy(0.12f), Color.Transparent)
                    )
                )
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimation(
                composition = composition,
                progress    = { progress },
                modifier    = Modifier.size(180.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "CivicFix",
                fontSize   = 32.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White,
                modifier   = Modifier.alpha(animAlpha)
            )
            Text(
                "Report. Track. Resolve.",
                fontSize = 14.sp,
                color    = CivicMuted,
                modifier = Modifier.alpha(animAlpha)
            )
        }
    }
}
