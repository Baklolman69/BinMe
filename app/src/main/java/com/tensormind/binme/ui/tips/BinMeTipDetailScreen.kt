package com.tensormind.binme.ui.tips

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.binme.data.remote.GroqRepository
import com.tensormind.binme.ui.theme.BinMeTheme
import kotlinx.coroutines.launch

@Composable
fun BinMeTipDetailScreen(
    tipItem: TipItem,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val repository = remember { GroqRepository() }
    val scope = rememberCoroutineScope()

    var aiGuideText by remember { mutableStateOf<String?>(null) }
    var isLoadingAi by remember { mutableStateOf(true) }

    LaunchedEffect(tipItem.id) {
        isLoadingAi = true
        scope.launch {
            val prompt = """
                Provide a comprehensive, practical, and structured sustainability guide for the tip: '${tipItem.title}' (${tipItem.description}).
                Format your response clearly with these sections:
                1) Why It Matters
                2) Step-by-Step Instructions
                3) Do's & Don'ts
                4) Local Impact for Lake Oswego curbside programs.
            """.trimIndent()

            val response = repository.sendMessage(prompt)
            aiGuideText = response
            isLoadingAi = false
        }
    }

    val greenPrimary = Color(0xFF16A34A)
    val textMain = Color(0xFF0F172A)
    val textSub = Color(0xFF64748B)

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Column {
                            Text(
                                text = "Tip Guide",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = textMain
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Groq AI Connected",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.5.sp
                                    ),
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }

                    // Circular Leaf Icon Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2F7E7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Eco",
                            tint = greenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFFAFDFB),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. HERO TIP SUMMARY CARD
            Surface(
                color = tipItem.cardBg,
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        // Category Badge Pill
                        Surface(
                            color = tipItem.badgeBg,
                            shape = CircleShape
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = tipItem.badgeIcon,
                                    contentDescription = tipItem.category,
                                    tint = tipItem.badgeText,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = tipItem.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    color = tipItem.badgeText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = tipItem.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp
                            ),
                            color = textMain
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = tipItem.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 18.sp,
                                fontSize = 12.5.sp
                            ),
                            color = textSub
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Tip Graphic Illustration
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (tipItem.illustrationType) {
                            TipIllustrationType.WATER_FAUCET_BOTTLE -> WaterFaucetBottleDetailGraphic()
                            TipIllustrationType.COMPOST_BIN_FOOD -> CompostBinDetailGraphic()
                            TipIllustrationType.STACKED_CARDBOARD -> StackedCardboardDetailGraphic()
                            TipIllustrationType.PLASTIC_BAG_PROHIBITED -> PlasticBagProhibitionDetailGraphic()
                            TipIllustrationType.REUSABLE_BOTTLE_BAG -> ReusableBottleAndBagDetailGraphic()
                        }
                    }
                }
            }

            // 2. GROQ AI GENERATED GUIDE SECTION
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = greenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Groq AI Sustainability Guide",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = textMain
                )
            }

            if (isLoadingAi) {
                // AI Loading Card
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = greenPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Generating AI Sustainability Guide...",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = textMain
                            )
                            Text(
                                text = "Consulting Groq AI for detailed waste sorting steps...",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = textSub
                            )
                        }
                    }
                }
            } else {
                // AI Generated Text Cards
                val text = aiGuideText ?: "No AI response available."

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        com.tensormind.binme.ui.chat.FormattedAiMarkdownText(text = text)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// =============================================================================
// DETAIL GRAPHIC HELPERS
// =============================================================================

@Composable
private fun WaterFaucetBottleDetailGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.5f
        drawCircle(color = Color(0xFFDCFCE7), center = Offset(centerX, centerY), radius = size.width * 0.45f)
        drawCircle(color = Color(0xFF38BDF8), center = Offset(centerX, centerY), radius = size.width * 0.25f)
    }
}

@Composable
private fun CompostBinDetailGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.5f
        drawCircle(color = Color(0xFFFEF3C7), center = Offset(centerX, centerY), radius = size.width * 0.45f)
        drawRoundRect(color = Color(0xFF15803D), topLeft = Offset(centerX - 20f, centerY - 15f), size = Size(40f, 30f), cornerRadius = CornerRadius(6f, 6f))
    }
}

@Composable
private fun StackedCardboardDetailGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.5f
        drawCircle(color = Color(0xFFE0F2FE), center = Offset(centerX, centerY), radius = size.width * 0.45f)
        drawRoundRect(color = Color(0xFFD97706), topLeft = Offset(centerX - 22f, centerY - 10f), size = Size(44f, 20f), cornerRadius = CornerRadius(4f, 4f))
    }
}

@Composable
private fun PlasticBagProhibitionDetailGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.5f
        drawCircle(color = Color(0xFFF3E8FF), center = Offset(centerX, centerY), radius = size.width * 0.45f)
        drawCircle(color = Color(0xFFE11D48), center = Offset(centerX, centerY), radius = 16f, style = Stroke(width = 3.5f))
    }
}

@Composable
private fun ReusableBottleAndBagDetailGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.5f
        drawCircle(color = Color(0xFFDCFCE7), center = Offset(centerX, centerY), radius = size.width * 0.45f)
        drawRoundRect(color = Color(0xFF047857), topLeft = Offset(centerX - 15f, centerY - 15f), size = Size(30f, 30f), cornerRadius = CornerRadius(6f, 6f))
    }
}

// =============================================================================
// PREVIEW
// =============================================================================

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun BinMeTipDetailScreenPreview() {
    BinMeTheme {
        BinMeTipDetailScreen(
            tipItem = TipItem(
                id = "1",
                title = "Rinse Before You Recycle",
                description = "Empty and rinse food and drink containers. This keeps them clean and prevents contamination in the recycling process.",
                category = "Recycling",
                badgeBg = Color(0xFFDCFCE7),
                badgeText = Color(0xFF15803D),
                badgeIcon = Icons.Default.Autorenew,
                cardBg = Color(0xFFF0FDF4),
                illustrationType = TipIllustrationType.WATER_FAUCET_BOTTLE
            )
        )
    }
}
