package com.example.rooster.professionaltools.educationalcontent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- Data Classes ---
data class EducationalContentItem(
    val contentId: String,
    val title: String,
    val contentType: String, // Article, Video, FAQ, Guide, Webinar Recording
    val speciesCategory: String,
    val topicCategory: String,
    val author: String,
    val publishDate: Date,
    var lastUpdatedDate: Date,
    val bodyOrUrl: String, // Full text for Article/FAQ, URL for Video/Guide/Webinar
    val keywords: List<String>,
    val accessLevel: String, // public, registered_users, vets_only
    var viewCount: Int = 0,
    val uploadedBy: String? = null
) {
    fun getFormattedDate(date: Date?): String =
        date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "N/A"
}

data class ContentFilters(
    val query: String? = null,
    val species: String? = null,
    val topic: String? = null,
    val type: String? = null
)

// --- ViewModel ---
class EducationalContentViewModel : ViewModel() {
    private val _allContent = MutableStateFlow<List<EducationalContentItem>>(emptyList())
    private val _filters = MutableStateFlow(ContentFilters())
    val filters: StateFlow<ContentFilters> = _filters

    val filteredContent: StateFlow<List<EducationalContentItem>> = combine(_allContent, _filters) { content, filters ->
        content.filter { item ->
            val userAccessLevel = currentSimulatedUserType.value // Use simulated user type for access control
            val itemAccess = item.accessLevel
            val accessible = when (itemAccess) {
                "public" -> true
                "registered_users" -> userAccessLevel != "guest"
                "vets_only" -> userAccessLevel == "vet"
                else -> true // Default to accessible if unknown
            }

            accessible &&
            (filters.query.isNullOrBlank() || item.title.contains(filters.query, ignoreCase = true) || item.keywords.any{ it.contains(filters.query, ignoreCase = true)}) &&
            (filters.species.isNullOrBlank() || item.speciesCategory.equals(filters.species, ignoreCase = true)) &&
            (filters.topic.isNullOrBlank() || item.topicCategory.equals(filters.topic, ignoreCase = true)) &&
            (filters.type.isNullOrBlank() || item.contentType.equals(filters.type, ignoreCase = true))
        }.sortedByDescending { it.publishDate }
    }.stateIn(kotlinx.coroutines.MainScope(), kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedContent = MutableStateFlow<EducationalContentItem?>(null)
    val selectedContent: StateFlow<EducationalContentItem?> = _selectedContent

    // Mock data for UI elements
    val speciesCategories = listOf("Any", "General", "Cattle", "Poultry", "Swine", "Horses", "Companion Animals")
    val topicCategories = listOf("Any", "Nutrition", "Disease Prevention", "Common Diseases", "Farm Management", "Biosecurity")
    val contentTypes = listOf("Any", "Article", "Video", "FAQ", "Guide")
    val accessLevels = listOf("public", "registered_users", "vets_only")
    val authors = listOf("Dr. Vet Expert", "AgriConsult Inc.", "University Extension")

    // Simulate current user type for access control demo
    val currentSimulatedUserType = MutableStateFlow("vet") // "guest", "farmer", "vet"


    init {
        loadMockContent()
    }

    private fun loadMockContent(count: Int = 10) {
        val tempContent = mutableListOf<EducationalContentItem>()
        for (i in 0 until count) {
            val type = contentTypes.filterNot { it == "Any" }.random()
            val species = speciesCategories.filterNot { it == "Any" }.random()
            val topic = topicCategories.filterNot { it == "Any" }.random()
            val body = if (type == "Article" || type == "FAQ") "This is the main body for '$type' about $topic in $species. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." else "https://example.com/content/${type.lowercase()}/$i"
            tempContent.add(
                EducationalContentItem(
                    contentId = "edu_${System.currentTimeMillis()}_$i",
                    title = "${if(type == "FAQ") "Q&A" else "Guide to"} $topic for $species",
                    contentType = type, speciesCategory = species, topicCategory = topic,
                    author = authors.random(),
                    publishDate = Date(System.currentTimeMillis() - Random.nextLong(1, 365) * 86400000L),
                    lastUpdatedDate = Date(System.currentTimeMillis() - Random.nextLong(0, 30) * 86400000L),
                    bodyOrUrl = body,
                    keywords = listOf(species, topic, type),
                    accessLevel = accessLevels.random(),
                    uploadedBy = "admin_system"
                )
            )
        }
        _allContent.value = tempContent
    }

    fun updateFilters(newFilters: ContentFilters) {
        _filters.value = newFilters
    }

    fun selectContent(contentId: String) {
        _selectedContent.value = _allContent.value.find { it.contentId == contentId }?.also {
            it.viewCount++ // This won't trigger recomposition of the list item directly, fine for this mock.
             _allContent.update { list -> list.map { c -> if(c.contentId == contentId) c.copy(viewCount = c.viewCount) else c } }
        }
    }
    fun clearSelectedContent() {
        _selectedContent.value = null
    }

    fun addContent(title: String, type: String, species: String, topic: String, author: String, body: String, keywords: List<String>, access: String, uploader: String) {
        val newItem = EducationalContentItem(
            contentId = "edu_new_${System.currentTimeMillis()}", title = title, contentType = type, speciesCategory = species, topicCategory = topic,
            author = author, publishDate = Date(), lastUpdatedDate = Date(), bodyOrUrl = body, keywords = keywords, accessLevel = access, uploadedBy = uploader
        )
        _allContent.update { (listOf(newItem) + it) }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalContentScreen(viewModel: EducationalContentViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val contentList by viewModel.filteredContent.collectAsState()
    val selectedContent by viewModel.selectedContent.collectAsState()
    val currentFilters by viewModel.filters.collectAsState()
    val currentUserType by viewModel.currentSimulatedUserType.collectAsState()


    var showFilterDialog by remember { mutableStateOf(false) }
    var showAddContentDialog by remember { mutableStateOf(false) }

    if (showFilterDialog) {
        FilterContentDialog(currentFilters, viewModel, onDismiss = { showFilterDialog = false })
    }
    if (showAddContentDialog) {
        AddContentDialog(viewModel, onDismiss = { showAddContentDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedContent == null) "Educational Content" else selectedContent!!.title) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5)), // Indigo
                navigationIcon = if (selectedContent != null) {
                    { IconButton(onClick = { viewModel.clearSelectedContent() }) { Icon(Icons.Filled.ArrowBack, "Back") } }
                } else null,
                actions = {
                    if (selectedContent == null) { // Actions for list view
                        IconButton(onClick = { showFilterDialog = true }) { Icon(Icons.Filled.Search, "Filter Content") }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedContent == null && currentUserType == "vet") { // Allow vets to add content
                FloatingActionButton(onClick = { showAddContentDialog = true }) {
                    Icon(Icons.Filled.Add, "Add New Content")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp)) {
            // User type switcher for demo purposes
            if (selectedContent == null) { // Show only on list view
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("Simulate User: ", style = MaterialTheme.typography.labelMedium)
                    viewModel.accessLevels.forEach { type -> // Using accessLevels as user types for demo
                        Button(
                            onClick = { viewModel.currentSimulatedUserType.value = type },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentUserType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) { Text(type.replaceFirstChar { it.titlecase() }) }
                    }
                }
                Divider()
            }

            if (selectedContent == null) {
                EducationalContentList(contentList, onContentClick = { viewModel.selectContent(it.contentId) })
            } else {
                EducationalContentDetail(selectedContent!!)
            }
        }
    }
}

@Composable
fun EducationalContentList(list: List<EducationalContentItem>, onContentClick: (EducationalContentItem) -> Unit) {
    if (list.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No content matches your criteria or access level.")
        }
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
        items(list, key = { it.contentId }) { item ->
            ContentListItem(item, onClick = { onContentClick(item) })
        }
    }
}

@Composable
fun ContentListItem(item: EducationalContentItem, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium)
            Text("Type: ${item.contentType}, Topic: ${item.topicCategory}, Species: ${item.speciesCategory}", style = MaterialTheme.typography.bodySmall)
            Text("Author: ${item.author}, Published: ${item.getFormattedDate(item.publishDate)}", style = MaterialTheme.typography.bodySmall)
            Text("Views: ${item.viewCount}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EducationalContentDetail(content: EducationalContentItem) {
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    Column(Modifier.fillMaxSize().verticalScroll(scrollState).padding(vertical = 8.dp)) {
        Text(content.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Author: ${content.author}", style = MaterialTheme.typography.titleSmall)
        Text("Published: ${content.getFormattedDate(content.publishDate)} | Updated: ${content.getFormattedDate(content.lastUpdatedDate)}", style = MaterialTheme.typography.bodySmall)
        Text("Type: ${content.contentType} | Topic: ${content.topicCategory} | Species: ${content.speciesCategory}", style = MaterialTheme.typography.bodySmall)
        Text("Keywords: ${content.keywords.joinToString()}", style = MaterialTheme.typography.bodySmall)
        Text("Access: ${content.accessLevel}", style = MaterialTheme.typography.bodySmall)
        Text("Views: ${content.viewCount}", style = MaterialTheme.typography.bodySmall)
        Divider(modifier = Modifier.padding(vertical = 12.dp))

        if (content.contentType == "Article" || content.contentType == "FAQ") {
            Text(content.bodyOrUrl, style = MaterialTheme.typography.bodyLarge)
        } else { // Video, Guide, Webinar
            Text("Content URL (click to open if valid):", style = MaterialTheme.typography.labelLarge)
            TextButton(onClick = { try { uriHandler.openUri(content.bodyOrUrl) } catch (e: Exception) { /* Handle error */ }}) {
                Text(content.bodyOrUrl, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun FilterContentDialog(currentFilters: ContentFilters, viewModel: EducationalContentViewModel, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf(currentFilters.query ?: "") }
    var species by remember { mutableStateOf(currentFilters.species ?: viewModel.speciesCategories.first()) }
    var topic by remember { mutableStateOf(currentFilters.topic ?: viewModel.topicCategories.first()) }
    var type by remember { mutableStateOf(currentFilters.type ?: viewModel.contentTypes.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Content") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Search Query") })
                ExposedDropdownMenuForOptions("Species Category", viewModel.speciesCategories, species) { species = it }
                ExposedDropdownMenuForOptions("Topic Category", viewModel.topicCategories, topic) { topic = it }
                ExposedDropdownMenuForOptions("Content Type", viewModel.contentTypes, type) { type = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.updateFilters(ContentFilters(
                    query = query.ifBlank { null },
                    species = if (species == "Any") null else species,
                    topic = if (topic == "Any") null else topic,
                    type = if (type == "Any") null else type
                ))
                onDismiss()
            }) { Text("Apply Filters") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddContentDialog(viewModel: EducationalContentViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(viewModel.contentTypes.filterNot { it == "Any" }.first()) }
    var species by remember { mutableStateOf(viewModel.speciesCategories.filterNot { it == "Any" }.first()) }
    var topic by remember { mutableStateOf(viewModel.topicCategories.filterNot { it == "Any" }.first()) }
    var author by remember { mutableStateOf(viewModel.authors.first()) }
    var bodyOrUrl by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("") } // comma-separated
    var access by remember { mutableStateOf(viewModel.accessLevels.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Educational Content") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = title, onValueChange = {title=it}, label = {Text("Title")}) }
                item { ExposedDropdownMenuForOptions("Content Type", viewModel.contentTypes.filterNot{it=="Any"}, type) {type=it} }
                item { ExposedDropdownMenuForOptions("Species", viewModel.speciesCategories.filterNot{it=="Any"}, species) {species=it} }
                item { ExposedDropdownMenuForOptions("Topic", viewModel.topicCategories.filterNot{it=="Any"}, topic) {topic=it} }
                item { ExposedDropdownMenuForOptions("Author", viewModel.authors, author) {author=it} }
                item { OutlinedTextField(value = bodyOrUrl, onValueChange = {bodyOrUrl=it}, label = {Text("Body (for Article/FAQ) or URL")}, minLines = 3) }
                item { OutlinedTextField(value = keywords, onValueChange = {keywords=it}, label = {Text("Keywords (comma-separated)")}) }
                item { ExposedDropdownMenuForOptions("Access Level", viewModel.accessLevels, access) {access=it} }
            }
        },
        confirmButton = { Button(onClick = {
            if(title.isNotBlank() && bodyOrUrl.isNotBlank()){
                viewModel.addContent(title,type,species,topic,author,bodyOrUrl,keywords.split(",").map(String::trim).filter(String::isNotBlank),access,"vet_currentUser")
                onDismiss()
            }
        }, enabled = title.isNotBlank() && bodyOrUrl.isNotBlank()) {Text("Add Content")} },
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")} }
    )
}

// Re-using ExposedDropdownMenuForOptions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuForOptions(
    label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onOptionSelected(option); expanded = false }) }
        }
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
fun PreviewEducationalContentScreen_List() {
    MaterialTheme { EducationalContentScreen(viewModel = EducationalContentViewModel()) }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
fun PreviewEducationalContentScreen_Detail() {
    val viewModel = EducationalContentViewModel()
    LaunchedEffect(Unit) {
        if(viewModel.filteredContent.value.isNotEmpty()) {
            viewModel.selectContent(viewModel.filteredContent.value.first().contentId)
        }
    }
    MaterialTheme { EducationalContentScreen(viewModel = viewModel) }
}
