package com.rooster.vethome.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rooster.vethome.domain.model.PatientHistorySummary
import com.rooster.core.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PatientSummaryCard(patient: PatientHistorySummary) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "${patient.farmName} - ${patient.species}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            patient.lastVisitDate?.let {
                Text(
                    stringResource(R.string.patient_last_visit_prefix) + " ${dateFormat.format(it)}", // TODO: Define R.string.patient_last_visit_prefix
                    style = MaterialTheme.typography.bodySmall
                )
            }
            patient.briefDiagnosis?.let {
                Text(
                    stringResource(R.string.patient_last_diagnosis_prefix) + " $it", // TODO: Define R.string.patient_last_diagnosis_prefix
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }
            if (patient.lastVisitDate == null && patient.briefDiagnosis == null) {
                Text(stringResource(R.string.patient_no_recent_info), style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.patient_no_recent_info
            }
            // TODO: Action to "View Full History"
        }
    }
}
