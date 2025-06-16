package com.example.rooster.ui.traceability

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rooster.ui.components.*

/**
 * Fowl Traceability Screen - Demonstrates TraceabilityTimelineItem usage
 *
 * Features:
 * - Uses TraceabilityTimelineItem component with sample data
 * - Rural-optimized UI with clear visual hierarchy
 * - Telugu language support
 * - Visual timeline showing fowl life events
 */
@Composable
fun FowlTraceabilityScreen(
    fowlId: String,
    onEventClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onVerificationClick: (String) -> Unit,
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // Sample timeline data - in real app this would come from ViewModel
    val sampleEvents =
        remember {
            listOf(
                TraceabilityEvent(
                    id = "event1",
                    type = TraceabilityEventType.BIRTH,
                    title = "Birth Record",
                    description =
                        if (isTeluguMode) {
                            "కదక్నాథ్ కోడిపిల్ల జన్మ రికార్డ్. ఆరోగ్యకరమైన పరిస్థితుల్లో జన్మించింది."
                        } else {
                            "Kadaknath chick birth record. Born in healthy condition with no complications."
                        },
                    timestamp = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000), // 30 days ago
                    location = if (isTeluguMode) "గ్రీన్ వ్యాలీ ఫార్మ్" else "Green Valley Farm",
                    imageUrl = "https://example.com/birth.jpg",
                    verificationStatus = VerificationStatus.VERIFIED,
                    performedBy = if (isTeluguMode) "డాక్టర్ రమేష్" else "Dr. Ramesh",
                    details =
                        mapOf(
                            "Weight" to "45g",
                            "Health Score" to "9/10",
                            "Parents" to "KDF001 x KDM002",
                        ),
                ),
                TraceabilityEvent(
                    id = "event2",
                    type = TraceabilityEventType.VACCINATION,
                    title = "First Vaccination",
                    description =
                        if (isTeluguMode) {
                            "మారెక్స్ వ్యాధికి వ్యతిరేక టీకా అందించబడింది. ఎటువంటి సైడ్ ఎఫెక్ట్స్ లేవు."
                        } else {
                            "Marek's disease vaccination administered. No adverse reactions observed."
                        },
                    timestamp = System.currentTimeMillis() - (25L * 24 * 60 * 60 * 1000), // 25 days ago
                    location = if (isTeluguMode) "గ్రీన్ వ్యాలీ క్లినిక్" else "Green Valley Clinic",
                    imageUrl = "https://example.com/vaccination.jpg",
                    verificationStatus = VerificationStatus.VERIFIED,
                    performedBy = if (isTeluguMode) "వెట్ టెక్నీషియన్ సునీత" else "Vet Technician Sunitha",
                    details =
                        mapOf(
                            "Vaccine Type" to "Marek's HVT",
                            "Dosage" to "0.2ml",
                            "Batch No" to "MVT2024-001",
                        ),
                ),
                TraceabilityEvent(
                    id = "event3",
                    type = TraceabilityEventType.HEALTH_CHECK,
                    title = "Weekly Health Check",
                    description =
                        if (isTeluguMode) {
                            "వారపు ఆరోగ్య తనిఖీ పూర్తయింది. పెరుగుదల రేటు మంచిది."
                        } else {
                            "Weekly health examination completed. Growth rate is satisfactory."
                        },
                    timestamp = System.currentTimeMillis() - (18L * 24 * 60 * 60 * 1000), // 18 days ago
                    location = if (isTeluguMode) "గ్రీన్ వ్యాలీ ఫార్మ్" else "Green Valley Farm",
                    verificationStatus = VerificationStatus.VERIFIED,
                    performedBy = if (isTeluguMode) "ఫార్మ్ సూపర్‌వైజర్ అనిల్" else "Farm Supervisor Anil",
                    details =
                        mapOf(
                            "Weight" to "185g",
                            "Health Score" to "8.5/10",
                            "Activity Level" to "High",
                        ),
                ),
                TraceabilityEvent(
                    id = "event4",
                    type = TraceabilityEventType.FEEDING_CHANGE,
                    title = "Feed Transition",
                    description =
                        if (isTeluguMode) {
                            "స్టార్టర్ ఫీడ్ నుండి గ్రోవర్ ఫీడ్‌కు మారింది."
                        } else {
                            "Transitioned from starter feed to grower feed as per schedule."
                        },
                    timestamp = System.currentTimeMillis() - (14L * 24 * 60 * 60 * 1000), // 14 days ago
                    location = if (isTeluguMode) "గ్రీన్ వ్యాలీ ఫార్మ్" else "Green Valley Farm",
                    verificationStatus = VerificationStatus.VERIFIED,
                    performedBy = if (isTeluguMode) "ఫీడ్ మేనేజర్ రవి" else "Feed Manager Ravi",
                    details =
                        mapOf(
                            "Previous Feed" to "Starter Crumbles",
                            "New Feed" to "Grower Pellets",
                            "Protein %" to "18%",
                        ),
                ),
                TraceabilityEvent(
                    id = "event5",
                    type = TraceabilityEventType.VACCINATION,
                    title = "Newcastle Vaccination",
                    description =
                        if (isTeluguMode) {
                            "న్యూకాజిల్ వ్యాధి టీకా అందించబడింది. రోగనిరోధక శక్తి పెరుగుదల కోసం."
                        } else {
                            "Newcastle disease vaccination given for immunity boost."
                        },
                    timestamp = System.currentTimeMillis() - (10L * 24 * 60 * 60 * 1000), // 10 days ago
                    location = if (isTeluguMode) "గ్రీన్ వ్యాలీ క్లినిక్" else "Green Valley Clinic",
                    verificationStatus = VerificationStatus.PENDING,
                    performedBy = if (isTeluguMode) "డాక్టర్ ప్రియ" else "Dr. Priya",
                    details =
                        mapOf(
                            "Vaccine Type" to "Newcastle La Sota",
                            "Route" to "Eye Drop",
                            "Follow-up" to "Required in 7 days",
                        ),
                ),
                TraceabilityEvent(
                    id = "event6",
                    type = TraceabilityEventType.MILESTONE,
                    title = "Growth Milestone",
                    description =
                        if (isTeluguMode) {
                            "6 వారాల మైలురాయిని చేరుకుంది. అంచనా కంటే మంచి పెరుగుదల."
                        } else {
                            "Reached 6-week milestone with excellent growth performance."
                        },
                    timestamp = System.currentTimeMillis() - (3L * 24 * 60 * 60 * 1000), // 3 days ago
                    location = if (isTeluguMode) "గ్రీన్ వ్యాలీ ఫార్మ్" else "Green Valley Farm",
                    imageUrl = "https://example.com/milestone.jpg",
                    verificationStatus = VerificationStatus.VERIFIED,
                    performedBy = if (isTeluguMode) "ఫార్మ్ మేనేజర్ రాజేష్" else "Farm Manager Rajesh",
                    details =
                        mapOf(
                            "Current Weight" to "650g",
                            "Expected Weight" to "580g",
                            "Growth Rate" to "Above Average",
                        ),
                ),
            )
        }

    StandardScreenLayout(
        topBar = {
            FowlTraceabilityTopBar(
                fowlId = fowlId,
                isTeluguMode = isTeluguMode,
            )
        },
        scrollable = false,
        modifier = modifier,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp), // No spacing for timeline continuity
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = sampleEvents,
                key = { _, event -> event.id },
            ) { index, event ->
                TraceabilityTimelineItem(
                    event = event,
                    isFirst = index == 0,
                    isLast = index == sampleEvents.lastIndex,
                    onEventClick = { onEventClick(event.id) },
                    onImageClick =
                        if (event.imageUrl.isNotEmpty()) {
                            { onImageClick(event.imageUrl) }
                        } else {
                            null
                        },
                    onVerificationClick = { onVerificationClick(event.id) },
                    isTeluguMode = isTeluguMode,
                    showFullDetails = true,
                )
            }
        }
    }
}

/**
 * Custom Top App Bar for Fowl Traceability
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FowlTraceabilityTopBar(
    fowlId: String,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = if (isTeluguMode) "కోడి చరిత్ర" else "Fowl Timeline",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "ID: $fowlId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* Handle back navigation */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = if (isTeluguMode) "వెనుకకు" else "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Handle share timeline */ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = if (isTeluguMode) "పంచుకోండి" else "Share",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        modifier = modifier,
    )
}
