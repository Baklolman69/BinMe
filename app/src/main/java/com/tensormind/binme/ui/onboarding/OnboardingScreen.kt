package com.tensormind.binme.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.tensormind.binme.R
import com.tensormind.binme.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val highlightWord: String,
    val subtitle: String,
    val lottieRes: Int,
    val gradientColors: List<Color>
)

@Composable
fun OnboardingPager(onFinished: () -> Unit = {}) {
    val pages = listOf(
        OnboardingPage(
            title = "Instant AI\nWaste Scanner",
            highlightWord = "Scanner",
            subtitle = "Snap a photo or scan a barcode to know instantly if an item is Recyclable, Compostable, or Trash with verified AI.",
            lottieRes = R.raw.scanning,
            gradientColors = listOf(Color(0xFFE8F5E9), Color.White)
        ),
        OnboardingPage(
            title = "Verified Local\nDisposal Guidance",
            highlightWord = "Local",
            subtitle = "Get official Lake Oswego & Clackamas County disposal rules and nearby OpenStreetMap drop-off stations.",
            lottieRes = R.raw.location,
            gradientColors = listOf(Color(0xFFE3F2FD), Color.White)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(
                page = pages[page]
            )
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            AnimatedPageIndicator(
                currentPage = pagerState.currentPage,
                pageCount = pages.size
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip button
                if (pagerState.currentPage < pages.lastIndex) {
                    TextButton(onClick = onFinished) {
                        Text(
                            "Skip",
                            color = GrayDark,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(64.dp))
                }

                // Next / Get Started button
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.lastIndex) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinished()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlackPrimary),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.lastIndex) "Let's Go! 🚀" else "Next",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = page.gradientColors)
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = buildAnnotatedString {
                    val highlight = page.highlightWord
                    val fullText = page.title
                    val index = fullText.indexOf(highlight)

                    if (index != -1) {
                        append(fullText.substring(0, index))
                        withStyle(style = SpanStyle(color = GreenPrimary, fontWeight = FontWeight.ExtraBold)) {
                            append(highlight)
                        }
                        append(fullText.substring(index + highlight.length))
                    } else {
                        append(fullText)
                    }
                },
                style = MaterialTheme.typography.displayLarge.copy(
                    lineHeight = 44.sp,
                    fontSize = 34.sp
                ),
                color = BlackPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = GrayDark,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(page.lottieRes)
            )
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun AnimatedPageIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 8.dp,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                label = "indicator_width"
            )
            val color by animateColorAsState(
                targetValue = if (isSelected) GreenPrimary else GrayMedium,
                animationSpec = tween(300),
                label = "indicator_color"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
