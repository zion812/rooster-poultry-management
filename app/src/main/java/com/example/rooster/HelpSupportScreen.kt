package com.example.rooster

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.help)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLanguageToggle) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = if (isTeluguMode) "Switch to English" else "Switch to Telugu",
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            item {
                SupportSectionTitle("FAQ")
                SupportItem(
                    title = "Frequently Asked Questions",
                    icon = Icons.Default.Quiz,
                    onClick = { /* Navigate to FAQScreen or show dialog */ },
                )
                SupportItem(
                    title = "Troubleshooting Guides",
                    icon = Icons.Default.Build,
                    onClick = { /* Navigate to TroubleshootingGuidesScreen */ },
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { HorizontalDivider() }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                SupportSectionTitle("Contact Support")
                SupportItem(
                    title = "Email Support",
                    icon = Icons.Default.Email,
                    onClick = { /* Launch email client */ },
                )
                SupportItem(
                    title = "Call Support",
                    icon = Icons.Default.Call,
                    onClick = { /* Launch dialer */ },
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { HorizontalDivider() }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                SupportSectionTitle("Submit Feedback")
                SupportItem(
                    title = "Submit Feedback",
                    icon = Icons.Default.Feedback,
                    onClick = { navController.navigate(NavigationRoute.Feedback.route) },
                )
            }
        }
    }
}

@Composable
private fun SupportSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun SupportItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}
