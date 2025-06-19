package com.example.orthovision.ui.screens.doctors

import DoctorCard
import DoctorsViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items


@Composable
fun DoctorsScreen(
    clinicId: String,
    viewModel: DoctorsViewModel,
    onDoctorClick: (Int) -> Unit // <-- новий параметр
) {
    val doctors = viewModel.doctors.value

    LaunchedEffect(clinicId) {
        viewModel.loadDoctorsByClinic(clinicId)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Лікарі клініки",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items(doctors) { doctor ->
                DoctorCard(
                    doctor = doctor,
                    onClick = { onDoctorClick(doctor.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}



