package com.example.rooster

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetConsultationScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(0) }
    var isTeluguMode by remember { mutableStateOf(true) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
    ) {
        // Header with language toggle
        VetConsultationHeader(
            isTeluguMode = isTeluguMode,
            onLanguageToggle = { isTeluguMode = !isTeluguMode },
        )

        // Tab Row
        VetConsultationTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            isTeluguMode = isTeluguMode,
        )

        // Content based on selected tab
        when (selectedTab) {
            0 -> AIHealthChatTab(isTeluguMode = isTeluguMode)
            1 ->
                VetConsultationTab(
                    isTeluguMode = isTeluguMode,
                    navController = navController,
                    onLanguageToggle = onLanguageToggle,
                )
            2 -> ConsultationHistoryTab(isTeluguMode = isTeluguMode)
        }
    }
}

@Composable
private fun VetConsultationHeader(
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = if (isTeluguMode) "వెట్ సంప్రదింపు" else "Vet Consultation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            // Language toggle
            TextButton(
                onClick = onLanguageToggle,
            ) {
                Text(
                    text = if (isTeluguMode) "EN" else "తె",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFF5722),
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
            ),
    )
}

@Composable
private fun VetConsultationTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isTeluguMode: Boolean,
) {
    val tabs =
        listOf(
            Triple(Icons.Default.SmartToy, if (isTeluguMode) "AI చాట్" else "AI Chat", "ai_chat"),
            Triple(
                Icons.Default.VideoCall,
                if (isTeluguMode) "వెట్ సంప్రదింపు" else "Vet Consult",
                "vet_consult",
            ),
            Triple(Icons.Default.History, if (isTeluguMode) "చరిత్ర" else "History", "history"),
        )

    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = Color(0xFFFF5722),
    ) {
        tabs.forEachIndexed { index, (icon, label, _) ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                    }
                },
            )
        }
    }
}

// ===============================
// AI HEALTH CHAT TAB
// ===============================

@Composable
private fun AIHealthChatTab(isTeluguMode: Boolean) {
    var chatMessages by remember { mutableStateOf(listOf<VetChatMessage>()) }
    var currentMessage by remember { mutableStateOf("") }
    var selectedFowlType by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize with welcome message
    LaunchedEffect(Unit) {
        chatMessages =
            listOf(
                VetChatMessage(
                    id = "welcome",
                    content =
                        if (isTeluguMode) {
                            "నమస్కారం! నేను మీ AI వెట్ అసిస్టెంట్. మీ కోళ్ల ఆరోగ్య సమస్యల గురించి అడగండి."
                        } else {
                            "Hello! I'm your AI Vet Assistant. Ask me about your poultry health concerns."
                        },
                    timestamp = Date(),
                    isFromUser = false,
                ),
            )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Fowl type selection
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = if (isTeluguMode) "కోళ్ల రకం ఎంచుకోండి:" else "Select Fowl Type:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val fowlTypes = listOf("Broiler", "Layer", "Desi", "Cockerel")
                    fowlTypes.forEach { type ->
                        FilterChip(
                            onClick = { selectedFowlType = type },
                            label = { Text(type) },
                            selected = selectedFowlType == type,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // Chat messages
        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false,
        ) {
            items(chatMessages) { message ->
                VetChatMessageCard(
                    message = message,
                    isTeluguMode = isTeluguMode,
                )
            }

            if (isLoading) {
                item {
                    TypingIndicator(isTeluguMode = isTeluguMode)
                }
            }
        }

        // Message input
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                OutlinedTextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    placeholder = {
                        Text(
                            if (isTeluguMode) {
                                "లక్షణాలు లేదా ప్రశ్న రాయండి..."
                            } else {
                                "Describe symptoms or ask a question..."
                            },
                        )
                    },
                    modifier = Modifier.weight(1f),
                    minLines = 1,
                    maxLines = 3,
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        if (currentMessage.isNotBlank() && selectedFowlType.isNotBlank()) {
                            scope.launch {
                                sendAIMessage(
                                    message = currentMessage,
                                    fowlType = selectedFowlType,
                                    isTeluguMode = isTeluguMode,
                                    chatMessages = chatMessages,
                                    onUpdateMessages = { chatMessages = it },
                                    onLoading = { isLoading = it },
                                )
                                currentMessage = ""
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun VetChatMessageCard(
    message: VetChatMessage,
    isTeluguMode: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (message.isFromUser) {
                            Color(0xFFFF5722)
                        } else {
                            Color.White
                        },
                ),
            border =
                if (!message.isFromUser) {
                    androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                } else {
                    null
                },
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isFromUser) Color.White else Color.Black,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        if (message.isFromUser) {
                            Color.White.copy(alpha = 0.7f)
                        } else {
                            Color.Gray
                        },
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator(isTeluguMode: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color(0xFFFF5722),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "AI ఆలోచిస్తోంది..." else "AI is thinking...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
    }
}

// ===============================
// VET CONSULTATION TAB
// ===============================

@Composable
private fun VetConsultationTab(
    isTeluguMode: Boolean,
    navController: NavController,
    onLanguageToggle: () -> Unit,
) {
    var availableVets by remember { mutableStateOf(listOf<VetProfile>()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedRegion by remember { mutableStateOf("All") }
    var inCall by remember { mutableStateOf(false) }

    LaunchedEffect(selectedRegion) {
        fetchAvailableVets(
            region = if (selectedRegion == "All") null else selectedRegion,
            onResult = { availableVets = it },
            onError = { error = it },
            setLoading = { isLoading = it },
        )
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = if (isTeluguMode) "అందుబాటులో ఉన్న వెట్లు" else "Available Veterinarians",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
        }

        item {
            // Region filter
            RegionFilterRow(
                selectedRegion = selectedRegion,
                onRegionSelected = { selectedRegion = it },
                isTeluguMode = isTeluguMode,
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF5722))
                }
            }
        } else if (error != null) {
            item {
                ErrorCard(
                    error = error!!,
                    isTeluguMode = isTeluguMode,
                    onRetry = {
                        error = null
                        fetchAvailableVets(
                            region = if (selectedRegion == "All") null else selectedRegion,
                            onResult = { availableVets = it },
                            onError = { error = it },
                            setLoading = { isLoading = it },
                        )
                    },
                )
            }
        } else if (availableVets.isEmpty()) {
            item {
                EmptyStateCard(
                    message =
                        if (isTeluguMode) {
                            "ప్రస్తుతం వెట్లు అందుబాటులో లేరు"
                        } else {
                            "No veterinarians available"
                        },
                    isTeluguMode = isTeluguMode,
                )
            }
        } else {
            items(availableVets) { vet ->
                VetProfileCard(
                    vet = vet,
                    isTeluguMode = isTeluguMode,
                    onBookConsultation = { consultationType ->
                        // Handle consultation booking
                        if (consultationType == ConsultationType.VIDEO_CALL || consultationType == ConsultationType.VOICE_CALL) {
                            inCall = true // Simulate starting a call
                        }
                    },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Center buttons
                ) {
                    Text(
                        text = if (isTeluguMode) "తక్షణ సహాయం" else "Immediate Help",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    // WebRTC Call Button (simulated)
                    Button(onClick = { inCall = true }) {
                        Icon(
                            Icons.Default.VideoCall,
                            contentDescription = stringResource(id = R.string.start_call),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isTeluguMode) {
                                stringResource(id = R.string.start_call_te)
                            } else {
                                stringResource(
                                    id = R.string.start_call,
                                )
                            },
                        )
                    }
                    if (inCall) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(if (isTeluguMode) "కాల్ కొనసాగుతోంది..." else "Call in progress...")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { inCall = false }) {
                            Icon(
                                Icons.Default.CallEnd,
                                contentDescription = stringResource(id = R.string.end_call),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isTeluguMode) {
                                    stringResource(id = R.string.end_call_te)
                                } else {
                                    stringResource(
                                        id = R.string.end_call,
                                    )
                                },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // AI Chatbot Button (placeholder for ML Kit integration)
                    OutlinedButton(onClick = {
                        // TODO: Navigate to or open AI Chatbot interface (e.g., AIHealthChatTab or a new screen)
                        // For now, this button is a placeholder.
                        // You might want to switch to the AIHealthChatTab:
                        // onTabSelected(0) // Assuming onTabSelected is available here or passed down
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = stringResource(id = R.string.chat_with_ai),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isTeluguMode) {
                                stringResource(id = R.string.chat_with_ai_te)
                            } else {
                                stringResource(
                                    id = R.string.chat_with_ai,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RegionFilterRow(
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    isTeluguMode: Boolean,
) {
    val regions = listOf("All", "Telangana", "Andhra Pradesh", "Karnataka", "Tamil Nadu")

    LazyColumn {
        item {
            Text(
                text = if (isTeluguMode) "ప్రాంతం:" else "Region:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                regions.take(3).forEach { region ->
                    FilterChip(
                        onClick = { onRegionSelected(region) },
                        label = {
                            Text(
                                text = region,
                                fontSize = 12.sp,
                            )
                        },
                        selected = selectedRegion == region,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                regions.drop(3).forEach { region ->
                    FilterChip(
                        onClick = { onRegionSelected(region) },
                        label = {
                            Text(
                                text = region,
                                fontSize = 12.sp,
                            )
                        },
                        selected = selectedRegion == region,
                        modifier = Modifier.weight(1f),
                    )
                }
                // Add empty space if needed
                if (regions.drop(3).size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun VetProfileCard(
    vet: VetProfile,
    isTeluguMode: Boolean,
    onBookConsultation: (ConsultationType) -> Unit,
) {
    var showBookingDialog by remember { mutableStateOf(false) }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { showBookingDialog = true },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Profile photo
                AsyncImage(
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(vet.profilePhotoUrl ?: R.drawable.ic_launcher_foreground)
                            .crossfade(true)
                            .size(150)
                            .build(),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f)),
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vet.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = vet.qualification,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint =
                                    if (index < vet.rating.toInt()) {
                                        Color(0xFFFFC107)
                                    } else {
                                        Color.Gray.copy(alpha = 0.3f)
                                    },
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = vet.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = "₹${vet.consultationFee.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5722),
                    )
                    Text(
                        text = if (isTeluguMode) "సంప్రదింపు" else "consultation",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specializations
            if (vet.specialization.isNotEmpty()) {
                Text(
                    text = if (isTeluguMode) "నైపుణ్యాలు:" else "Specializations:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = vet.specialization.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Book consultation button
            Button(
                onClick = { showBookingDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722),
                    ),
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "సంప్రదింపు బుక్ చేయండి" else "Book Consultation",
                    color = Color.White,
                )
            }
        }
    }

    // Booking dialog
    if (showBookingDialog) {
        ConsultationBookingDialog(
            vet = vet,
            isTeluguMode = isTeluguMode,
            onConfirm = { consultationType ->
                onBookConsultation(consultationType)
                showBookingDialog = false
            },
            onDismiss = { showBookingDialog = false },
        )
    }
}

// ===============================
// CONSULTATION HISTORY TAB
// ===============================

@Composable
private fun ConsultationHistoryTab(isTeluguMode: Boolean) {
    var consultations by remember { mutableStateOf(listOf<VetConsultation>()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        fetchConsultationHistory(
            onResult = { consultations = it },
            onError = { error = it },
            setLoading = { isLoading = it },
        )
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = if (isTeluguMode) "సంప్రదింపు చరిత్ర" else "Consultation History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF5722))
                }
            }
        } else if (error != null) {
            item {
                ErrorCard(
                    error = error!!,
                    isTeluguMode = isTeluguMode,
                    onRetry = {
                        error = null
                        fetchConsultationHistory(
                            onResult = { consultations = it },
                            onError = { error = it },
                            setLoading = { isLoading = it },
                        )
                    },
                )
            }
        } else if (consultations.isEmpty()) {
            item {
                EmptyStateCard(
                    message =
                        if (isTeluguMode) {
                            "ఇంకా సంప్రదింపులు లేవు"
                        } else {
                            "No consultations yet"
                        },
                    isTeluguMode = isTeluguMode,
                )
            }
        } else {
            items(consultations) { consultation ->
                ConsultationHistoryCard(
                    consultation = consultation,
                    isTeluguMode = isTeluguMode,
                )
            }
        }
    }
}

@Composable
private fun ConsultationHistoryCard(
    consultation: VetConsultation,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = consultation.vetName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                StatusChip(
                    status = consultation.status,
                    isTeluguMode = isTeluguMode,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isTeluguMode) "లక్షణాలు:" else "Symptoms:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = consultation.symptoms,
                style = MaterialTheme.typography.bodyMedium,
            )

            if (consultation.aiResponse != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isTeluguMode) "AI సలహా:" else "AI Advice:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = consultation.aiResponse,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text =
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            .format(consultation.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )

                Text(
                    text = "₹${consultation.cost.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722),
                )
            }
        }
    }
}

// ===============================
// HELPER COMPONENTS
// ===============================

@Composable
private fun ErrorCard(
    error: String,
    isTeluguMode: Boolean,
    onRetry: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (isTeluguMode) "లోపం జరిగింది" else "Error occurred",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRetry,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722),
                    ),
            ) {
                Text(
                    text = if (isTeluguMode) "మళ్లీ ప్రయత్నించండి" else "Retry",
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    message: String,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.MedicalServices,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray.copy(alpha = 0.5f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StatusChip(
    status: ConsultationStatus,
    isTeluguMode: Boolean,
) {
    val (text, color) =
        when (status) {
            ConsultationStatus.PENDING ->
                (if (isTeluguMode) "వేచి ఉంది" else "Pending") to Color(0xFFFFA726)

            ConsultationStatus.ACTIVE ->
                (if (isTeluguMode) "కొనసాగుతోంది" else "Active") to Color(0xFF66BB6A)

            ConsultationStatus.COMPLETED ->
                (if (isTeluguMode) "పూర్తయింది" else "Completed") to Color(0xFF43A047)

            ConsultationStatus.CANCELLED ->
                (if (isTeluguMode) "రద్దయింది" else "Cancelled") to Color(0xFFE53935)

            ConsultationStatus.SCHEDULED ->
                (if (isTeluguMode) "షెడ్యూల్ అయింది" else "Scheduled") to Color(0xFF8E24AA)
        }

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f),
            ),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ConsultationBookingDialog(
    vet: VetProfile,
    isTeluguMode: Boolean,
    onConfirm: (ConsultationType) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedType by remember { mutableStateOf(ConsultationType.AI_CHAT) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
            ) {
                Text(
                    text = if (isTeluguMode) "సంప్రదింపు రకం ఎంచుకోండి" else "Select Consultation Type",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Consultation type options
                val consultationTypes =
                    listOf(
                        ConsultationType.AI_CHAT to (if (isTeluguMode) "AI చాట్" else "AI Chat"),
                        ConsultationType.TEXT_CHAT to (if (isTeluguMode) "టెక్స్ట్ చాట్" else "Text Chat"),
                        ConsultationType.VOICE_CALL to (if (isTeluguMode) "వాయిస్ కాల్" else "Voice Call"),
                        ConsultationType.VIDEO_CALL to (if (isTeluguMode) "వీడియో కాల్" else "Video Call"),
                    )

                consultationTypes.forEach { (type, label) ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { selectedType = type }
                                .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            colors =
                                RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFFF5722),
                                ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = if (isTeluguMode) "రద్దు చేయండి" else "Cancel",
                            color = Color.Gray,
                        )
                    }

                    Button(
                        onClick = { onConfirm(selectedType) },
                        modifier = Modifier.weight(1f),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5722),
                            ),
                    ) {
                        Text(
                            text = if (isTeluguMode) "బుక్ చేయండి" else "Book",
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

// ===============================
// HELPER FUNCTIONS
// ===============================

data class VetChatMessage(
    val id: String,
    val content: String,
    val timestamp: Date,
    val isFromUser: Boolean,
)

private suspend fun sendAIMessage(
    message: String,
    fowlType: String,
    isTeluguMode: Boolean,
    chatMessages: List<VetChatMessage>,
    onUpdateMessages: (List<VetChatMessage>) -> Unit,
    onLoading: (Boolean) -> Unit,
) {
    // Add user message
    val userMessage =
        VetChatMessage(
            id = "user_${System.currentTimeMillis()}",
            content = message,
            timestamp = Date(),
            isFromUser = true,
        )

    onUpdateMessages(chatMessages + userMessage)
    onLoading(true)

    // Simulate AI processing delay
    delay(2000)

    // Fetch AI health tips
    fetchAIHealthTips(
        symptoms = message,
        fowlType = fowlType,
        onResult = { healthTips ->
            onLoading(false)
            if (healthTips.isNotEmpty()) {
                val aiResponse = healthTips.first()
                val aiMessage =
                    VetChatMessage(
                        id = "ai_${System.currentTimeMillis()}",
                        content = "${aiResponse.title}\n\n${aiResponse.description}\n\n${if (isTeluguMode) "సిఫార్సు చర్య:" else "Recommended Action:"} ${aiResponse.recommendedAction}",
                        timestamp = Date(),
                        isFromUser = false,
                    )
                onUpdateMessages(chatMessages + userMessage + aiMessage)
            } else {
                val fallbackMessage =
                    VetChatMessage(
                        id = "ai_${System.currentTimeMillis()}",
                        content =
                            if (isTeluguMode) {
                                "క్షమించండి, ఈ లక్షణాల గురించి నాకు సమాచారం లేదు. దయచేసి వెట్ని సంప్రదించండి."
                            } else {
                                "Sorry, I don't have information about these symptoms. Please consult a veterinarian."
                            },
                        timestamp = Date(),
                        isFromUser = false,
                    )
                onUpdateMessages(chatMessages + userMessage + fallbackMessage)
            }
        },
        onError = { error ->
            onLoading(false)
            val errorMessage =
                VetChatMessage(
                    id = "ai_${System.currentTimeMillis()}",
                    content =
                        if (isTeluguMode) {
                            "లోపం జరిగింది. దయచేసి మళ్లీ ప్రయత్నించండి."
                        } else {
                            "An error occurred. Please try again."
                        },
                    timestamp = Date(),
                    isFromUser = false,
                )
            onUpdateMessages(chatMessages + userMessage + errorMessage)
        },
        setLoading = { },
    )
}
