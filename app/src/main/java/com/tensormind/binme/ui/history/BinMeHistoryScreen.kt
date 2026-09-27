package com.tensormind.binme.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.binme.data.HistoryItem
import com.tensormind.binme.data.ScanHistoryRepository

@Composable
fun BinMeHistoryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToTrack: () -> Unit = {},
    onNavigateToTips: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onAskAboutItem: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val historyItems by ScanHistoryRepository.historyList.collectAsState()

    val textMain = Color(0xFF0F172A)
    val textSub = Color(0xFF64748B)
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
                        isSelected = false,
                        onClick = onNavigateToHome
                    )
                    BottomNavItem(
                        icon = Icons.Default.Schedule,
                        label = "History",
                        isSelected = true,
                        onClick = { }
                    )
                    BottomNavItem(
                        icon = Icons.Default.Eco,
                        label = "Track",
                        isSelected = false,
                        onClick = onNavigateToTrack
                    )
                    BottomNavItem(
                        icon = Icons.Default.Lightbulb,
                        label = "Tips",
                        isSelected = false,
                        onClick = onNavigateToTips
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
        containerColor = bgPage,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = "Scan History",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = textSub
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2F7E7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = greenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Subtitle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Waste Scans",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = textMain
                    )
                    Text(
                        text = "${historyItems.size} items classified",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = textSub
                    )
                }

                if (historyItems.isNotEmpty()) {
                    TextButton(onClick = { ScanHistoryRepository.clearHistory() }) {
                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }

            // History List / Empty State
            if (historyItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2F7E7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = greenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "No Scans Yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = textMain
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Items you scan will appear here automatically.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = textSub
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(historyItems) { item ->
                        HistoryCard(
                            item = item,
                            onClick = { onAskAboutItem("How do I properly dispose of ${item.itemName} in Lake Oswego?") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    item: HistoryItem,
    onClick: () -> Unit
) {
    val isCompost = item.category.equals("Compost", ignoreCase = true)
    val isTrash = item.category.equals("Trash", ignoreCase = true)

    val badgeBg = when {
        isCompost -> Color(0xFFFEF3C7)
        isTrash -> Color(0xFFF3E8FF)
        else -> Color(0xFFDCFCE7)
    }

    val badgeText = when {
        isCompost -> Color(0xFF854D0E)
        isTrash -> Color(0xFF6B21A8)
        else -> Color(0xFF15803D)
    }

    val icon = when {
        isCompost -> Icons.Default.Eco
        isTrash -> Icons.Default.Delete
        else -> Icons.Default.Autorenew
    }

    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = item.category,
                        tint = badgeText,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.itemName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = Color(0xFF64748B),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
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
