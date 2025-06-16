package com.example.rooster.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.parse.ParseException
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Load existing user data
    LaunchedEffect(Unit) {
        val user = ParseUser.getCurrentUser()
        name = user?.username.orEmpty()
        location = user?.getString("location").orEmpty()
        bio = user?.getString("bio").orEmpty()
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTeluguMode) "సంపాదించండి" else "Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onLanguageToggle) {
                        Text(if (isTeluguMode) "EN" else "తె")
                    }
                },
            )
        },
    ) { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
            if (loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (isTeluguMode) "పేరు" else "Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isTeluguMode) "స్థానం" else "Location") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text(if (isTeluguMode) "జీవనపు వివరణ" else "Bio") },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                        maxLines = 5,
                    )
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            scope.launch {
                                loading = true
                                val user = ParseUser.getCurrentUser()
                                user?.apply {
                                    put("username", name)
                                    put("location", location)
                                    put("bio", bio)
                                }
                                withContext(Dispatchers.IO) {
                                    try {
                                        ParseUser.getCurrentUser()?.save()
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT)
                                                .show()
                                            navController.popBackStack()
                                        }
                                    } catch (e: ParseException) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                context,
                                                "Save failed: ${e.localizedMessage}",
                                                Toast.LENGTH_LONG,
                                            ).show()
                                        }
                                    }
                                }
                                loading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (isTeluguMode) "సేవ్ చేయండి" else "Save")
                    }
                }
            }
        }
    }
}
