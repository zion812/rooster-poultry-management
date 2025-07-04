package com.example.rooster.feature.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.common.R // Assuming R class from core-common

@Composable
fun roleToDisplayString(role: UserRole): String {
    return when (role) {
        UserRole.FARMER -> stringResource(id = R.string.role_farmer)
        UserRole.BUYER -> stringResource(id = R.string.role_buyer)
        UserRole.ADMIN -> stringResource(id = R.string.role_admin)
        UserRole.VETERINARIAN -> stringResource(id = R.string.role_veterinarian)
    }
}
