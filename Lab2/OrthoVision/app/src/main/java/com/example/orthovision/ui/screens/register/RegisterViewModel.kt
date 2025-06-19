package com.example.orthovision.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orthovision.data.model.RegisterRequest
import com.example.orthovision.data.remote.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _registerResult = MutableStateFlow<Boolean?>(null)
    val registerResult: StateFlow<Boolean?> = _registerResult

    fun register(name: String, email: String, password: String, role: String = "patient") {
        viewModelScope.launch {
            try {
                val request = RegisterRequest(name, email, password, role)
                val result = repository.register(request)
                _registerResult.value = result.user != null // якщо user не null — успіх
            } catch (e: Exception) {
                _registerResult.value = false
            }
        }
    }


}