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
import com.example.rooster.NavigationRoute
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
                    PersonalMessageList(personal, isTeluguMode) { msg ->
                        navController.navigate(NavigationRoute.Chat(msg.senderId).route)
                    }

                1 ->
                    GroupMessageList(group, isTeluguMode) { msg ->
                        navController.navigate(NavigationRoute.Chat(msg.groupId).route)
                    }

                2 ->
                    CommunityMessageList(community, isTeluguMode) { msg ->
                        navController.navigate(NavigationRoute.Chat(msg.id).route)
                    }
            }
        }
    }
}

@Composable
private fun PersonalMessageList(
    items: List<PersonalMessage>,
    isTeluguMode: Boolean,
    onClick: (PersonalMessage) -> Unit,
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
                ChatItemRow(item.senderName, item.content, isTeluguMode)
            }
        }
    }
}

@Composable
private fun GroupMessageList(
    items: List<GroupMessage>,
    isTeluguMode: Boolean,
    onClick: (GroupMessage) -> Unit,
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
                ChatItemRow(item.groupName, item.content, isTeluguMode)
            }
        }
    }
}

@Composable
private fun CommunityMessageList(
    items: List<CommunityMessage>,
    isTeluguMode: Boolean,
    onClick: (CommunityMessage) -> Unit,
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
                ChatItemRow(item.title, item.content, isTeluguMode)
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
