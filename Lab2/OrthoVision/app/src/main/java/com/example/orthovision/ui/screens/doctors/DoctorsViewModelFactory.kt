package com.example.orthovision.ui.screens.doctors

import DoctorsViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.orthovision.data.remote.api.DoctorApi
import com.example.orthovision.data.remote.repository.DoctorsRepository

class DoctorsViewModelFactory(
    private val doctorsRepository: DoctorsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorsViewModel(doctorsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
