package com.example.rooster.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * StandardScreenLayout - Core Layout Composable for Rooster App
 * Provides consistent padding, structure, and optional scrolling
 * Follows the UI Architecture Plan for reusable layouts
 */

@Composable
fun StandardScreenLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    scrollable: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
    ) { innerPadding ->
        val columnModifier =
            if (scrollable) {
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState())
            } else {
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(contentPadding)
            }

        Column(
            modifier = columnModifier,
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            content = content,
        )
    }
}

/**
 * StandardScreenLayoutWithoutScaffold - For use within existing Scaffold contexts
 * Provides consistent padding and structure without double Scaffold wrapping
 */
@Composable
fun StandardScreenLayoutWithoutScaffold(
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val columnModifier =
        if (scrollable) {
            modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
        } else {
            modifier
                .fillMaxSize()
                .padding(contentPadding)
        }

    Column(
        modifier = columnModifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

/**
 * StandardCardLayout - Consistent card structure for content sections
 */
@Composable
fun StandardCardLayout(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
        ) {
            title?.let {
                Text(
                    text = it,
                    style = titleStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            content()
        }
    }
}

/**
 * LoadingLayout - Consistent loading state layout
 */
@Composable
fun LoadingLayout(
    modifier: Modifier = Modifier,
    message: String = "Loading...",
    showProgress: Boolean = true,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (showProgress) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * ErrorLayout - Consistent error state layout
 */
@Composable
fun ErrorLayout(
    modifier: Modifier = Modifier,
    message: String = "Something went wrong",
    actionText: String = "Retry",
    onAction: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
            onAction?.let { action ->
                Button(
                    onClick = action,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                ) {
                    Text(actionText)
                }
            }
        }
    }
}

/**
 * EmptyStateLayout - Consistent empty state layout
 */
@Composable
fun EmptyStateLayout(
    modifier: Modifier = Modifier,
    message: String = "No items found",
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (actionText != null && onAction != null) {
                OutlinedButton(
                    onClick = onAction,
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                ) {
                    Text(actionText)
                }
            }
        }
    }
}
