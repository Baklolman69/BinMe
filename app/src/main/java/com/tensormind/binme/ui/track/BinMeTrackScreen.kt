package com.tensormind.binme.ui.track

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.binme.ui.theme.BinMeTheme

@Composable
fun BinMeTrackScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToTips: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(2) }

    val textMain = Color(0xFF0F172A)
    val textSub = Color(0xFF475569)
    val greenPrimary = Color(0xFF16A34A)
    val greenDark = Color(0xFF15803D)
    val bgPage = Color(0xFFFAFDFB)

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
                        isSelected = activeTab == 0,
                        onClick = {
                            activeTab = 0
                            onNavigateToHome()
                        }
                    )
                    BottomNavItem(
                        icon = Icons.Default.Schedule,
                        label = "History",
                        isSelected = activeTab == 1,
                        onClick = {
                            activeTab = 1
                            onNavigateToHistory()
                        }
                    )
                    BottomNavItem(
                        icon = Icons.Default.Eco,
                        label = "Track",
                        isSelected = activeTab == 2,
                        onClick = { activeTab = 2 }
                    )
                    BottomNavItem(
                        icon = Icons.Default.Lightbulb,
                        label = "Tips",
                        isSelected = activeTab == 3,
                        onClick = {
                            activeTab = 3
                            onNavigateToTips()
                        }
                    )
                    BottomNavItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isSelected = activeTab == 4,
                        onClick = {
                            activeTab = 4
                            onNavigateToSettings()
                        }
                    )
                }
            }
        },
        containerColor = bgPage,
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
                            text = "Small habits. A greener tomorrow.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // User Avatar Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Top Right Circular Leaf Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
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

            // =================================================================
            // 2. GREETING HERO BANNER
            // =================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1.2f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hey Alex!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp
                            ),
                            color = textMain
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🍃", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "You're making a real impact. Track your habits, lower your carbon footprint, and earn rewards!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 18.sp,
                            fontSize = 12.5.sp
                        ),
                        color = textSub
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Happy Earth Globe Illustration with note
                Box(
                    modifier = Modifier.size(width = 120.dp, height = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HappyEarthTrackGraphic()
                }
            }

            // =================================================================
            // 3. MEASURABLE IMPACT & TRANSPARENT TRACKER
            // =================================================================
            var showCalculationDialog by remember { mutableStateOf(false) }

            if (showCalculationDialog) {
                AlertDialog(
                    onDismissRequest = { showCalculationDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showCalculationDialog = false }) {
                            Text("Got it", fontWeight = FontWeight.Bold, color = greenPrimary)
                        }
                    },
                    title = {
                        Text(
                            text = "How Impact Is Calculated",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Our calculations use official EPA Waste Reduction Model (WARM) standards:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp)
                            )
                            Text(
                                text = "• 1 PET Plastic Bottle recycled = 0.08 kg CO₂e saved\n• 1 Aluminum Can recycled = 0.14 kg CO₂e saved\n• 1 kg Paper/Cardboard recycled = 1.50 kg CO₂e saved\n• 1 kg Food Scraps composted = 0.45 kg CO₂e saved",
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp, fontSize = 11.5.sp),
                                color = textSub
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Source: US EPA WARM Version 15 & Oregon DEQ Environmental Data.",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = greenDark
                            )
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(greenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Your Measurable Impact",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = textMain
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Calculation info",
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { showCalculationDialog = true }
                                    )
                                }
                                Text(
                                    text = "Transparent calculation methodology",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = CircleShape,
                            modifier = Modifier.clickable { showCalculationDialog = true }
                        ) {
                            Text(
                                text = "Source Data ℹ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = greenDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Measurable Concrete Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "18 Items",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                    color = greenDark
                                )
                                Text(
                                    text = "Correctly sorted",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = textSub
                                )
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "4.2 kg",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                    color = Color(0xFF0369A1)
                                )
                                Text(
                                    text = "Landfill diverted",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = textSub
                                )
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "14 kWh",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                    color = Color(0xFFD97706)
                                )
                                Text(
                                    text = "Energy saved",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = textSub
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Personalized AI Habit Recommendation
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "Personalized Recommendation",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = Color(0xFF1E40AF)
                                )
                                Text(
                                    text = "You frequently scan plastic water bottles. Carrying a reusable stainless steel bottle could eliminate over 120 single-use bottles per year!",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        lineHeight = 15.sp,
                                        fontSize = 11.sp
                                    ),
                                    color = Color(0xFF1E3A8A)
                                )
                            }
                        }
                    }
                }
            }

            // =================================================================
            // 4. YOUR HABITS SECTION
            // =================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Habits",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = textMain
                    )
                    Text(
                        text = "See how your daily choices add up.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = textSub
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Details",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        color = greenDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = greenDark,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            // 3 Habit Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Commute
                HabitCard(
                    title = "Commute",
                    metric = "1.8 kg CO₂e",
                    subtitle = "Bus • 12 miles",
                    cardBg = Color(0xFFEFF6FF),
                    iconBg = Color(0xFF3B82F6),
                    icon = Icons.Default.DirectionsBus,
                    modifier = Modifier.weight(1f)
                )

                // Food
                HabitCard(
                    title = "Food",
                    metric = "1.6 kg CO₂e",
                    subtitle = "Mostly home-cooked",
                    cardBg = Color(0xFFFFFBEB),
                    iconBg = Color(0xFFEAB308),
                    icon = Icons.Default.Restaurant,
                    modifier = Modifier.weight(1f)
                )

                // Energy
                HabitCard(
                    title = "Energy",
                    metric = "1.4 kg CO₂e",
                    subtitle = "Electricity • 6 hrs",
                    cardBg = Color(0xFFF3E8FF),
                    iconBg = Color(0xFFA855F7),
                    icon = Icons.Default.Bolt,
                    modifier = Modifier.weight(1f)
                )
            }

            // =================================================================
            // 5. QUICK SWAPS FOR A BIGGER IMPACT SECTION
            // =================================================================
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(greenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Quick Swaps for a Bigger Impact",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = textMain
                                )
                                Text(
                                    text = "Small changes. Big difference.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = textSub
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "See all swaps",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = greenDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = greenDark,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Swap Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SwapCard(
                            icon = Icons.Default.DirectionsCar,
                            iconBg = Color(0xFFDCFCE7),
                            iconTint = greenDark,
                            text = "Try biking or walking instead of short car trips.",
                            savings = "-0.5 kg CO₂e / day",
                            modifier = Modifier.weight(1f)
                        )
                        SwapCard(
                            icon = Icons.Default.ShoppingBag,
                            iconBg = Color(0xFFFEF3C7),
                            iconTint = Color(0xFF854D0E),
                            text = "Choose plant-based meals 2–3x per week.",
                            savings = "-0.4 kg CO₂e / day",
                            modifier = Modifier.weight(1f)
                        )
                        SwapCard(
                            icon = Icons.Default.Lightbulb,
                            iconBg = Color(0xFFFEF08A),
                            iconTint = Color(0xFF854D0E),
                            text = "Turn off lights & unplug devices when not in use.",
                            savings = "-0.3 kg CO₂e / day",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // =================================================================
            // 6. DUAL STREAK & BADGES ROW
            // =================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Current Streak Card
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEDD5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = Color(0xFFEA580C),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Current Streak",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = textSub
                                )
                                Text(
                                    text = "5 days",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = textMain
                                )
                            }
                        }

                        Text(
                            text = "Keep it going!",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = textSub
                        )

                        // 7 Day Circles Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            days.forEachIndexed { index, day ->
                                val isDone = index < 4
                                val isToday = index == 4

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isDone -> greenPrimary
                                                    isToday -> Color(0xFFF59E0B)
                                                    else -> Color(0xFFE2E8F0)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        } else if (isToday) {
                                            Text(
                                                text = "🔥",
                                                fontSize = 8.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                        color = textSub
                                    )
                                }
                            }
                        }
                    }
                }

                // Badges Card
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Badges",
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Badges",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = textMain
                                    )
                                    Text(
                                        text = "3/12 earned",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = textSub
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "View Badges",
                                tint = textSub,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        // 4 Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BadgeIconItem(
                                icon = Icons.Default.Eco,
                                label = "Week\nWarrior",
                                bg = Color(0xFFDCFCE7),
                                tint = greenDark
                            )
                            BadgeIconItem(
                                icon = Icons.Default.DirectionsRun,
                                label = "Active\nExplorer",
                                bg = Color(0xFFFEF3C7),
                                tint = Color(0xFFD97706)
                            )
                            BadgeIconItem(
                                icon = Icons.Default.Park,
                                label = "Food\nChanger",
                                bg = Color(0xFFDCFCE7),
                                tint = Color(0xFF047857)
                            )
                            BadgeIconItem(
                                icon = Icons.Default.Lock,
                                label = "",
                                bg = Color(0xFFF1F5F9),
                                tint = Color(0xFF94A3B8),
                                isLocked = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// =============================================================================
// SUB-COMPONENTS & GRAPHICS
// =============================================================================

@Composable
private fun HabitCard(
    title: String,
    metric: String,
    subtitle: String,
    cardBg: Color,
    iconBg: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        color = cardBg,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconBg.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconBg,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                ),
                color = Color(0xFF0F172A)
            )

            Text(
                text = metric,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp),
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun SwapCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    text: String,
    savings: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 14.sp,
                    fontSize = 10.sp
                ),
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFFDCFCE7),
                shape = CircleShape
            ) {
                Text(
                    text = savings,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = Color(0xFF15803D),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun BadgeIconItem(
    icon: ImageVector,
    label: String,
    bg: Color,
    tint: Color,
    isLocked: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }

        if (!isLocked) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF334155)
            )
        }
    }
}

@Composable
private fun HappyEarthTrackGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.5f
        val radius = size.height * 0.38f

        // Soft green aura
        drawCircle(
            color = Color(0xFFDCFCE7),
            center = Offset(centerX, centerY),
            radius = radius * 1.25f
        )

        // Blue Earth Sphere
        drawCircle(
            color = Color(0xFF38BDF8),
            center = Offset(centerX, centerY),
            radius = radius
        )

        // Green Continents
        val c = Path().apply {
            moveTo(centerX - radius * 0.5f, centerY - radius * 0.2f)
            cubicTo(
                centerX - radius * 0.1f, centerY - radius * 0.7f,
                centerX + radius * 0.4f, centerY - radius * 0.3f,
                centerX + radius * 0.1f, centerY + radius * 0.3f
            )
            close()
        }
        drawPath(c, Color(0xFF22C55E))

        // Happy Curved Eyes ^ ^
        val eyeRadius = 3f
        drawArc(
            color = Color(0xFF0F172A),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - radius * 0.3f, centerY - radius * 0.1f),
            size = Size(eyeRadius * 2, eyeRadius * 2),
            style = Stroke(width = 2f)
        )
        drawArc(
            color = Color(0xFF0F172A),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX + radius * 0.1f, centerY - radius * 0.1f),
            size = Size(eyeRadius * 2, eyeRadius * 2),
            style = Stroke(width = 2f)
        )

        // Smile
        drawArc(
            color = Color(0xFF0F172A),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - 4f, centerY + 3f),
            size = Size(8f, 6f),
            style = Stroke(width = 2f)
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
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 28.dp)
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
                fontSize = 10.5.sp
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
fun BinMeTrackScreenPreview() {
    BinMeTheme {
        BinMeTrackScreen()
    }
}
