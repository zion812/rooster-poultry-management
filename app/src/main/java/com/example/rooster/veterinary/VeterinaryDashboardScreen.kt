package com.example.rooster.veterinary

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
import com.example.rooster.veterinary.healthalerts.HealthAlertSystemScreen
import com.example.rooster.veterinary.patienthistory.PatientHistoryScreen
import com.example.rooster.veterinary.prescriptions.PrescriptionManagementScreen
import com.example.rooster.veterinary.telemedicine.TelemedicineScreen
import com.example.rooster.veterinary.consultations.VetConsultationScreen

object VeterinaryDestinations {
    const val DASHBOARD = "veterinary_dashboard"
    const val HEALTH_ALERTS = "veterinary_health_alerts"
    const val PATIENT_HISTORY = "veterinary_patient_history"
    const val PRESCRIPTIONS = "veterinary_prescriptions"
    const val TELEMEDICINE = "veterinary_telemedicine"
    const val CONSULTATIONS = "veterinary_consultations"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeterinaryDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veterinary Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF673AB7)) // Deep Purple
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
            item { VeterinaryDashboardButton(navController, "Health Alert System", VeterinaryDestinations.HEALTH_ALERTS) }
            item { VeterinaryDashboardButton(navController, "Patient History", VeterinaryDestinations.PATIENT_HISTORY) }
            item { VeterinaryDashboardButton(navController, "Prescription Management", VeterinaryDestinations.PRESCRIPTIONS) }
            item { VeterinaryDashboardButton(navController, "Telemedicine", VeterinaryDestinations.TELEMEDICINE) }
            item { VeterinaryDashboardButton(navController, "Vet Consultation Scheduler", VeterinaryDestinations.CONSULTATIONS) }
        }
    }
}

@Composable
fun VeterinaryDashboardButton(navController: NavController, text: String, route: String) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
    ) {
        Text(text)
    }
}

@Composable
fun VeterinaryFeatureNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = VeterinaryDestinations.DASHBOARD) {
        composable(VeterinaryDestinations.DASHBOARD) { VeterinaryDashboardScreen(navController) }
        composable(VeterinaryDestinations.HEALTH_ALERTS) { HealthAlertSystemScreen() }
        composable(VeterinaryDestinations.PATIENT_HISTORY) { PatientHistoryScreen() }
        composable(VeterinaryDestinations.PRESCRIPTIONS) { PrescriptionManagementScreen() }
        composable(VeterinaryDestinations.TELEMEDICINE) { TelemedicineScreen() }
        composable(VeterinaryDestinations.CONSULTATIONS) { VetConsultationScreen() }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVeterinaryDashboardScreen() {
    MaterialTheme {
        VeterinaryFeatureNavigator()
    }
}
