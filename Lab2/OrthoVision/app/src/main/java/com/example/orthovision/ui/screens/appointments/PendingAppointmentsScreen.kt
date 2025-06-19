package com.example.orthovision.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orthovision.data.model.Appointment


@Composable
fun PendingAppointmentsScreen(
    patientId: Int,
    viewModel: AppointmentsViewModel = viewModel(),
    onAppointmentCancelled: () -> Unit
) {
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.loadAppointments(patientId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)  // Білий фон для всього екрану
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            }
            appointments.isEmpty() -> {
                Text(
                    text = "Немає активних записів",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(appointments) { appointment ->
                        AppointmentItem(
                            appointment = appointment,
                            onCancel = { viewModel.cancelAppointment(appointment.ID) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentItem(
    appointment: Appointment,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp,
        backgroundColor = Color.White // Білий колір картки
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Причина: ${appointment.Reason}", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Статус: ${appointment.Status}", color = Color.Gray)
                Text(text = "Дата створення: ${appointment.CreatedAt.substring(0, 10)}", color = Color.Gray)
            }
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Скасувати запис",
                    tint = Color.Red
                )
            }
        }
    }
}
