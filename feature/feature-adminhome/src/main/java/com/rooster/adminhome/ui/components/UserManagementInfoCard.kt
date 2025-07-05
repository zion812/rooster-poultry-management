package com.rooster.adminhome.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rooster.adminhome.domain.model.UserManagementInfo

@Composable
fun UserManagementInfoCard(userInfo: UserManagementInfo) {
import androidx.compose.ui.res.stringResource
import com.rooster.core.R as CoreR

@Composable
fun UserManagementInfoCard(userInfo: UserManagementInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(CoreR.string.admin_user_stats_title), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(stringResource(CoreR.string.admin_total_users_prefix), userInfo.totalUsers.toString())
            InfoRow(stringResource(CoreR.string.admin_new_users_today_prefix), userInfo.newUsersToday.toString())
            InfoRow(stringResource(CoreR.string.admin_active_users_prefix), userInfo.activeUsers.toString())
            InfoRow(stringResource(CoreR.string.admin_pending_verification_prefix), userInfo.pendingVerifications.toString())
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
    Spacer(modifier = Modifier.height(4.dp))
}
