package com.tensormind.binme.ui.tips

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.binme.ui.theme.BinMeTheme

enum class TipIllustrationType {
    WATER_FAUCET_BOTTLE,
    COMPOST_BIN_FOOD,
    STACKED_CARDBOARD,
    PLASTIC_BAG_PROHIBITED,
    REUSABLE_BOTTLE_BAG
}

data class TipItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "Recycling", "Composting", "Trash", "Extra Tip"
    val badgeBg: Color,
    val badgeText: Color,
    val badgeIcon: ImageVector,
    val cardBg: Color,
    val illustrationType: TipIllustrationType
)

@Composable
fun BinMeTipsScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All Tips") }
    var activeSelectedTip by remember { mutableStateOf<TipItem?>(null) }

    val categories = listOf("All Tips", "Recycling", "Composting", "Trash", "Extra Tips")

    val allTips = remember {
        listOf(
            TipItem(
                id = "1",
                title = "Rinse Before You Recycle",
                description = "Empty and rinse food and drink containers. This keeps them clean and prevents contamination in the recycling process.",
                category = "Recycling",
                badgeBg = Color(0xFFDCFCE7),
                badgeText = Color(0xFF15803D),
                badgeIcon = Icons.Default.Autorenew,
                cardBg = Color(0xFFF0FDF4),
                illustrationType = TipIllustrationType.WATER_FAUCET_BOTTLE
            ),
            TipItem(
                id = "2",
                title = "Food Scraps Are Gold",
                description = "Fruit and vegetable scraps, coffee grounds, and eggshells can all be composted. They turn into nutrient-rich soil!",
                category = "Composting",
                badgeBg = Color(0xFFFEF3C7),
                badgeText = Color(0xFF854D0E),
                badgeIcon = Icons.Default.Eco,
                cardBg = Color(0xFFFFFBEB),
                illustrationType = TipIllustrationType.COMPOST_BIN_FOOD
            ),
            TipItem(
                id = "3",
                title = "Flatten Cardboard Boxes",
                description = "Flattening cardboard saves space in recycling bins and helps the material get processed more efficiently.",
                category = "Recycling",
                badgeBg = Color(0xFFE0F2FE),
                badgeText = Color(0xFF0369A1),
                badgeIcon = Icons.Default.Autorenew,
                cardBg = Color(0xFFF0F9FF),
                illustrationType = TipIllustrationType.STACKED_CARDBOARD
            ),
            TipItem(
                id = "4",
                title = "Plastic Bags Go in the Trash",
                description = "Most plastic bags and film aren't recyclable in curbside programs. Reuse them when possible or drop them at special collection locations.",
                category = "Trash",
                badgeBg = Color(0xFFF3E8FF),
                badgeText = Color(0xFF6B21A8),
                badgeIcon = Icons.Default.Delete,
                cardBg = Color(0xFFFAF5FF),
                illustrationType = TipIllustrationType.PLASTIC_BAG_PROHIBITED
            ),
            TipItem(
                id = "5",
                title = "Choose Reusable Alternatives",
                description = "Use a reusable water bottle, coffee cup, and shopping bag. It reduces waste and saves money over time!",
                category = "Extra Tip",
                badgeBg = Color(0xFFDCFCE7),
                badgeText = Color(0xFF047857),
                badgeIcon = Icons.Default.Lightbulb,
                cardBg = Color(0xFFF0FDF4),
                illustrationType = TipIllustrationType.REUSABLE_BOTTLE_BAG
            )
        )
    }

    val filteredTips = remember(selectedCategory) {
        if (selectedCategory == "All Tips") {
            allTips
        } else {
            allTips.filter {
                if (selectedCategory == "Extra Tips") {
                    it.category == "Extra Tip"
                } else {
                    it.category == selectedCategory
                }
            }
        }
    }

    if (activeSelectedTip != null) {
        BinMeTipDetailScreen(
            tipItem = activeSelectedTip!!,
            onBackClick = { activeSelectedTip = null },
            modifier = modifier
        )
    } else {
        val textMain = Color(0xFF0F172A)
        val textSub = Color(0xFF475569)
        val greenPrimary = Color(0xFF16A34A)

        Scaffold(
            bottomBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem(
                            icon = Icons.Default.Home,
                            label = "Home",
                            isSelected = false,
                            onClick = onNavigateToHome
                        )
                        BottomNavItem(
                            icon = Icons.Default.Schedule,
                            label = "History",
                            isSelected = false,
                            onClick = onNavigateToHistory
                        )
                        BottomNavItem(
                            icon = Icons.Default.Eco,
                            label = "Tips",
                            isSelected = true,
                            onClick = { }
                        )
                        BottomNavItem(
                            icon = Icons.Default.Settings,
                            label = "Settings",
                            isSelected = false,
                            onClick = onNavigateToSettings
                        )
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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // =================================================================
                // 1. TOP BRAND HEADER
                // =================================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Shield Icon with leaf inside
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .shadow(3.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(greenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(26.dp)) {
                                val path = Path().apply {
                                    moveTo(size.width * 0.15f, size.height * 0.85f)
                                    cubicTo(
                                        size.width * 0.15f, size.height * 0.3f,
                                        size.width * 0.5f, size.height * 0.15f,
                                        size.width * 0.85f, size.height * 0.15f
                                    )
                                    cubicTo(
                                        size.width * 0.85f, size.height * 0.7f,
                                        size.width * 0.5f, size.height * 0.85f,
                                        size.width * 0.15f, size.height * 0.85f
                                    )
                                    close()
                                }
                                drawPath(path, Color.White)
                                drawLine(
                                    color = Color(0xFF16A34A),
                                    start = Offset(size.width * 0.18f, size.height * 0.82f),
                                    end = Offset(size.width * 0.65f, size.height * 0.35f),
                                    strokeWidth = 3f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bin",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp
                                    ),
                                    color = textMain
                                )
                                Text(
                                    text = "ME",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp
                                    ),
                                    color = greenPrimary
                                )
                            }

                            Text(
                                text = "Small actions. A cleaner tomorrow.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Top Right Circular Leaf Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2F7E7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Tips",
                            tint = greenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // =================================================================
                // 2. HERO BANNER ("Sustainability Tips")
                // =================================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(
                            text = "Sustainability Tips",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp
                            ),
                            color = textMain
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Simple tips to help you sort smarter, reduce waste, and make a bigger impact.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 18.sp,
                                fontSize = 12.5.sp
                            ),
                            color = textSub
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Globe Illustration
                    Box(
                        modifier = Modifier
                            .size(width = 110.dp, height = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        HeroTipsGlobeGraphic()
                    }
                }

                // =================================================================
                // 3. CATEGORY FILTER CHIPS ROW
                // =================================================================
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category

                        val chipBg = if (isSelected) Color(0xFF0F5132) else Color.White
                        val chipText = if (isSelected) Color.White else Color(0xFF334155)
                        val chipBorder = if (isSelected) Color(0xFF0F5132) else Color(0xFFE2E8F0)

                        val icon = when (category) {
                            "All Tips" -> Icons.Default.Eco
                            "Recycling" -> Icons.Default.Autorenew
                            "Composting" -> Icons.Default.Park
                            "Trash" -> Icons.Default.Delete
                            else -> Icons.Default.Lightbulb
                        }

                        Surface(
                            onClick = { selectedCategory = category },
                            shape = CircleShape,
                            color = chipBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, chipBorder),
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = category,
                                    tint = chipText,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = chipText
                                )
                            }
                        }
                    }
                }

                // =================================================================
                // 4. TIPS CARDS LIST
                // =================================================================
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredTips.forEach { tip ->
                        TipCardItem(
                            tip = tip,
                            onClick = { activeSelectedTip = tip }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// =============================================================================
// TIP CARD ITEM COMPOSABLE
// =============================================================================

@Composable
private fun TipCardItem(
    tip: TipItem,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = tip.cardBg,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.3f)) {
                // Category Badge Pill
                Surface(
                    color = tip.badgeBg,
                    shape = CircleShape,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = tip.badgeIcon,
                            contentDescription = tip.category,
                            tint = tip.badgeText,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = tip.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            ),
                            color = tip.badgeText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    ),
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 16.sp,
                        fontSize = 11.5.sp
                    ),
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Illustration Graphic
            Box(
                modifier = Modifier
                    .size(width = 85.dp, height = 85.dp),
                contentAlignment = Alignment.Center
            ) {
                when (tip.illustrationType) {
                    TipIllustrationType.WATER_FAUCET_BOTTLE -> WaterFaucetBottleGraphic()
                    TipIllustrationType.COMPOST_BIN_FOOD -> CompostBinGraphic()
                    TipIllustrationType.STACKED_CARDBOARD -> StackedCardboardGraphic()
                    TipIllustrationType.PLASTIC_BAG_PROHIBITED -> PlasticBagProhibitionGraphic()
                    TipIllustrationType.REUSABLE_BOTTLE_BAG -> ReusableBottleAndBagGraphic()
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Far Right Action Circle Arrow
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Action",
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// =============================================================================
// CUSTOM VECTOR ILLUSTRATION GRAPHICS FOR TIPS
// =============================================================================

@Composable
private fun HeroTipsGlobeGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.5f
        val radius = size.height * 0.4f

        // Soft green aura
        drawCircle(
            color = Color(0xFFDCFCE7),
            center = Offset(centerX, centerY),
            radius = radius * 1.25f
        )

        // Sparkle rays
        val rayColor = Color(0xFF22C55E)
        drawLine(
            color = rayColor,
            start = Offset(centerX + radius * 0.8f, centerY - radius * 0.9f),
            end = Offset(centerX + radius * 1.15f, centerY - radius * 1.25f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = rayColor,
            start = Offset(centerX + radius * 1.1f, centerY - radius * 0.5f),
            end = Offset(centerX + radius * 1.4f, centerY - radius * 0.65f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Blue Globe
        drawCircle(
            color = Color(0xFF0284C7),
            center = Offset(centerX, centerY),
            radius = radius
        )

        // Green Continents
        val c1 = Path().apply {
            moveTo(centerX - radius * 0.5f, centerY - radius * 0.2f)
            cubicTo(
                centerX - radius * 0.1f, centerY - radius * 0.7f,
                centerX + radius * 0.4f, centerY - radius * 0.3f,
                centerX + radius * 0.1f, centerY + radius * 0.3f
            )
            close()
        }
        drawPath(c1, Color(0xFF22C55E))

        // Leaves sprouting
        val leaf = Path().apply {
            moveTo(centerX + radius * 0.8f, centerY - radius * 0.2f)
            cubicTo(
                centerX + radius * 1.3f, centerY - radius * 0.4f,
                centerX + radius * 1.2f, centerY + radius * 0.2f,
                centerX + radius * 0.8f, centerY
            )
            close()
        }
        drawPath(leaf, Color(0xFF16A34A))
    }
}

@Composable
private fun WaterFaucetBottleGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.55f

        // Light background circle aura
        drawCircle(
            color = Color(0xFFDCFCE7),
            center = Offset(centerX, centerY),
            radius = size.width * 0.42f
        )

        // Faucet Pipe Top Right
        val faucetColor = Color(0xFF0284C7)
        drawRoundRect(
            color = faucetColor,
            topLeft = Offset(centerX - 4f, centerY - 32f),
            size = Size(20f, 8f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = faucetColor,
            topLeft = Offset(centerX + 8f, centerY - 32f),
            size = Size(8f, 18f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Water Drops falling
        drawCircle(Color(0xFF38BDF8), center = Offset(centerX + 12f, centerY - 8f), radius = 3f)
        drawCircle(Color(0xFF38BDF8), center = Offset(centerX + 12f, centerY + 2f), radius = 2.5f)

        // Plastic Water Bottle
        val bottleW = 20f
        val bottleH = 38f
        drawRoundRect(
            color = Color(0xFFBAE6FD),
            topLeft = Offset(centerX + 2f, centerY + 8f),
            size = Size(bottleW, bottleH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Blue Bottle Cap
        drawRoundRect(
            color = Color(0xFF0284C7),
            topLeft = Offset(centerX + 7f, centerY + 4f),
            size = Size(10f, 5f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Sparkle stars
        drawCircle(Color(0xFF22C55E), center = Offset(centerX - 18f, centerY - 10f), radius = 2.5f)
        drawCircle(Color(0xFF22C55E), center = Offset(centerX + 24f, centerY - 12f), radius = 2.5f)
    }
}

@Composable
private fun CompostBinGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.6f

        // Light yellow background aura
        drawCircle(
            color = Color(0xFFFEF08A).copy(alpha = 0.6f),
            center = Offset(centerX, centerY - 5f),
            radius = size.width * 0.42f
        )

        // Green Compost Container Bin
        val binW = 48f
        val binH = 26f
        drawRoundRect(
            color = Color(0xFF15803D),
            topLeft = Offset(centerX - binW / 2, centerY),
            size = Size(binW, binH),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // Food Scraps piling on top (Banana peel, eggshell, leaves)
        // Banana (Yellow curve)
        drawArc(
            color = Color(0xFFFACC15),
            startAngle = 180f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(centerX - 18f, centerY - 16f),
            size = Size(20f, 14f),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // Eggshell (White/beige semi-circle)
        drawArc(
            color = Color(0xFFFEF3C7),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(centerX + 4f, centerY - 12f),
            size = Size(12f, 10f)
        )

        // Coffee grounds / Compost soil (Brown pile)
        drawCircle(Color(0xFF78350F), center = Offset(centerX, centerY - 4f), radius = 8f)

        // Green Leaf icon on bin
        drawCircle(Color.White, center = Offset(centerX, centerY + 13f), radius = 4f)
    }
}

@Composable
private fun StackedCardboardGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.55f

        // Light sky blue background aura
        drawCircle(
            color = Color(0xFFE0F2FE),
            center = Offset(centerX, centerY),
            radius = size.width * 0.42f
        )

        // Stacked Cardboard Boxes Isometric
        val boxW = 42f
        val boxH = 10f

        // Bottom Box
        drawRoundRect(
            color = Color(0xFFD97706),
            topLeft = Offset(centerX - boxW / 2, centerY + 8f),
            size = Size(boxW, boxH),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Middle Box
        drawRoundRect(
            color = Color(0xFFF59E0B),
            topLeft = Offset(centerX - boxW / 2 + 2f, centerY - 2f),
            size = Size(boxW - 4f, boxH),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Top Box
        drawRoundRect(
            color = Color(0xFFFBBF24),
            topLeft = Offset(centerX - boxW / 2 + 4f, centerY - 12f),
            size = Size(boxW - 8f, boxH),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Sparkle rays
        val rayColor = Color(0xFF0284C7)
        drawLine(
            color = rayColor,
            start = Offset(centerX - 24f, centerY - 12f),
            end = Offset(centerX - 28f, centerY - 16f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = rayColor,
            start = Offset(centerX + 24f, centerY - 12f),
            end = Offset(centerX + 28f, centerY - 16f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun PlasticBagProhibitionGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.55f

        // Light purple background aura
        drawCircle(
            color = Color(0xFFF3E8FF),
            center = Offset(centerX, centerY),
            radius = size.width * 0.42f
        )

        // Translucent Plastic Bag
        val bagW = 28f
        val bagH = 32f
        drawRoundRect(
            color = Color(0xFFCBD5E1).copy(alpha = 0.6f),
            topLeft = Offset(centerX - bagW / 2, centerY - 10f),
            size = Size(bagW, bagH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        // Bag handles
        drawArc(
            color = Color(0xFF94A3B8),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - 10f, centerY - 18f),
            size = Size(20f, 16f),
            style = Stroke(width = 3f)
        )

        // Red Prohibition Mark ⦸
        val probRadius = 14f
        val probX = centerX + 12f
        val probY = centerY + 8f

        drawCircle(
            color = Color(0xFFE11D48),
            center = Offset(probX, probY),
            radius = probRadius,
            style = Stroke(width = 3.5f)
        )
        drawLine(
            color = Color(0xFFE11D48),
            start = Offset(probX - probRadius * 0.7f, probY - probRadius * 0.7f),
            end = Offset(probX + probRadius * 0.7f, probY + probRadius * 0.7f),
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ReusableBottleAndBagGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.55f

        // Light mint background aura
        drawCircle(
            color = Color(0xFFDCFCE7),
            center = Offset(centerX, centerY),
            radius = size.width * 0.42f
        )

        // Reusable Water Bottle (Green)
        drawRoundRect(
            color = Color(0xFF047857),
            topLeft = Offset(centerX - 24f, centerY - 12f),
            size = Size(14f, 32f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Bottle cap handle
        drawCircle(
            color = Color(0xFF065F46),
            center = Offset(centerX - 17f, centerY - 15f),
            radius = 4f,
            style = Stroke(width = 2.5f)
        )

        // Canvas Tote Bag (Beige)
        val bagW = 26f
        val bagH = 30f
        drawRoundRect(
            color = Color(0xFFFEF3C7),
            topLeft = Offset(centerX - 4f, centerY - 8f),
            size = Size(bagW, bagH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Tote handle
        drawArc(
            color = Color(0xFFD97706),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX, centerY - 16f),
            size = Size(18f, 16f),
            style = Stroke(width = 2.5f)
        )
        // Leaf logo on tote bag
        drawCircle(Color(0xFF16A34A), center = Offset(centerX + 9f, centerY + 8f), radius = 4f)

        // Sparkle rays
        drawLine(
            color = Color(0xFF22C55E),
            start = Offset(centerX + 22f, centerY - 14f),
            end = Offset(centerX + 26f, centerY - 18f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 46.dp, height = 28.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFDCFCE7) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFF16A34A) else Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = if (isSelected) Color(0xFF16A34A) else Color(0xFF64748B)
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF16A34A))
            )
        }
    }
}

// =============================================================================
// COMPOSE PREVIEW
// =============================================================================

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun BinMeTipsScreenPreview() {
    BinMeTheme {
        BinMeTipsScreen()
    }
}
