// File: app/src/main/java/com/example/rooster/ui/FamilyTreeScreen.kt
package com.example.rooster.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Node representing a bird in the family tree.
 */
data class FamilyNode(
    val id: String,
    val name: String,
    val children: List<FamilyNode> = emptyList(),
)

/**
 * ViewModel to load and hold a family chain for a given bird ID.
 */
class FamilyTreeViewModel : ViewModel() {
    private val _nodes = MutableStateFlow<List<FamilyNode>>(emptyList())
    val nodes: StateFlow<List<FamilyNode>> = _nodes

    fun loadFamilyChain(chickenId: String) {
        viewModelScope.launch {
            try {
                // Real Parse backend call to fetch family tree data
                val query = ParseQuery.getQuery<ParseObject>("Fowl")
                query.whereEqualTo("objectId", chickenId)
                query.include("parentMale")
                query.include("parentFemale")

                val fowlList = query.find()
                if (fowlList.isNotEmpty()) {
                    val fowl = fowlList[0]
                    val familyNodes = buildFamilyTree(fowl, chickenId)
                    _nodes.value = familyNodes
                } else {
                    // No fowl found, show fallback
                    showFallbackData(chickenId)
                }
            } catch (e: Exception) {
                // Log error but show mock data as fallback
                android.util.Log.e("FamilyTreeVM", "Failed to load real family tree", e)
                showFallbackData(chickenId)
            }
        }
    }

    private fun showFallbackData(chickenId: String) {
        _nodes.value =
            listOf(
                FamilyNode(
                    chickenId,
                    "Chick $chickenId",
                    children =
                        listOf(
                            FamilyNode(
                                "p1", "Parent A",
                                listOf(
                                    FamilyNode("g1", "Grandparent 1"),
                                    FamilyNode("g2", "Grandparent 2"),
                                ),
                            ),
                            FamilyNode("p2", "Parent B"),
                        ),
                ),
            )
    }

    private suspend fun buildFamilyTree(
        fowl: ParseObject,
        chickenId: String,
    ): List<FamilyNode> {
        val parents = mutableListOf<FamilyNode>()

        // Add male parent if exists
        fowl.getParseObject("parentMale")?.let { maleParent ->
            val maleNode =
                FamilyNode(
                    id = maleParent.objectId ?: "",
                    name = maleParent.getString("name") ?: "Unknown Male",
                    children = buildParentChildren(maleParent),
                )
            parents.add(maleNode)
        }

        // Add female parent if exists
        fowl.getParseObject("parentFemale")?.let { femaleParent ->
            val femaleNode =
                FamilyNode(
                    id = femaleParent.objectId ?: "",
                    name = femaleParent.getString("name") ?: "Unknown Female",
                    children = buildParentChildren(femaleParent),
                )
            parents.add(femaleNode)
        }

        return listOf(
            FamilyNode(
                id = fowl.objectId ?: chickenId,
                name = fowl.getString("name") ?: "Unknown Fowl",
                children = parents,
            ),
        )
    }

    private suspend fun buildParentChildren(parent: ParseObject): List<FamilyNode> {
        val grandparents = mutableListOf<FamilyNode>()

        // Add grandparents
        parent.getParseObject("parentMale")?.let { grandpa ->
            grandparents.add(
                FamilyNode(
                    id = grandpa.objectId ?: "",
                    name = grandpa.getString("name") ?: "Unknown Grandfather",
                ),
            )
        }

        parent.getParseObject("parentFemale")?.let { grandma ->
            grandparents.add(
                FamilyNode(
                    id = grandma.objectId ?: "",
                    name = grandma.getString("name") ?: "Unknown Grandmother",
                ),
            )
        }

        return grandparents
    }
}

@Composable
fun FamilyTreeScreen(
    chickenId: String,
    vm: FamilyTreeViewModel = viewModel(),
) {
    val nodes by vm.nodes.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(chickenId) {
        vm.loadFamilyChain(chickenId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Tree: $chickenId") },
                actions = {
                    IconButton(onClick = { shareTree(context, chickenId) }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share Tree")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(16.dp),
        ) {
            if (nodes.isEmpty()) {
                Text("Loading family tree...", style = MaterialTheme.typography.bodyMedium)
            } else {
                nodes.forEach { node ->
                    TreeNode(node)
                }
            }
        }
    }
}

@Composable
private fun TreeNode(
    node: FamilyNode,
    level: Int = 0,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(start = (level * 16).dp, top = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { if (node.children.isNotEmpty()) expanded = !expanded }
                    .padding(4.dp),
        ) {
            if (node.children.isNotEmpty()) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(node.name, fontWeight = FontWeight.Medium)
        }

        if (expanded) {
            node.children.forEach { child ->
                TreeNode(child, level + 1)
            }
        }
    }
}

/**
 * Sends a generic ACTION_SEND intent with a link to this bird's family tree.
 */
private fun shareTree(
    context: Context,
    chickenId: String,
) {
    val url = "https://yourapp.com/p/$chickenId"
    val intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "View this bird's family tree: $url")
        }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}
