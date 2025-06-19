import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.orthovision.data.model.AppointmentCreateRequest
import com.example.orthovision.data.model.AppointmentTime
import com.example.orthovision.data.model.Doctor
import com.example.orthovision.ui.screens.doctorDetails.DoctorDetailsFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorDetailScreen(
    doctor: Doctor,
    clinicId: Int,
    patientId: Int,
    onAppointmentCreated: () -> Unit = {}
) {
    val repository = remember { DoctorDetailsFactory.provideAppointmentsRepository() }
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<AppointmentTime?>(null) }
    var visitReason by remember { mutableStateOf("") }

    val appointments by produceState<List<AppointmentTime>>(initialValue = emptyList()) {
        value = repository.getAppointmentsByDoctor(doctor.id, clinicId)!!
    }

    val filteredAppointments = if (selectedDate.isEmpty()) {
        appointments.filter { !it.is_booked }
    } else {
        appointments.filter {
            !it.is_booked &&
                    it.available_time.substring(0, 10) == selectedDate
        }
    }

    fun createAppointment(appointmentTime: AppointmentTime, reason: String) {
        val newAppointment = AppointmentCreateRequest(
            patientId = patientId,
            reason = reason,
            appointmentTimeId = appointmentTime.id
        )
        CoroutineScope(Dispatchers.IO).launch {
            repository.createAppointment(newAppointment)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ви записались на прийом!", Toast.LENGTH_SHORT).show()
                onAppointmentCreated()
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(doctor.avatarUrl),
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = doctor.name, style = MaterialTheme.typography.h6)
                Text(text = doctor.specialization)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DatePickerButton(selectedDate = selectedDate, onDateSelected = { date ->
            selectedDate = date
        })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Вільні години на дату: ${if (selectedDate.isEmpty()) "Вся дата" else selectedDate}",
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (filteredAppointments.isEmpty()) {
            Text("Немає доступних годин 😔")
        } else {
            filteredAppointments.forEach { appointment ->
                Button(
                    onClick = {
                        selectedAppointment = appointment
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    val time = appointment.available_time.substring(11, 16)
                    Text(time)
                }
            }
        }
    }

    if (showDialog && selectedAppointment != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Причина візиту") },
            text = {
                TextField(
                    value = visitReason,
                    onValueChange = { visitReason = it },
                    placeholder = { Text("Опишіть причину візиту") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        createAppointment(selectedAppointment!!, visitReason)
                        visitReason = ""
                        showDialog = false
                        selectedAppointment = null
                    }
                ) {
                    Text("Підтвердити")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        visitReason = ""
                        selectedAppointment = null
                    }
                ) {
                    Text("Скасувати")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerButton(selectedDate: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(date)
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )

    Button(onClick = {
        datePickerDialog.show()
    }) {
        Text(if (selectedDate.isEmpty()) "Оберіть дату" else "Дата: $selectedDate")
    }
}

