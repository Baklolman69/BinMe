package com.tensormind.binme.ui.settings

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
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.tensormind.binme.ui.theme.BinMeTheme
import com.tensormind.binme.util.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun BinMeSettingsScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToTips: () -> Unit = {},
    onLogOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    val savedNotifications by dataStoreManager.notificationsEnabled.collectAsState(initial = true)
    val savedLocation by dataStoreManager.userLocation.collectAsState(initial = "Lake Oswego, OR")
    var notificationsEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(savedNotifications) {
        notificationsEnabled = savedNotifications
    }

    val textMain = Color(0xFF0F172A)
    val textSub = Color(0xFF64748B)
    val greenPrimary = Color(0xFF16A34A)
    val greenDark = Color(0xFF15803D)
    val bgPage = Color(0xFFFAFDFB)
    val mintCardBg = Color(0xFFF0FDF4)

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
                        isSelected = false,
                        onClick = onNavigateToTips
                    )
                    BottomNavItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isSelected = true,
                        onClick = { }
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
                            text = "A cleaner tomorrow.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = textSub
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
            // 2. USER PROFILE BANNER
            // =================================================================
            Surface(
                color = mintCardBg,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
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
                        // User Profile Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(greenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hello, Eco Friend",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = textMain
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "🍃",
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Making a difference, one item at a time.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                color = textSub
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Profile Details",
                        tint = textSub,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // =================================================================
            // 3. GENERAL SECTION
            // =================================================================
            SettingsSectionHeader(title = "GENERAL")

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Notifications Item with Switch
                    SettingRowItem(
                        icon = Icons.Default.NotificationsNone,
                        title = "Notifications",
                        subtitle = "Get reminders and updates",
                        trailingContent = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { isChecked ->
                                    notificationsEnabled = isChecked
                                    scope.launch {
                                        dataStoreManager.setNotificationsEnabled(isChecked)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = greenPrimary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                    // Location Item
                    SettingRowItem(
                        icon = Icons.Default.LocationOn,
                        title = "Location",
                        subtitle = savedLocation,
                        showChevron = true
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                    // Language Item
                    SettingRowItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = "English",
                        showChevron = true
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                    // Appearance Item
                    SettingRowItem(
                        icon = Icons.Default.DarkMode,
                        title = "Appearance",
                        subtitle = "Light mode",
                        showChevron = true
                    )
                }
            }

            // =================================================================
            // 4. AI & PRIVACY SECTION
            // =================================================================
            SettingsSectionHeader(title = "AI & PRIVACY")

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // AI Preferences
                    SettingRowItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "AI Preferences",
                        subtitle = "Adjust how BinME classifies and explains",
                        showChevron = true
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                    // Privacy & Data
                    SettingRowItem(
                        icon = Icons.Default.Security,
                        title = "Privacy & Data",
                        subtitle = "Manage your data and permissions",
                        showChevron = true
                    )
                }
            }

            // =================================================================
            // 5. APP SECTION
            // =================================================================
            SettingsSectionHeader(title = "APP")

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // About BinME
                    SettingRowItem(
                        icon = Icons.Default.Info,
                        title = "About BinME",
                        subtitle = "Version 1.0.0",
                        showChevron = true
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                    // Terms of Service
                    SettingRowItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        subtitle = "Read our terms and conditions",
                        showChevron = true
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))

                    // Privacy Policy
                    SettingRowItem(
                        icon = Icons.Default.Gavel,
                        title = "Privacy Policy",
                        subtitle = "How we protect your data",
                        showChevron = true
                    )
                }
            }

            // =================================================================
            // 6. LOG OUT BUTTON
            // =================================================================
            Surface(
                onClick = onLogOut,
                color = mintCardBg,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Log Out",
                                tint = greenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Log Out",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = greenDark
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Log Out Action",
                        tint = greenDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// =============================================================================
// COMPONENT HELPERS
// =============================================================================

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            fontSize = 11.sp
        ),
        color = Color(0xFF64748B),
        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
    )
}

@Composable
private fun SettingRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showChevron: Boolean = false,
    onClick: () -> Unit = {},
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDCFCE7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF15803D),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = Color(0xFF64748B)
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
        } else if (showChevron) {
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

// =============================================================================
// COMPOSE PREVIEW
// =============================================================================

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun BinMeSettingsScreenPreview() {
    BinMeTheme {
        BinMeSettingsScreen()
    }
}
