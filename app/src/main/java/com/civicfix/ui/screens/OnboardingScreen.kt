package com.civicfix.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.civicfix.R
import com.civicfix.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardPage(
    val lottieRes: Int,
    val title: String,
    val subtitle: String,
    val accent: Color
)

val onboardPages = listOf(
    OnboardPage(R.raw.splash_lottie, "Report Issues",
        "Spot a pothole, broken streetlight or garbage overflow? Report it in seconds with photo and location.", CivicAccent),
    OnboardPage(R.raw.splash_lottie, "Track Status",
        "Follow your complaint from Reported to Resolved. Get notified at every update.", CivicGreen),
    OnboardPage(R.raw.splash_lottie, "Build Better Cities",
        "Upvote issues that matter. Help authorities prioritize what the community needs most.", Color(0xFFA855F7))
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState { onboardPages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicNavy)
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            OnboardPage(onboardPages[page])
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(onboardPages.size) { i ->
                    val isSelected = pagerState.currentPage == i
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 6.dp,
                        animationSpec = tween(300),
                        label = "dot_width"
                    )
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) onboardPages[i].accent else CivicMuted.copy(0.3f)
                            )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            val isLast = pagerState.currentPage == onboardPages.lastIndex

            Button(
                onClick = {
                    if (isLast) {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(CivicAccent, CivicGreen)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isLast) "Get Started" else "Next",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = CivicNavy
                    )
                }
            }

            if (!isLast) {
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }) {
                    Text("Skip", color = CivicMuted, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun OnboardPage(page: OnboardPage) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(page.lottieRes))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(
                    Brush.radialGradient(listOf(page.accent.copy(0.08f), Color.Transparent))
                ),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(composition, { progress }, modifier = Modifier.size(240.dp))
        }

        Spacer(Modifier.height(32.dp))

        Text(
            page.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            page.subtitle,
            fontSize = 15.sp,
            color = CivicMuted,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(Modifier.height(120.dp))
    }
}
