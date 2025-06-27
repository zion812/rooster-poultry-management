package com.example.rooster.feature.farm.ui.lineage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.feature.farm.domain.model.LineageNode
import com.example.rooster.feature.farm.domain.model.FlockType
import com.example.rooster.feature.farm.domain.model.LineageInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlockLineageScreen(
    viewModel: FlockLineageViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onFlockClick: (flockId: String) -> Unit // To navigate to another flock's detail/lineage
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flock Lineage") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is LineageUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is LineageUiState.Success -> {
                    LineageDetailContent(
                        lineageInfo = state.lineageInfo,
                        onFlockClick = onFlockClick
                    )
                }
                is LineageUiState.NoLineageData -> {
                     Text(
                        text = "No lineage data available for this flock.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is LineageUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun LineageDetailContent(
    lineageInfo: LineageInfo,
    onFlockClick: (flockId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Lineage for: ${lineageInfo.centralFlockNode.name} (${lineageInfo.centralFlockId})",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Ancestors (Up to ${lineageInfo.generationDepthUp} generations):", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        DisplayNodeAndParents(node = lineageInfo.centralFlockNode, onFlockClick = onFlockClick, indentLevel = 0, maxDepth = lineageInfo.generationDepthUp)

        Spacer(modifier = Modifier.height(24.dp))
        Text("Descendants (Up to ${lineageInfo.generationDepthDown} generations):", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        DisplayNodeAndChildren(node = lineageInfo.centralFlockNode, onFlockClick = onFlockClick, indentLevel = 0, maxDepth = lineageInfo.generationDepthDown)
    }
}

@Composable
fun DisplayNodeAndParents(
    node: LineageNode?,
    onFlockClick: (flockId: String) -> Unit,
    indentLevel: Int,
    maxDepth: Int,
    isParentType: String? = null // "Father" or "Mother"
) {
    if (node == null || indentLevel > maxDepth) return // Stop if no node or max depth exceeded for this path

    Row(modifier = Modifier.padding(start = (indentLevel * 24).dp)) {
        isParentType?.let { Text("$it: ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) }
        TextButton(onClick = { onFlockClick(node.flockId) }) {
             Text(
                "${node.name} (${node.flockId.take(6)}... - ${node.type.name.capitalizeWord()})",
                fontSize = 15.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (indentLevel < maxDepth) {
        DisplayNodeAndParents(node.father, onFlockClick, indentLevel + 1, maxDepth, "Father")
        DisplayNodeAndParents(node.mother, onFlockClick, indentLevel + 1, maxDepth, "Mother")
    }
}

@Composable
fun DisplayNodeAndChildren(
    node: LineageNode?,
    onFlockClick: (flockId: String) -> Unit,
    indentLevel: Int,
    maxDepth: Int
) {
    if (node == null || indentLevel > maxDepth) return

    if (indentLevel > 0) { // Don't re-display the central node if it's the starting point for children display
        Row(modifier = Modifier.padding(start = (indentLevel * 24).dp)) {
            TextButton(onClick = { onFlockClick(node.flockId) }) {
                 Text(
                    "${node.name} (${node.flockId.take(6)}... - ${node.type.name.capitalizeWord()})",
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (indentLevel < maxDepth) {
        node.children.forEach { child ->
            DisplayNodeAndChildren(child, onFlockClick, indentLevel + 1, maxDepth)
        }
    }
}

fun String.capitalizeWord(): String = this.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }


@Preview(showBackground = true)
@Composable
fun PreviewLineageDetailContent() {
    val greatGrandParentF = LineageNode("ggf1", "GreatGrandPa F", "BreedX", FlockType.ROOSTER)
    val grandParentM = LineageNode("gpm1", "GrandMa M", "BreedX", FlockType.HEN)
    val grandParentF = LineageNode("gpf1", "GrandPa F", "BreedX", FlockType.ROOSTER, father = greatGrandParentF)
    val parentM = LineageNode("pm1", "Mother Hen", "BreedY", FlockType.HEN, mother = grandParentM)
    val parentF = LineageNode("pf1", "Father Rooster", "BreedX", FlockType.ROOSTER, father = grandParentF)
    val child1 = LineageNode("c1", "Chick Alpha", "BreedY", FlockType.CHICK)
    val child2 = LineageNode("c2", "Chick Beta", "BreedY", FlockType.CHICK)
    val central = LineageNode(
        flockId = "central1", name = "Champion Cock", breed = "BreedZ", type = FlockType.ROOSTER,
        father = parentF, mother = parentM, children = listOf(child1, child2)
    )
    val sampleLineageInfo = LineageInfo(
        centralFlockId = "central1",
        centralFlockNode = central,
        generationDepthUp = 2,
        generationDepthDown = 1
    )

    MaterialTheme {
        LineageDetailContent(lineageInfo = sampleLineageInfo, onFlockClick = {})
    }
}
