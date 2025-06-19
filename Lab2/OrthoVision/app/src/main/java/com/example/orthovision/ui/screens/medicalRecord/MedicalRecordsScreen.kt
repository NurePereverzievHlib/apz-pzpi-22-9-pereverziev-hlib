package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.orthovision.data.model.AppointmentWithDiseases
import com.example.orthovision.ui.screens.medicalRecord.MedicalRecordViewModel

@Composable
fun MedicalRecordsScreen(viewModel: MedicalRecordViewModel, userId: Int) {

    val records by viewModel.records.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRecords(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Medical Records") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White) // <- Білий фон
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text(
                    text = error ?: "Unknown error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                records.isEmpty() -> Text(
                    text = "No medical records found",
                    modifier = Modifier.align(Alignment.Center),
                    fontStyle = FontStyle.Italic
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(records) { record ->
                        AppointmentCard(record)
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(record: AppointmentWithDiseases) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 6.dp,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Reason: ${record.appointment.Reason}",
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(
                text = "Status: ${record.appointment.Status}",
                style = MaterialTheme.typography.body1,
                color = Color.DarkGray
            )
            Text(
                text = "Created At: ${record.appointment.CreatedAt}",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            record.diseases?.let { diseases ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF7F7F7), shape = MaterialTheme.shapes.small)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Diagnoses",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    diseases.forEach { disease ->
                        DiseaseItem(disease)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } ?: Text("No diseases recorded", fontStyle = FontStyle.Italic, color = Color.Gray)
        }
    }
}

@Composable
fun DiseaseItem(disease: com.example.orthovision.data.model.Disease) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = disease.DiseaseName,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
            color = Color.Black
        )
        Text(
            text = disease.Description,
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )
        Text(
            text = "Status: ${disease.Status}",
            style = MaterialTheme.typography.caption.copy(fontStyle = FontStyle.Italic),
            color = Color.Gray
        )
    }
}
