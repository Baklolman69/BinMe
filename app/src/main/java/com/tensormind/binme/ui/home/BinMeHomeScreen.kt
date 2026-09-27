package com.tensormind.binme.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.binme.R
import com.tensormind.binme.data.remote.ScanResultData
import com.tensormind.binme.ui.scan.BinMeCameraScanScreen
import com.tensormind.binme.ui.scan.BinMeScanResultScreen
import com.tensormind.binme.ui.theme.BinMeTheme

@Composable
fun BinMeHomeScreen(
    onNavigateToScan: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToTips: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onLogOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) }
    var chatInitialPrompt by remember { mutableStateOf<String?>(null) }
    var showChatScreen by remember { mutableStateOf(false) }

    var isCameraActive by remember { mutableStateOf(false) }
    var currentScanResultData by remember { mutableStateOf<ScanResultData?>(null) }

    if (isCameraActive) {
        BinMeCameraScanScreen(
            onScanCompleted = { result: ScanResultData ->
                isCameraActive = false
                currentScanResultData = result
            },
            onBackClick = { isCameraActive = false },
            modifier = modifier
        )
    } else if (currentScanResultData != null) {
        val scanData = currentScanResultData!!
        BinMeScanResultScreen(
            scanResult = scanData,
            onScanAnother = {
                currentScanResultData = null
                isCameraActive = true
            },
            onAskBinMe = { query: String ->
                currentScanResultData = null
                chatInitialPrompt = query
                showChatScreen = true
            },
            onNavigateToHome = {
                currentScanResultData = null
                activeTab = 0
            },
            onNavigateToHistory = {
                currentScanResultData = null
                activeTab = 1
                onNavigateToHistory()
            },
            onNavigateToTips = {
                currentScanResultData = null
                activeTab = 3
            },
            onNavigateToSettings = {
                currentScanResultData = null
                activeTab = 4
            },
            modifier = modifier
        )
    } else if (showChatScreen) {
        com.tensormind.binme.ui.chat.BinMeChatScreen(
            initialPrompt = chatInitialPrompt,
            onBackClick = {
                showChatScreen = false
                chatInitialPrompt = null
            },
            modifier = modifier
        )
    } else if (activeTab == 1) {
        com.tensormind.binme.ui.history.BinMeHistoryScreen(
            onNavigateToHome = { activeTab = 0 },
            onNavigateToTrack = { activeTab = 2 },
            onNavigateToTips = { activeTab = 3 },
            onNavigateToSettings = { activeTab = 4 },
            onAskAboutItem = { query: String ->
                chatInitialPrompt = query
                showChatScreen = true
            },
            modifier = modifier
        )
    } else if (activeTab == 2) {
        com.tensormind.binme.ui.track.BinMeTrackScreen(
            onNavigateToHome = { activeTab = 0 },
            onNavigateToHistory = {
                activeTab = 1
                onNavigateToHistory()
            },
            onNavigateToTips = { activeTab = 3 },
            onNavigateToSettings = { activeTab = 4 },
            modifier = modifier
        )
    } else if (activeTab == 3) {
        com.tensormind.binme.ui.tips.BinMeTipsScreen(
            onNavigateToHome = { activeTab = 0 },
            onNavigateToHistory = {
                activeTab = 1
                onNavigateToHistory()
            },
            onNavigateToSettings = { activeTab = 4 },
            modifier = modifier
        )
    } else if (activeTab == 4) {
        com.tensormind.binme.ui.settings.BinMeSettingsScreen(
            onNavigateToHome = { activeTab = 0 },
            onNavigateToHistory = {
                activeTab = 1
                onNavigateToHistory()
            },
            onNavigateToTips = { activeTab = 3 },
            onLogOut = {
                activeTab = 0
                onLogOut()
            },
            modifier = modifier
        )
    } else {
        // Color Palette matching reference image precisely
        val bgPage = Color(0xFFFAFDFB)
        val textMain = Color(0xFF0F172A)
        val textSub = Color(0xFF475569)
        val greenPrimary = Color(0xFF16A34A)
        val greenDark = Color(0xFF15803D)
        val greenHeroBg = Color(0xFFF2FAF4)
        val blueCardBg = Color(0xFFF0F6FE)
        val mintPillBg = Color(0xFFEDF8F0)

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
                            onClick = { activeTab = 0 }
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
                            onClick = { activeTab = 3 }
                        )
                        BottomNavItem(
                            icon = Icons.Default.Settings,
                            label = "Settings",
                            isSelected = activeTab == 4,
                            onClick = { activeTab = 4 }
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
                                text = "Snap. Sort. A cleaner tomorrow.",
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
                            .background(Color(0xFFE2F7E7))
                            .clickable { activeTab = 2 },
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
                // 2. HERO CARD ("Small choices. Big impact.")
                // =================================================================
                Surface(
                    color = greenHeroBg,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.15f)) {
                            Text(
                                text = buildAnnotatedString {
                                    append("Small choices.\n")
                                    withStyle(SpanStyle(color = greenPrimary, fontWeight = FontWeight.Bold)) {
                                        append("Big impact.")
                                    }
                                },
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 25.sp,
                                    lineHeight = 31.sp
                                ),
                                color = textMain
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Snap a photo of an item and let BinME tell you if it's recyclable, compostable, or trash — and why.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 18.sp,
                                    fontSize = 12.5.sp
                                ),
                                color = textSub
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Earth + 3 Bins High-Fidelity Vector Graphic
                        Box(
                            modifier = Modifier
                                .weight(0.95f)
                                .height(135.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            HeroEarthGraphic()
                        }
                    }
                }

                // =================================================================
                // 3. MIDDLE DUAL CARDS
                // =================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // LEFT CARD: "Scan a Product"
                    Surface(
                        onClick = { isCameraActive = true },
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(240.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFF22C55E), Color(0xFF15803D))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            // Organic wave background texture
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val wavePath = Path().apply {
                                    moveTo(0f, size.height * 0.2f)
                                    cubicTo(
                                        size.width * 0.4f, size.height * 0.05f,
                                        size.width * 0.7f, size.height * 0.35f,
                                        size.width, size.height * 0.25f
                                    )
                                    lineTo(size.width, 0f)
                                    lineTo(0f, 0f)
                                    close()
                                }
                                drawPath(wavePath, Color.White.copy(alpha = 0.08f))
                            }

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Camera Circle with glowing halo ring
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.22f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Camera",
                                            tint = greenDark,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    // Top right sparkle lines
                                    Canvas(modifier = Modifier.matchParentSize()) {
                                        val strokeW = 3f
                                        val color = Color.White.copy(alpha = 0.9f)
                                        drawLine(
                                            color = color,
                                            start = Offset(size.width * 0.78f, size.height * 0.15f),
                                            end = Offset(size.width * 0.92f, size.height * 0.02f),
                                            strokeWidth = strokeW,
                                            cap = StrokeCap.Round
                                        )
                                        drawLine(
                                            color = color,
                                            start = Offset(size.width * 0.88f, size.height * 0.32f),
                                            end = Offset(size.width * 1.02f, size.height * 0.26f),
                                            strokeWidth = strokeW,
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "Scan a Product",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ),
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Take a photo and get an instant result with a reason.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            lineHeight = 15.sp,
                                            fontSize = 11.5.sp
                                        ),
                                        color = Color.White.copy(alpha = 0.92f)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = { isCameraActive = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = CircleShape,
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CameraAlt,
                                                contentDescription = null,
                                                tint = greenDark,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Open Camera",
                                                style = MaterialTheme.typography.labelMedium.copy(
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
                                }
                            }
                        }
                    }

                    // RIGHT CARD: Tilted Scanner Preview Card
                    Surface(
                        onClick = { isCameraActive = true },
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(240.dp)
                            .graphicsLayer { rotationZ = -2.5f }
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Water Bottle Photo with Corner Viewfinder Marks
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(132.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.binme_bottle_preview),
                                        contentDescription = "Bottle Preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Camera Viewfinder Brackets [ ]
                                    Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                                        val strokeW = 3.5f
                                        val cornerLen = 16f
                                        val color = Color.White

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

                                    // Top right floating green leaf pill inside photo
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(greenPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Eco,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }

                                // Overlapping Bottom Result Badge
                                Surface(
                                    color = Color(0xFFF0FDF4),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(greenPrimary),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Autorenew,
                                                        contentDescription = "Recyclable",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Recyclable",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.5.sp
                                                    ),
                                                    color = greenDark
                                                )
                                            }

                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                                contentDescription = "Details",
                                                tint = greenDark,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = "This is a PET plastic bottle. It can be recycled in most curbside programs.",
                                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 12.sp),
                                            color = textSub,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // =================================================================
                // 4. AI ASSISTANT CARD ("Ask BinME")
                // =================================================================
                Surface(
                    color = blueCardBg,
                    shape = RoundedCornerShape(28.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(28.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.15f)) {
                            Text(
                                text = "AI ASSISTANT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 10.sp
                                ),
                                color = Color(0xFF3B82F6)
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Ask BinME",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                color = textMain
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Wondering if you can recycle something in your city? Ask away!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 17.sp,
                                    fontSize = 11.5.sp
                                ),
                                color = textSub
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showChatScreen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = CircleShape,
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubble,
                                        contentDescription = "Chat",
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Start Chat",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        ),
                                        color = Color(0xFF2563EB)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Mascot + Stacked Floating Speech Bubbles
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // AI Robot Mascot Graphic
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(68.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                MascotRobotGraphic()
                            }

                            // Speech Bubble 1 (Blue)
                            Surface(
                                onClick = {
                                    chatInitialPrompt = "Can I recycle this in my city?"
                                    showChatScreen = true
                                },
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(18.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD))
                            ) {
                                Text(
                                    text = "Can I recycle this in my city?",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 9.5.sp
                                    ),
                                    color = Color(0xFF0369A1),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            // Speech Bubble 2 (Green)
                            Surface(
                                onClick = {
                                    chatInitialPrompt = "Is this compostable?"
                                    showChatScreen = true
                                },
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(18.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                            ) {
                                Text(
                                    text = "Is this compostable?",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 9.5.sp
                                    ),
                                    color = greenDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            // Speech Bubble 3 (Purple)
                            Surface(
                                onClick = {
                                    chatInitialPrompt = "Where do I dispose of this?"
                                    showChatScreen = true
                                },
                                color = Color(0xFFF3E8FF),
                                shape = RoundedCornerShape(18.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD8B4FE))
                            ) {
                                Text(
                                    text = "Where do I dispose of this?",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 9.5.sp
                                    ),
                                    color = Color(0xFF7C3AED),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // =================================================================
                // 5. IMPACT VALUE CHIPS ROW (Mint Green Bar)
                // =================================================================
                Surface(
                    color = mintPillBg,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImpactChip(icon = Icons.Default.Eco, label = "Less waste", tint = greenPrimary)
                        HorizontalDivider(modifier = Modifier.height(18.dp).width(1.dp), color = Color(0xFFCBD5E1))
                        ImpactChip(icon = Icons.Default.Autorenew, label = "More recycling", tint = greenDark)
                        HorizontalDivider(modifier = Modifier.height(18.dp).width(1.dp), color = Color(0xFFCBD5E1))
                        ImpactChip(icon = Icons.Default.Park, label = "Healthier planet", tint = Color(0xFF047857))
                        HorizontalDivider(modifier = Modifier.height(18.dp).width(1.dp), color = Color(0xFFCBD5E1))
                        ImpactChip(icon = Icons.Default.Favorite, label = "A cleaner future", tint = Color(0xFF059669))
                    }
                }
            }
        }
    }
}

// =============================================================================
// HIGH-FIDELITY VECTOR ILLUSTRATIONS & GRAPHIC HELPERS
// =============================================================================

@Composable
private fun HeroEarthGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.55f
        val centerY = size.height * 0.42f
        val globeRadius = size.height * 0.38f

        // 1. Light green aura background
        drawCircle(
            color = Color(0xFFDCFCE7),
            center = Offset(centerX, centerY),
            radius = globeRadius * 1.35f
        )

        // 2. Sparkle rays radiating from top-right
        val sparkleColor = Color(0xFF22C55E)
        val strokeW = 3.5f
        drawLine(
            color = sparkleColor,
            start = Offset(centerX + globeRadius * 0.8f, centerY - globeRadius * 0.9f),
            end = Offset(centerX + globeRadius * 1.15f, centerY - globeRadius * 1.25f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = sparkleColor,
            start = Offset(centerX + globeRadius * 1.1f, centerY - globeRadius * 0.5f),
            end = Offset(centerX + globeRadius * 1.45f, centerY - globeRadius * 0.65f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = sparkleColor,
            start = Offset(centerX + globeRadius * 0.3f, centerY - globeRadius * 1.2f),
            end = Offset(centerX + globeRadius * 0.4f, centerY - globeRadius * 1.55f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )

        // 3. Globe Blue Sphere
        drawCircle(
            color = Color(0xFF38BDF8),
            center = Offset(centerX, centerY),
            radius = globeRadius
        )

        // Continents (Green shapes on globe)
        val continent1 = Path().apply {
            moveTo(centerX - globeRadius * 0.6f, centerY - globeRadius * 0.2f)
            cubicTo(
                centerX - globeRadius * 0.2f, centerY - globeRadius * 0.8f,
                centerX + globeRadius * 0.3f, centerY - globeRadius * 0.4f,
                centerX + globeRadius * 0.1f, centerY + globeRadius * 0.2f
            )
            cubicTo(
                centerX - globeRadius * 0.4f, centerY + globeRadius * 0.5f,
                centerX - globeRadius * 0.8f, centerY + globeRadius * 0.1f,
                centerX - globeRadius * 0.6f, centerY - globeRadius * 0.2f
            )
            close()
        }
        drawPath(continent1, Color(0xFF22C55E))

        val continent2 = Path().apply {
            moveTo(centerX + globeRadius * 0.2f, centerY + globeRadius * 0.1f)
            cubicTo(
                centerX + globeRadius * 0.7f, centerY - globeRadius * 0.2f,
                centerX + globeRadius * 0.8f, centerY + globeRadius * 0.5f,
                centerX + globeRadius * 0.4f, centerY + globeRadius * 0.7f
            )
            close()
        }
        drawPath(continent2, Color(0xFF16A34A))

        // Leaves sprouting around globe
        val leafPath1 = Path().apply {
            moveTo(centerX - globeRadius * 1.1f, centerY)
            cubicTo(
                centerX - globeRadius * 1.4f, centerY - globeRadius * 0.5f,
                centerX - globeRadius * 0.9f, centerY - globeRadius * 0.8f,
                centerX - globeRadius * 0.8f, centerY - globeRadius * 0.4f
            )
            close()
        }
        drawPath(leafPath1, Color(0xFF15803D))

        val leafPath2 = Path().apply {
            moveTo(centerX + globeRadius * 0.8f, centerY - globeRadius * 0.1f)
            cubicTo(
                centerX + globeRadius * 1.3f, centerY - globeRadius * 0.3f,
                centerX + globeRadius * 1.2f, centerY + globeRadius * 0.3f,
                centerX + globeRadius * 0.8f, centerY + globeRadius * 0.1f
            )
            close()
        }
        drawPath(leafPath2, Color(0xFF22C55E))

        // 4. Three Waste Bins at bottom
        val binY = centerY + globeRadius * 0.35f
        val binW = 22.dp.toPx()
        val binH = 28.dp.toPx()
        val binSpacing = 28.dp.toPx()

        // Green Bin (Recycling)
        val greenBinX = centerX - binSpacing
        drawRoundRect(
            color = Color(0xFF16A34A),
            topLeft = Offset(greenBinX - binW / 2, binY),
            size = Size(binW, binH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = Color(0xFF15803D),
            topLeft = Offset(greenBinX - binW / 2 - 2f, binY - 4f),
            size = Size(binW + 4f, 6f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        // Recycle Icon on Green Bin
        drawCircle(Color.White, center = Offset(greenBinX, binY + binH * 0.5f), radius = 5f)

        // Brown Bin (Compost)
        val brownBinX = centerX
        drawRoundRect(
            color = Color(0xFF854D0E),
            topLeft = Offset(brownBinX - binW / 2, binY + 2f),
            size = Size(binW, binH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = Color(0xFF713F12),
            topLeft = Offset(brownBinX - binW / 2 - 2f, binY - 2f),
            size = Size(binW + 4f, 6f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        // Leaf Icon on Brown Bin
        drawCircle(Color.White, center = Offset(brownBinX, binY + binH * 0.5f), radius = 5f)

        // Grey Bin (Trash)
        val greyBinX = centerX + binSpacing
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(greyBinX - binW / 2, binY + 4f),
            size = Size(binW, binH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(greyBinX - binW / 2 - 2f, binY),
            size = Size(binW + 4f, 6f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        // Trash Can Icon on Grey Bin
        drawRect(
            color = Color.White,
            topLeft = Offset(greyBinX - 3f, binY + binH * 0.35f),
            size = Size(6f, 8f)
        )
    }
}

@Composable
private fun MascotRobotGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.5f
        val radius = size.height * 0.42f

        // Glowing Blue Outer Halo
        drawCircle(
            color = Color(0xFFDBEAFE),
            center = Offset(centerX, centerY),
            radius = radius * 1.25f
        )

        // Robot Head Container (Outer Helmet)
        drawCircle(
            color = Color.White,
            center = Offset(centerX, centerY),
            radius = radius
        )
        drawCircle(
            color = Color(0xFF93C5FD),
            center = Offset(centerX, centerY),
            radius = radius,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
        )

        // Inner Dark Visor
        val visorW = radius * 1.3f
        val visorH = radius * 0.9f
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(centerX - visorW / 2, centerY - visorH / 2 + 2f),
            size = Size(visorW, visorH),
            cornerRadius = CornerRadius(16f, 16f)
        )

        // Happy Curved LED Eyes ^ ^
        val eyeRadius = 4f
        val eyeOffsetY = centerY - 2f
        drawArc(
            color = Color.White,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - visorW * 0.28f, eyeOffsetY - eyeRadius),
            size = Size(eyeRadius * 2, eyeRadius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f, cap = StrokeCap.Round)
        )
        drawArc(
            color = Color.White,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX + visorW * 0.28f - eyeRadius * 2, eyeOffsetY - eyeRadius),
            size = Size(eyeRadius * 2, eyeRadius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // Smiling Mouth
        drawArc(
            color = Color.White,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - 4f, centerY + 3f),
            size = Size(8f, 6f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f, cap = StrokeCap.Round)
        )

        // Teal Headphone Pads on Sides
        drawRoundRect(
            color = Color(0xFF2DD4BF),
            topLeft = Offset(centerX - radius - 5f, centerY - 10f),
            size = Size(8f, 20f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = Color(0xFF2DD4BF),
            topLeft = Offset(centerX + radius - 3f, centerY - 10f),
            size = Size(8f, 20f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Top Leaf Antenna Sprouting Top Right
        val leafStem = Path().apply {
            moveTo(centerX + radius * 0.3f, centerY - radius * 0.8f)
            quadraticTo(
                centerX + radius * 0.5f, centerY - radius * 1.3f,
                centerX + radius * 0.8f, centerY - radius * 1.4f
            )
        }
        drawPath(leafStem, Color(0xFF16A34A), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f, cap = StrokeCap.Round))

        val antennaLeaf = Path().apply {
            moveTo(centerX + radius * 0.8f, centerY - radius * 1.4f)
            cubicTo(
                centerX + radius * 1.2f, centerY - radius * 1.6f,
                centerX + radius * 1.1f, centerY - radius * 1.0f,
                centerX + radius * 0.8f, centerY - radius * 1.4f
            )
            close()
        }
        drawPath(antennaLeaf, Color(0xFF22C55E))
    }
}

@Composable
private fun ImpactChip(
    icon: ImageVector,
    label: String,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            ),
            color = Color(0xFF334155)
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
fun BinMeHomeScreenPreview() {
    BinMeTheme {
        BinMeHomeScreen()
    }
}
