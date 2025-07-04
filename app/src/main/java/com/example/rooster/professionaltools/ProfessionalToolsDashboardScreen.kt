package com.example.rooster.professionaltools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rooster.professionaltools.casestudies.CaseStudyScreen
import com.example.rooster.professionaltools.diagnosisassistant.DiagnosisAssistantScreen
import com.example.rooster.professionaltools.educationalcontent.EducationalContentScreen
import com.example.rooster.professionaltools.vetnetwork.VeterinaryNetworkScreen

object ProfessionalToolsDestinations {
    const val DASHBOARD = "professional_tools_dashboard"
    const val CASE_STUDIES = "professional_tools_case_studies"
    const val DIAGNOSIS_ASSISTANT = "professional_tools_diagnosis_assistant"
    const val EDUCATIONAL_CONTENT = "professional_tools_educational_content"
    const val VETERINARY_NETWORK = "professional_tools_veterinary_network"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalToolsDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Professional Tools Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)) // Dark Blue
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { ProfessionalToolsDashboardButton(navController, "Case Studies", ProfessionalToolsDestinations.CASE_STUDIES) }
            item { ProfessionalToolsDashboardButton(navController, "Diagnosis Assistant", ProfessionalToolsDestinations.DIAGNOSIS_ASSISTANT) }
            item { ProfessionalToolsDashboardButton(navController, "Educational Content", ProfessionalToolsDestinations.EDUCATIONAL_CONTENT) }
            item { ProfessionalToolsDashboardButton(navController, "Veterinary Network", ProfessionalToolsDestinations.VETERINARY_NETWORK) }
        }
    }
}

@Composable
fun ProfessionalToolsDashboardButton(navController: NavController, text: String, route: String) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
    ) {
        Text(text)
    }
}

@Composable
fun ProfessionalToolsFeatureNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ProfessionalToolsDestinations.DASHBOARD) {
        composable(ProfessionalToolsDestinations.DASHBOARD) { ProfessionalToolsDashboardScreen(navController) }
        composable(ProfessionalToolsDestinations.CASE_STUDIES) { CaseStudyScreen() }
        composable(ProfessionalToolsDestinations.DIAGNOSIS_ASSISTANT) { DiagnosisAssistantScreen() }
        composable(ProfessionalToolsDestinations.EDUCATIONAL_CONTENT) { EducationalContentScreen() }
        composable(ProfessionalToolsDestinations.VETERINARY_NETWORK) { VeterinaryNetworkScreen() }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfessionalToolsDashboardScreen() {
    MaterialTheme {
        ProfessionalToolsFeatureNavigator()
    }
}
