package com.example.orthovision.ui.screens.medicalRecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.orthovision.data.remote.repository.MedicalRecordRepository

class MedicalRecordViewModelFactory(
    private val repository: MedicalRecordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicalRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicalRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
