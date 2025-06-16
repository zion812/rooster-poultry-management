package com.example.rooster.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.MessagingManager.CommunityMessage
import com.example.rooster.MessagingManager.GroupMessage
import com.example.rooster.MessagingManager.PersonalMessage
import com.example.rooster.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
    viewModel: ChatViewModel = viewModel(),
) {
    val personal by viewModel.personalMessages.collectAsState()
    val group by viewModel.groupMessages.collectAsState()
    val community by viewModel.communityMessages.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs =
        listOf(
            if (isTeluguMode) "వ్యక్తిగత" else "Personal",
            if (isTeluguMode) "గ్రూప్" else "Group",
            if (isTeluguMode) "కమ్యూనిటీ" else "Community",
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTeluguMode) "చాట్స్" else "Chats") },
                actions = {
                    TextButton(onClick = onLanguageToggle) { Text(if (isTeluguMode) "EN" else "తె") }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }
            when (selectedTab) {
                0 ->
                    MessageList(personal, isTeluguMode) { msg ->
                        navController.navigate("chat/${msg.senderId}")
                    }

                1 ->
                    MessageList(group, isTeluguMode) { msg ->
                        navController.navigate("chat/${msg.groupId}")
                    }

                2 ->
                    MessageList(community, isTeluguMode) { msg ->
                        navController.navigate("chat/${msg.id}")
                    }
            }
        }
    }
}

@Composable
private fun <T> MessageList(
    items: List<T>,
    isTeluguMode: Boolean,
    onClick: (T) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items) { item ->
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onClick(item) },
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                when (item) {
                    is PersonalMessage -> ChatItemRow(item.senderName, item.content, isTeluguMode)
                    is GroupMessage -> ChatItemRow(item.groupName, item.content, isTeluguMode)
                    is CommunityMessage -> ChatItemRow(item.title, item.content, isTeluguMode)
                }
            }
        }
    }
}

@Composable
private fun ChatItemRow(
    title: String,
    message: String,
    isTeluguMode: Boolean,
) {
    Column(Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(text = message, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}
