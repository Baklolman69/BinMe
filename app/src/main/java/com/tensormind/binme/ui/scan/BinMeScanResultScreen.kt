package com.tensormind.binme.ui.scan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.binme.R
import com.tensormind.binme.data.remote.GroqVisionRepository
import com.tensormind.binme.data.remote.ScanResultData
import com.tensormind.binme.ui.theme.BinMeTheme

import coil.compose.AsyncImage

@Composable
fun BinMeScanResultScreen(
    scanResult: ScanResultData = GroqVisionRepository().getDefaultScanResult(),
    onScanAnother: () -> Unit = {},
    onAskBinMe: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToTips: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) }

    val textMain = Color(0xFF0F172A)
    val textSub = Color(0xFF475569)
    val greenPrimary = Color(0xFF16A34A)
    val greenDark = Color(0xFF15803D)
    val bgPage = Color(0xFFFAFDFB)

    val categoryIsCompost = scanResult.category.equals("Compost", ignoreCase = true)
    val categoryIsTrash = scanResult.category.equals("Trash", ignoreCase = true)

    val cardBgColor = when {
        categoryIsCompost -> Color(0xFFFFFBEB)
        categoryIsTrash -> Color(0xFFFAF5FF)
        else -> Color(0xFFF0FDF4)
    }

    val cardBorderColor = when {
        categoryIsCompost -> Color(0xFFFEF3C7)
        categoryIsTrash -> Color(0xFFF3E8FF)
        else -> Color(0xFFDCFCE7)
    }

    val iconBgColor = when {
        categoryIsCompost -> Color(0xFFEAB308)
        categoryIsTrash -> Color(0xFFA855F7)
        else -> greenPrimary
    }

    val iconVector = when {
        categoryIsCompost -> Icons.Default.Eco
        categoryIsTrash -> Icons.Default.Delete
        else -> Icons.Default.Autorenew
    }

    val categoryTextColor = when {
        categoryIsCompost -> Color(0xFF854D0E)
        categoryIsTrash -> Color(0xFF6B21A8)
        else -> greenDark
    }

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
                        label = "Tips",
                        isSelected = activeTab == 2,
                        onClick = {
                            activeTab = 2
                            onNavigateToTips()
                        }
                    )
                    BottomNavItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isSelected = activeTab == 3,
                        onClick = {
                            activeTab = 3
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
                            text = "Know where it goes.",
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
            // 2. CAPTURED PHOTO PREVIEW BOX WITH VIEWFINDER BRACKETS
            // =================================================================
            Surface(
                shape = RoundedCornerShape(26.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!scanResult.imageUri.isNullOrBlank()) {
                        AsyncImage(
                            model = scanResult.imageUri,
                            contentDescription = "Scanned Item Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.binme_bottle_preview),
                            contentDescription = "Scanned Item Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Viewfinder Corners [ ]
                    Canvas(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                        val strokeW = 4f
                        val cornerLen = 22f
                        val color = Color(0xFF86EFAC)

                        // Top Left
                        drawLine(color, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW, StrokeCap.Round)
                        drawLine(color, Offset(0f, 0f), Offset(0f, cornerLen), strokeW, StrokeCap.Round)

                        // Top Right
                        drawLine(color, Offset(size.width, 0f), Offset(size.width - cornerLen, 0f), strokeW, StrokeCap.Round)
                        drawLine(color, Offset(size.width, 0f), Offset(size.width, cornerLen), strokeW, StrokeCap.Round)

                        // Bottom Left
                        drawLine(color, Offset(0f, size.height), Offset(cornerLen, size.height), strokeW, StrokeCap.Round)
                        drawLine(color, Offset(0f, size.height), Offset(0f, size.height - cornerLen), strokeW, StrokeCap.Round)

                        // Bottom Right
                        drawLine(color, Offset(size.width, size.height), Offset(size.width - cornerLen, size.height), strokeW, StrokeCap.Round)
                        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), strokeW, StrokeCap.Round)
                    }

                    // Floating Analyzing Badge Top Right
                    Surface(
                        color = Color(0xFF0F5132).copy(alpha = 0.9f),
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Analyzed",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // =================================================================
            // 2.5 UNCERTAINTY WARNING CARD (If AI is unsure)
            // =================================================================
            if (scanResult.isUncertain) {
                Surface(
                    color = Color(0xFFFFF7ED),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA580C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Uncertainty Warning",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "I'm Not 100% Sure",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color(0xFFC2410C)
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = scanResult.uncertaintyReason ?: "The image might be blurry, dirty, or contains mixed materials. Try taking another photo closer up or scan the barcode.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    lineHeight = 16.sp,
                                    fontSize = 11.5.sp
                                ),
                                color = Color(0xFF9A3412)
                            )
                        }
                    }
                }
            }

            // =================================================================
            // 3. CLASSIFICATION RESULT CARD WITH RATIONALE & CITATION
            // =================================================================
            Surface(
                color = cardBgColor,
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Category Icon Circle
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = scanResult.category,
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = scanResult.category,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = categoryTextColor
                                )

                                Surface(
                                    color = cardBorderColor,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = scanResult.confidence,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = categoryTextColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = scanResult.itemName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = textMain
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = scanResult.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    lineHeight = 17.sp,
                                    fontSize = 12.sp
                                ),
                                color = textSub
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rationale Box ("Why it was classified")
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Classification Rationale",
                                    tint = categoryTextColor,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Why it was classified this way",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = categoryTextColor
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = scanResult.classificationRationale,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    lineHeight = 16.sp,
                                    fontSize = 11.5.sp
                                ),
                                color = textSub
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Citation Link
                            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(cardBorderColor)
                                    .clickable {
                                        try {
                                            uriHandler.openUri(scanResult.verifiedSourceUrl)
                                        } catch (_: Exception) {}
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Verify Source",
                                    tint = categoryTextColor,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Source: ${scanResult.verifiedSourceTitle} ↗",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = categoryTextColor
                                )
                            }
                        }
                    }
                }
            }

            // =================================================================
            // 4. LOCAL GUIDANCE CARD (Lake Oswego, OR)
            // =================================================================
            Surface(
                color = Color(0xFFF0F6FE),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFF1E3A8A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Local Guidance",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = Color(0xFF1E3A8A)
                            )
                        }

                        // Voice Output Speaker Button
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val speechHelper = remember { com.tensormind.binme.util.SpeechHelper(context) }
                        DisposableEffect(Unit) {
                            onDispose { speechHelper.shutdown() }
                        }

                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = CircleShape,
                            modifier = Modifier.clickable {
                                speechHelper.speak("${scanResult.itemName} is classified as ${scanResult.category}. ${scanResult.locationGuidanceTitle}. ${scanResult.locationGuidanceSubtitle}")
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Read Aloud",
                                    tint = Color(0xFF1E3A8A),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Read Aloud 🔊",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    ),
                                    color = Color(0xFF1E3A8A)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Allowed",
                            tint = greenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = scanResult.locationGuidanceTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = textMain
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = scanResult.locationGuidanceSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 16.sp,
                            fontSize = 12.sp
                        ),
                        color = textSub,
                        modifier = Modifier.padding(start = 28.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Action Instruction Pills Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (scanResult.actionPills.size >= 3) {
                            ActionInstructionPill(
                                icon = Icons.Default.WaterDrop,
                                title = scanResult.actionPills[0].title,
                                subtitle = scanResult.actionPills[0].subtitle,
                                modifier = Modifier.weight(1f)
                            )
                            ActionInstructionPill(
                                icon = Icons.Default.Adjust,
                                title = scanResult.actionPills[1].title,
                                subtitle = scanResult.actionPills[1].subtitle,
                                modifier = Modifier.weight(1f)
                            )
                            ActionInstructionPill(
                                icon = Icons.Default.Delete,
                                title = scanResult.actionPills[2].title,
                                subtitle = scanResult.actionPills[2].subtitle,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            ActionInstructionPill(
                                icon = Icons.Default.WaterDrop,
                                title = "Rinse it",
                                subtitle = "Remove leftover liquid or food.",
                                modifier = Modifier.weight(1f)
                            )
                            ActionInstructionPill(
                                icon = Icons.Default.Adjust,
                                title = "Remove cap",
                                subtitle = "(if possible) ℹ",
                                modifier = Modifier.weight(1f)
                            )
                            ActionInstructionPill(
                                icon = Icons.Default.Delete,
                                title = "Place in",
                                subtitle = "recycling bin",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // =================================================================
            // 4.5 OPENSTREETMAP NEARBY DISPOSAL MAP CARD
            // =================================================================
            com.tensormind.binme.ui.map.OpenStreetMapDisposalCard(
                locations = if (scanResult.nearbyLocations.isNotEmpty()) scanResult.nearbyLocations else com.tensormind.binme.data.LocalRecyclingDatabase.NEARBY_LOCATIONS,
                itemName = scanResult.itemName
            )

            // =================================================================
            // 5. ACTION BUTTONS (Scan Another + Ask BinME)
            // =================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Scan Another Button
                Button(
                    onClick = onScanAnother,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scan Another",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF334155)
                        )
                    }
                }

                // Ask BinME Button
                Button(
                    onClick = { onAskBinMe("Can I recycle a plastic bottle in Lake Oswego?") },
                    colors = ButtonDefaults.buttonColors(containerColor = greenPrimary),
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Ask BinME",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "(e.g. \"Can I recycle this here?\")",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun ActionInstructionPill(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                ),
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 12.sp,
                    fontSize = 8.5.sp
                ),
                color = Color(0xFF64748B)
            )
        }
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
fun BinMeScanResultScreenPreview() {
    BinMeTheme {
        BinMeScanResultScreen()
    }
}
