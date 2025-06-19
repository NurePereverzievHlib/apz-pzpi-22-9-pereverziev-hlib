package com.example.orthovision.ui.screens.medicalRecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orthovision.data.model.AppointmentWithDiseases
import com.example.orthovision.data.remote.repository.MedicalRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicalRecordViewModel(private val repo: MedicalRecordRepository) : ViewModel() {

    private val _records = MutableStateFlow<List<AppointmentWithDiseases>>(emptyList())
    val records: StateFlow<List<AppointmentWithDiseases>> = _records

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadRecords(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val data = repo.fetchMedicalRecords(userId)
                if (data != null) {
                    _records.value = data
                } else {
                    _error.value = "No data found"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
            _isLoading.value = false
        }
    }
}
