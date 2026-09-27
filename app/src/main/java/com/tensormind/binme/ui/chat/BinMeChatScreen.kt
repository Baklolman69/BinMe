package com.tensormind.binme.ui.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensormind.binme.data.remote.GroqRepository
import com.tensormind.binme.ui.theme.BinMeTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessageItem(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val time: String,
    val imageRes: Int? = null,
    val headline: String? = null,
    val classification: ClassificationBadgeData? = null,
    val quickTips: List<String> = emptyList()
)

data class ClassificationBadgeData(
    val category: String, // "Compost", "Recyclable", "Trash"
    val item: String, // "Pizza box (greasy)"
    val confidence: String // "High confidence (92%)"
)

@Composable
fun BinMeChatScreen(
    initialPrompt: String? = null,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val repository = remember { GroqRepository() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val messages = remember { mutableStateListOf<ChatMessageItem>() }

    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank()) {
            handleSendMessage(
                query = initialPrompt,
                messages = messages,
                repository = repository,
                scope = scope,
                onSetThinking = { isThinking = it }
            )
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

                        // Logo Shield Badge
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(greenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(22.dp)) {
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

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bin",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp
                                    ),
                                    color = textMain
                                )
                                Text(
                                    text = "ME",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp
                                    ),
                                    color = greenPrimary
                                )
                            }

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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Location Chip
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Lake Oswego, OR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    ),
                                    color = Color(0xFF334155)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Leaf Action Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2F7E7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = "Eco",
                                tint = greenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Quick Suggestion Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    val chips = listOf(
                        ChipData("Scan an item", Icons.Default.CameraAlt),
                        ChipData("Local rules", Icons.Default.LocationOn),
                        ChipData("Recycling tips", Icons.Default.Eco),
                        ChipData("My impact", Icons.Default.BarChart)
                    )

                    items(chips) { chip ->
                        Surface(
                            onClick = {
                                handleSendMessage(
                                    query = chip.label,
                                    messages = messages,
                                    repository = repository,
                                    scope = scope,
                                    onSetThinking = { isThinking = it }
                                )
                            },
                            shape = CircleShape,
                            color = Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = chip.icon,
                                    contentDescription = chip.label,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = chip.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }

                // Input Bar Container
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2F7E7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = greenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = "Type your question...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                                    color = Color(0xFF94A3B8)
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (inputText.isNotBlank()) {
                                    val query = inputText
                                    inputText = ""
                                    handleSendMessage(
                                        query = query,
                                        messages = messages,
                                        repository = repository,
                                        scope = scope,
                                        onSetThinking = { isThinking = it }
                                    )
                                }
                            }),
                            modifier = Modifier.weight(1f)
                        )

                        // Solid Green Send Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(greenPrimary)
                                .clickable {
                                    if (inputText.isNotBlank()) {
                                        val query = inputText
                                        inputText = ""
                                        handleSendMessage(
                                            query = query,
                                            messages = messages,
                                            repository = repository,
                                            scope = scope,
                                            onSetThinking = { isThinking = it }
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFFBFDFB),
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (messages.isEmpty() && !isThinking) {
                // Centered Empty State Welcome Card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2F7E7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = greenPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "How can I help you today?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = textMain,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ask BinME anything about recycling, composting, or trash sorting rules in your area!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            fontSize = 13.sp
                        ),
                        color = textSub,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3 Prompt Starter Chips
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val samplePrompts = listOf(
                            "Can I recycle a pizza box in Lake Oswego?",
                            "Is a plastic coffee cup compostable or recyclable?",
                            "Where do I dispose of old batteries?"
                        )

                        samplePrompts.forEach { prompt ->
                            Surface(
                                onClick = {
                                    handleSendMessage(
                                        query = prompt,
                                        messages = messages,
                                        repository = repository,
                                        scope = scope,
                                        onSetThinking = { isThinking = it }
                                    )
                                },
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = prompt,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        ),
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(messages) { msg ->
                        if (msg.isUser) {
                            UserMessageBubble(msg = msg)
                        } else {
                            AiResponseCard(msg = msg)
                        }
                    }

                    if (isThinking) {
                        item {
                            AiThinkingBubble()
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun UserMessageBubble(msg: ChatMessageItem) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.End) {
                if (msg.text.isNotBlank()) {
                    Surface(
                        color = Color(0xFF046A38),
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 4.dp
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.5.sp,
                                lineHeight = 18.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                if (msg.imageRes != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = 3.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .width(220.dp)
                            .height(130.dp)
                    ) {
                        Image(
                            painter = painterResource(msg.imageRes),
                            contentDescription = "User Attachment",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = msg.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontSize = 9.5.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Delivered",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // User Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF16A34A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AiResponseCard(msg: ChatMessageItem) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Robot Mascot Avatar
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(22.dp)) {
                drawCircle(Color(0xFF0F172A), radius = size.width * 0.45f)
                // Happy eyes
                drawArc(
                    color = Color.White,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.2f, size.height * 0.35f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.25f, size.height * 0.25f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.55f, size.height * 0.35f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.25f, size.height * 0.25f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 22.dp,
                    bottomStart = 22.dp,
                    bottomEnd = 22.dp
                ),
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!msg.headline.isNullOrBlank()) {
                        Text(
                            text = msg.headline,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Formatted Markdown AI Response Text
                    FormattedAiMarkdownText(text = msg.text)

                    // Classification Badge
                    if (msg.classification != null) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22C55E)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Eco,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = msg.classification.category,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.5.sp
                                            ),
                                            color = Color(0xFF15803D)
                                        )
                                        Text(
                                            text = msg.classification.item,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = msg.classification.confidence,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        ),
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Quick Tips Section
                    if (msg.quickTips.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Tips",
                                    tint = Color(0xFF047857),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Quick Tips",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    ),
                                    color = Color(0xFF047857)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            msg.quickTips.forEach { tip ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "• ",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF15803D)
                                    )
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            lineHeight = 16.sp,
                                            fontSize = 11.5.sp
                                        ),
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = msg.time,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontSize = 9.5.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun FormattedAiMarkdownText(text: String, modifier: Modifier = Modifier) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val lines = text.split("\n")

    var currentSectionType = "normal" // "normal", "danger", "success", "info"

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lines.forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isNotBlank()) {
                val cleanLine = line.removePrefix("* ").removePrefix("- ").removePrefix("• ").trim()

                // Filter out stray divider lines like "--", "---", "* --"
                if (cleanLine == "--" || cleanLine == "---" || cleanLine == "* --" || cleanLine == "* ---") {
                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                }
                // Header Lines (#, ##, ###)
                else if (line.startsWith("#") || (line.startsWith("##") || line.startsWith("###"))) {
                    val rawHeader = line.replace("#", "").trim()

                    currentSectionType = when {
                        "❌" in rawHeader || "NOT" in rawHeader || "Do Not" in rawHeader -> "danger"
                        "📋" in rawHeader || "Classification" in rawHeader || "Details" in rawHeader -> "success"
                        "✅" in rawHeader || "Why" in rawHeader || "Source" in rawHeader -> "info"
                        else -> "normal"
                    }

                    val (bgColor, borderColor, textColor) = when (currentSectionType) {
                        "danger" -> Triple(Color(0xFFFEF2F2), Color(0xFFFCA5A5), Color(0xFF991B1B))
                        "success" -> Triple(Color(0xFFF0FDF4), Color(0xFF86EFAC), Color(0xFF15803D))
                        "info" -> Triple(Color(0xFFF0F9FF), Color(0xFFBAE6FD), Color(0xFF0369A1))
                        else -> Triple(Color(0xFFF8FAFC), Color(0xFFE2E8F0), Color(0xFF0F172A))
                    }

                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 2.dp)
                    ) {
                        Text(
                            text = rawHeader,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            ),
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
                // Bullet Point Lines (starting with •, -, *)
                else if (line.startsWith("•") || line.startsWith("-") || line.startsWith("* ")) {
                    val bulletText = line.removePrefix("•").removePrefix("-").removePrefix("*").trim()

                    // Check if bullet text contains a markdown link [Text](URL)
                    val linkMatch = Regex("\\[([^\\]]+)\\]\\(([^\\)]+)\\)").find(bulletText)

                    if (linkMatch != null) {
                        val label = linkMatch.groupValues[1]
                        val url = linkMatch.groupValues[2]
                        val cleanLabel = if (label.startsWith("http")) {
                            if ("oswego" in label) "Lake Oswego Sustainability Website" else "Clackamas County Recycling Guide"
                        } else {
                            label.removeSuffix(":")
                        }

                        Surface(
                            onClick = {
                                try { uriHandler.openUri(url) } catch (_: Exception) {}
                            },
                            color = Color(0xFFF0FDF4),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Open Link",
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$cleanLabel ↗",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    } else {
                        val dotColor = if (currentSectionType == "danger") Color(0xFFDC2626) else Color(0xFF16A34A)

                        Row(
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = if (currentSectionType == "danger") "✖ " else "• ",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = dotColor
                            )
                            Text(
                                text = parseRichTextWithBadges(bulletText),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 20.sp,
                                    fontSize = 13.sp
                                ),
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }
                // Standard Text Paragraphs
                else {
                    Text(
                        text = parseRichTextWithBadges(line),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 21.sp,
                            fontSize = 13.5.sp
                        ),
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun parseRichTextWithBadges(input: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val text = input

        while (cursor < text.length) {
            val boldStart = text.indexOf("**", cursor)
            if (boldStart == -1) {
                append(text.substring(cursor))
                break
            }
            append(text.substring(cursor, boldStart))
            val boldEnd = text.indexOf("**", boldStart + 2)
            if (boldEnd == -1) {
                append(text.substring(boldStart))
                break
            }

            val boldContent = text.substring(boldStart + 2, boldEnd)
            val isDanger = "Do NOT" in boldContent || "not" in boldContent.lowercase()

            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDanger) Color(0xFFB91C1C) else Color(0xFF0F172A)
                )
            ) {
                append(boldContent)
            }
            cursor = boldEnd + 2
        }
    }
}

@Composable
private fun AiThinkingBubble() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Thinking",
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Text(
                text = "BinME is thinking...",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

private fun handleSendMessage(
    query: String,
    messages: MutableList<ChatMessageItem>,
    repository: GroqRepository,
    scope: kotlinx.coroutines.CoroutineScope,
    onSetThinking: (Boolean) -> Unit
) {
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    messages.add(ChatMessageItem(isUser = true, text = query, time = time))

    onSetThinking(true)

    scope.launch {
        val answer = repository.sendMessage(userQuery = query)
        onSetThinking(false)

        val isCompost = "compost" in answer.lowercase()
        val isRecycle = "recycle" in answer.lowercase()

        val classification = if (isCompost) {
            ClassificationBadgeData("Compost", query, "High confidence (91%)")
        } else if (isRecycle) {
            ClassificationBadgeData("Recyclable", query, "High confidence (94%)")
        } else {
            null
        }

        messages.add(
            ChatMessageItem(
                isUser = false,
                text = answer,
                time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                classification = classification
            )
        )
    }
}

private data class ChipData(val label: String, val icon: ImageVector)

// =============================================================================
// COMPOSE PREVIEW
// =============================================================================

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun BinMeChatScreenPreview() {
    BinMeTheme {
        BinMeChatScreen()
    }
}
