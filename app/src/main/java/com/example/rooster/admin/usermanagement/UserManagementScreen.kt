package com.example.rooster.admin.usermanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Data Classes ---
data class UserProfile(
    val name: String,
    val details: Map<String, String> // e.g., specialty, farm_size
)

data class UserData(
    val id: String,
    val email: String,
    var isVerified: Boolean,
    var roles: List<String>,
    var accountStatus: String,
    val lastLogin: Date?,
    val profile: UserProfile?
) {
    fun getFormattedLastLogin(): String {
        return lastLogin?.let {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it)
        } ?: "Never"
    }
}

// --- ViewModel ---
class UserManagementViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<UserData>>(emptyList())
    val users: StateFlow<List<UserData>> = _users

    init {
        loadMockUsers()
    }

    private fun loadMockUsers() {
        _users.value = listOf(
            UserData(
                "user001", "alice@example.com", true, listOf("veterinarian", "admin"), "active",
                Date(System.currentTimeMillis() - 2 * 3600000),
                UserProfile("Dr. Alice Smith", mapOf("specialty" to "General Practice"))
            ),
            UserData(
                "user002", "bob@example.com", false, listOf("farmer"), "pending_verification",
                null,
                UserProfile("Bob Johnson", mapOf("farm_size" to "100 acres"))
            ),
            UserData(
                "user003", "charlie@example.com", true, listOf("farmer", "seller"), "active",
                Date(System.currentTimeMillis() - 24 * 3600000),
                UserProfile("Charlie Brown", mapOf("store_name" to "Brown's Farm Produce"))
            ),
            UserData(
                "user004", "diana@example.com", true, listOf("veterinarian"), "suspended",
                Date(System.currentTimeMillis() - 7 * 24 * 3600000),
                UserProfile("Dr. Diana Prince", mapOf("specialty" to "Surgery"))
            )
        )
    }

    fun verifyUser(userId: String) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.id == userId) {
                    user.copy(isVerified = true, accountStatus = "active")
                } else user
            }
        }
    }

    fun assignRole(userId: String, role: String) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.id == userId && !user.roles.contains(role)) {
                    user.copy(roles = user.roles + role)
                } else user
            }
        }
    }

    fun revokeRole(userId: String, role: String) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.id == userId && user.roles.contains(role)) {
                    user.copy(roles = user.roles - role)
                } else user
            }
        }
    }

    fun changeAccountStatus(userId: String, newStatus: String) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.id == userId) {
                    user.copy(accountStatus = newStatus)
                } else user
            }
        }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(viewModel: UserManagementViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val users by viewModel.users.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Cyan) // Example color
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(users) { user ->
                UserCard(user, viewModel)
            }
        }
    }
}

@Composable
fun UserCard(user: UserData, viewModel: UserManagementViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var actionType by remember { mutableStateOf("") } // "assignRole", "revokeRole", "changeStatus"
    var inputValue by remember { mutableStateOf("") }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Perform Action on ${user.email}") },
            text = {
                Column {
                    Text("Current Status: ${user.accountStatus}")
                    Text("Current Roles: ${user.roles.joinToString()}")
                    if (actionType == "assignRole" || actionType == "revokeRole") {
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text("Role Name") }
                        )
                    } else if (actionType == "changeStatus") {
                         OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text("New Status (e.g., active, suspended)") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    when (actionType) {
                        "assignRole" -> viewModel.assignRole(user.id, inputValue)
                        "revokeRole" -> viewModel.revokeRole(user.id, inputValue)
                        "changeStatus" -> viewModel.changeAccountStatus(user.id, inputValue)
                    }
                    showDialog = false
                    inputValue = ""
                }) { Text("Confirm") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${user.id}", style = MaterialTheme.typography.titleMedium)
            Text("Email: ${user.email}")
            Text("Verified: ${if (user.isVerified) "Yes" else "No"}", color = if (user.isVerified) Color.Green else Color.Red)
            Text("Roles: ${user.roles.joinToString(", ")}")
            Text("Status: ${user.accountStatus}", color = when(user.accountStatus){
                "active" -> Color.Green
                "pending_verification" -> Color.Blue
                "suspended" -> Color.Magenta
                else -> Color.Gray
            })
            Text("Last Login: ${user.getFormattedLastLogin()}")
            user.profile?.let { profile ->
                Text("Profile Name: ${profile.name}")
                profile.details.forEach { (key, value) ->
                    Text("${key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}: $value")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (!user.isVerified) {
                    Button(onClick = { viewModel.verifyUser(user.id) }) {
                        Text("Verify")
                    }
                }
                IconButton(onClick = { actionType = "assignRole"; inputValue = ""; showDialog = true }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Assign Role")
                }
                IconButton(onClick = { actionType = "revokeRole"; inputValue = ""; showDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Revoke Role")
                }
                 IconButton(onClick = { actionType = "changeStatus"; inputValue = user.accountStatus; showDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Change Status")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserManagementScreen() {
    MaterialTheme { // Wrap in MaterialTheme for preview
        UserManagementScreen(viewModel = UserManagementViewModel())
    }
}
package com.example.rooster.admin.usermanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Data Classes ---
data class UserProfile(
    val name: String,
    val details: Map<String, String> // e.g., specialty, farm_size
)

data class UserData(
    val id: String,
    val email: String,
    var isVerified: Boolean,
    var roles: List<String>,
    var accountStatus: String,
    val lastLogin: Date?,
    val profile: UserProfile?
) {
    fun getFormattedLastLogin(): String {
        return lastLogin?.let {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it)
        } ?: "Never"
    }
}

// --- ViewModel ---
class UserManagementViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<UserData>>(emptyList())
    val users: StateFlow<List<UserData>> = _users

    init {
        loadMockUsers()
    }

    private fun loadMockUsers() {
        _users.value = listOf(
            UserData(
                "user001", "alice@example.com", true, listOf("veterinarian", "admin"), "active",
                Date(System.currentTimeMillis() - 2 * 3600000),
                UserProfile("Dr. Alice Smith", mapOf("specialty" to "General Practice"))
            ),
            UserData(
                "user002", "bob@example.com", false, listOf("farmer"), "pending_verification",
                null,
                UserProfile("Bob Johnson", mapOf("farm_size" to "100 acres"))
            ),
            UserData(
                "user003", "charlie@example.com", true, listOf("farmer", "seller"), "active",
                Date(System.currentTimeMillis() - 24 * 3600000),
                UserProfile("Charlie Brown", mapOf("store_name" to "Brown's Farm Produce"))
            ),
            UserData(
                "user004", "diana@example.com", true, listOf("veterinarian"), "suspended",
                Date(System.currentTimeMillis() - 7 * 24 * 3600000),
                UserProfile("Dr. Diana Prince", mapOf("specialty" to "Surgery"))
            )
        )
    }

    fun verifyUser(userId: String) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.id == userId) {
                    user.copy(isVerified = true, accountStatus = "active")
                } else user
            }
        }
    }

    fun assignRole(userId: String, role: String) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.id == userId && !user.roles.contains(role)) {
                    user.copy(roles = user.roles + role)
                } else user
            }
        }
    }

    fun revokeRole(userId: String, role: String) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.id == userId && user.roles.contains(role)) {
                    user.copy(roles = user.roles - role)
                } else user
            }
        }
    }

    fun changeAccountStatus(userId: String, newStatus: String) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.id == userId) {
                    user.copy(accountStatus = newStatus)
                } else user
            }
        }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(viewModel: UserManagementViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val users by viewModel.users.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Cyan) // Example color
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(users) { user ->
                UserCard(user, viewModel)
            }
        }
    }
}

@Composable
fun UserCard(user: UserData, viewModel: UserManagementViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var actionType by remember { mutableStateOf("") } // "assignRole", "revokeRole", "changeStatus"
    var inputValue by remember { mutableStateOf("") }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Perform Action on ${user.email}") },
            text = {
                Column {
                    Text("Current Status: ${user.accountStatus}")
                    Text("Current Roles: ${user.roles.joinToString()}")
                    if (actionType == "assignRole" || actionType == "revokeRole") {
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text("Role Name") }
                        )
                    } else if (actionType == "changeStatus") {
                         OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text("New Status (e.g., active, suspended)") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    when (actionType) {
                        "assignRole" -> viewModel.assignRole(user.id, inputValue)
                        "revokeRole" -> viewModel.revokeRole(user.id, inputValue)
                        "changeStatus" -> viewModel.changeAccountStatus(user.id, inputValue)
                    }
                    showDialog = false
                    inputValue = ""
                }) { Text("Confirm") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${user.id}", style = MaterialTheme.typography.titleMedium)
            Text("Email: ${user.email}")
            Text("Verified: ${if (user.isVerified) "Yes" else "No"}", color = if (user.isVerified) Color.Green else Color.Red)
            Text("Roles: ${user.roles.joinToString(", ")}")
            Text("Status: ${user.accountStatus}", color = when(user.accountStatus){
                "active" -> Color.Green
                "pending_verification" -> Color.Blue
                "suspended" -> Color.Magenta
                else -> Color.Gray
            })
            Text("Last Login: ${user.getFormattedLastLogin()}")
            user.profile?.let { profile ->
                Text("Profile Name: ${profile.name}")
                profile.details.forEach { (key, value) ->
                    Text("${key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}: $value")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (!user.isVerified) {
                    Button(onClick = { viewModel.verifyUser(user.id) }) {
                        Text("Verify")
                    }
                }
                IconButton(onClick = { actionType = "assignRole"; inputValue = ""; showDialog = true }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Assign Role")
                }
                IconButton(onClick = { actionType = "revokeRole"; inputValue = ""; showDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Revoke Role")
                }
                 IconButton(onClick = { actionType = "changeStatus"; inputValue = user.accountStatus; showDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Change Status")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserManagementScreen() {
    MaterialTheme { // Wrap in MaterialTheme for preview
        UserManagementScreen(viewModel = UserManagementViewModel())
    }
}
