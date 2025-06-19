package com.example.orthovision.ui.screens.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orthovision.data.model.Appointment
import com.example.orthovision.data.remote.repository.AppointmentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentsViewModel(private val repository: AppointmentsRepository) : ViewModel() {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadAppointments(patientId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = repository.getPendingAppointments(patientId)
                _appointments.value = list
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Помилка завантаження"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelAppointment(appointmentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.cancelAppointment(appointmentId)
            if (success) {
                _appointments.value = _appointments.value.filter { it.ID != appointmentId }
                _errorMessage.value = null
            } else {
                _errorMessage.value = "Не вдалося скасувати запис"
            }
            _isLoading.value = false
        }
    }
}
