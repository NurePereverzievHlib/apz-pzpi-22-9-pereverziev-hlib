package com.example.orthovision.ui.screens.home

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orthovision.data.model.Clinic
import com.example.orthovision.data.remote.repository.ClinicRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ClinicRepository) : ViewModel() {

    var clinics by mutableStateOf<List<Clinic>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")

    val filteredClinics: List<Clinic>
        get() = clinics.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.address.contains(searchQuery, ignoreCase = true)
        }


    internal fun loadClinics() {
        viewModelScope.launch {
            clinics = repository.getAllClinics()
        }
    }
}
