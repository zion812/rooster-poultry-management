package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.models.VaccinationTemplate
import com.example.rooster.viewmodel.FarmerDashboardViewModel

@Composable
fun VaccinationTemplateUploader(
    farmId: String,
    vm: FarmerDashboardViewModel = viewModel(),
) {
    var isUploading by remember { mutableStateOf(false) }
    var newTemplateName by remember { mutableStateOf("") }
    val templates by vm.templates.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Text("Vaccination Templates", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = newTemplateName,
            onValueChange = { newTemplateName = it },
            label = { Text("Template Name") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                isUploading = true
                val template =
                    VaccinationTemplate(
                        farmId = farmId,
                        name = newTemplateName,
                        schedule = listOf(),
                    )
                vm.uploadTemplate(farmId, template)
                newTemplateName = ""
                isUploading = false
            },
            enabled = newTemplateName.isNotBlank(),
        ) {
            Text(if (isUploading) "Uploading..." else "Upload Template")
        }
        Spacer(Modifier.height(16.dp))
        templates.forEach { tpl ->
            ListItem(
                headlineContent = { Text(tpl.name) },
                supportingContent = { Text("Uploaded: ${tpl.uploadedAt}") },
            )
            HorizontalDivider()
        }
    }
}
