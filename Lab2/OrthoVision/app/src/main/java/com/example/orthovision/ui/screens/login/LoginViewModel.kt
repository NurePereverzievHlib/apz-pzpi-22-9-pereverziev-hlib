package com.example.orthovision.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orthovision.data.TokenManager
import com.example.orthovision.data.remote.repository.AuthRepository
import com.example.orthovision.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)

                TokenManager.saveToken(response.token)
                TokenManager.saveUserId(response.user.id)
                TokenManager.saveUserName(response.user.name)

                _loginResult.value = response.token.isNotEmpty()
            } catch (e: Exception) {
                _loginResult.value = false
            }
        }
    }
}

