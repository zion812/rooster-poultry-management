package com.example.rooster.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.rooster.ui.theme.RoosterCustomShapes

/**
 * Core UI Components for Rooster App
 * Implements reusable components following the UI Architecture Plan
 * Designed for rural users with larger touch targets and clear visual feedback
 */

/**
 * AppTopAppBar - Consistent top app bar across screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null && onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navigate back",
                    )
                }
            }
        },
        actions = actions,
        colors = colors,
    )
}

/**
 * LoadingIndicator - Consistent loading indicator
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Loading...",
    size: androidx.compose.ui.unit.Dp = 40.dp,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * ErrorDisplay - Consistent error display with retry option
 */
@Composable
fun ErrorDisplay(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    retryText: String = "Retry",
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
        onRetry?.let { retry ->
            Button(
                onClick = retry,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text(retryText)
            }
        }
    }
}

/**
 * SimpleButton - Standard button with consistent styling
 */
@Composable
fun SimpleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors =
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp), // Rural-friendly touch target
        enabled = enabled,
        colors = colors,
        shape = RoosterCustomShapes.ButtonShape,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

/**
 * StandardTextField - Consistent text field with rural-friendly design
 */
@Composable
fun StandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            visualTransformation =
                if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
            leadingIcon =
                leadingIcon?.let { icon ->
                    {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            trailingIcon =
                if (isPassword) {
                    {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            )
                        }
                    }
                } else {
                    trailingIcon
                },
            shape = RoosterCustomShapes.TextFieldShape,
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp), // Rural-friendly height
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
            )
        }
    }
}

/**
 * StandardCard - Consistent card design
 */
@Composable
fun StandardCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: androidx.compose.ui.unit.Dp = 2.dp,
    colors: CardColors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = RoosterCustomShapes.CardShape,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content,
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = RoosterCustomShapes.CardShape,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content,
            )
        }
    }
}

/**
 * StatusChip - Consistent status indicator chip
 */
@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Surface(
        modifier = modifier,
        shape = RoosterCustomShapes.ChipShape,
        color = containerColor,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

/**
 * IconButton with consistent styling and rural-friendly touch targets
 */
@Composable
fun StandardIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors =
        IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp), // Rural-friendly touch target
        enabled = enabled,
        colors = colors,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
        )
    }
}
