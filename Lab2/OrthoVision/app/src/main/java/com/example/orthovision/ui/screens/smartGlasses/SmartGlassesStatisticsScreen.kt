package com.example.orthovision.ui.statistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.orthovision.data.model.GlassesStatistics
import com.example.orthovision.ui.screens.smartGlasses.SmartGlassesStatisticsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SmartGlassesStatisticsScreen(
    viewModel: SmartGlassesStatisticsViewModel
) {
    val date by viewModel.date
    val stats by viewModel.statistics
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Статистика за ${date.format(DateTimeFormatter.ISO_DATE)}",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        DateSelector(
            selectedDate = date,
            onDateSelected = viewModel::setDate
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text(text = error ?: "Помилка", color = MaterialTheme.colorScheme.error)
            }
            stats != null -> {
                StatisticsContent(stats!!)
            }
            else -> {
                Text("Немає даних")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Тут можна зробити DatePickerDialog, або Dropdown для дати, місяця, року
    // Для простоти — вибір через DatePickerDialog, який викликається кнопкою

    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        DatePickerDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    // TODO: Тут треба отримати дату з DatePicker (якщо використовуєш Material3 DatePicker)
                    // Для прикладу, умовно:
                    // onDateSelected(selectedDateFromPicker)
                    showDialog.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Відміна")
                }
            }
        ) {
            // TODO: DatePicker UI тут
        }
    }

    Button(onClick = { showDialog.value = true }) {
        Text(text = "Оберіть дату: ${selectedDate.format(DateTimeFormatter.ISO_DATE)}")
    }
}

@Composable
fun StatisticsContent(stats: GlassesStatistics) {
    fun formatTime(seconds: Double): String {
        val mins = (seconds / 60).toInt()
        val secs = (seconds % 60).toInt()
        return "${mins} хв ${secs} сек"
    }

    Column {
        Text("Час нахилу голови > 45°: ${formatTime(stats.timeHeadTiltExceeds45)}")
        Text("Час яскравого освітлення: ${formatTime(stats.timeHighLight)}")
        Text("Час слабкого освітлення: ${formatTime(stats.timeLowLight)}")
    }
}
