package com.example.rooster

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    val context = LocalContext.current
    val appVersion = "1.0.0 (Build 20250529)"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
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
                SettingItem(
                    title = stringResource(R.string.language_preference),
                    icon = Icons.Default.Language,
                    onClick = onLanguageToggle,
                ) {
                    Text(
                        text = if (isTeluguMode) "తెలుగు" else "English",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item { HorizontalDivider() }
            item {
                SettingItem(
                    title = stringResource(R.string.notification_settings),
                    icon = Icons.Default.Notifications,
                    onClick = {
                        // Open app notification settings
                        val intent =
                            Intent().apply {
                                action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                            }
                        context.startActivity(intent)
                    },
                )
            }
            item { HorizontalDivider() }
            item {
                SettingItem(
                    title = "Privacy Policy",
                    icon = Icons.Default.Security,
                    onClick = {
                        // Open privacy policy URL
                        val url = "https://example.com/privacy"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                )
            }
            item { HorizontalDivider() }
            item {
                SettingItem(
                    title = "Terms of Service",
                    icon = Icons.Default.Info,
                    onClick = {
                        // Open terms of service URL
                        val url = "https://example.com/terms"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                )
            }
            item { HorizontalDivider() }
            item {
                SettingItem(
                    title = "App Version",
                    icon = Icons.Default.Info,
                    onClick = null,
                ) {
                    Text(appVersion, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        trailingContent?.invoke()
        if (onClick != null && trailingContent == null) {
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
