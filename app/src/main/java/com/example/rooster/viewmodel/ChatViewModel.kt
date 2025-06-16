package com.example.rooster.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.MessagingManager
import com.example.rooster.MessagingManager.CommunityMessage
import com.example.rooster.MessagingManager.GroupMessage
import com.example.rooster.MessagingManager.PersonalMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val messagingManager by lazy { MessagingManager.getInstance(getApplication()) }

    private val _personal = MutableStateFlow<List<PersonalMessage>>(emptyList())
    val personalMessages: StateFlow<List<PersonalMessage>> = _personal

    private val _group = MutableStateFlow<List<GroupMessage>>(emptyList())
    val groupMessages: StateFlow<List<GroupMessage>> = _group

    private val _community = MutableStateFlow<List<CommunityMessage>>(emptyList())
    val communityMessages: StateFlow<List<CommunityMessage>> = _community

    init {
        viewModelScope.launch {
            messagingManager.personalMessages.collectLatest { msgs -> _personal.value = msgs }
        }
        viewModelScope.launch {
            messagingManager.groupMessages.collectLatest { msgs -> _group.value = msgs }
        }
        viewModelScope.launch {
            messagingManager.communityMessages.collectLatest { msgs -> _community.value = msgs }
        }
    }
}
