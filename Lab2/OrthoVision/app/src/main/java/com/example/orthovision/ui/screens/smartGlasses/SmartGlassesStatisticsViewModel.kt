package com.example.orthovision.ui.screens.smartGlasses

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.orthovision.data.model.GlassesStatistics
import com.example.orthovision.data.remote.repository.SmartGlassesStatisticsRepository
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class SmartGlassesStatisticsViewModel(
    private val repository: SmartGlassesStatisticsRepository  // Ось сюди треба репозиторій!
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _date = mutableStateOf(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val date: State<LocalDate> = _date

    private val _statistics = mutableStateOf<GlassesStatistics?>(null)
    val statistics: State<GlassesStatistics?> = _statistics

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        loadStatistics()
    }

    fun setDate(newDate: LocalDate) {
        _date.value = newDate
        loadStatistics()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getStatistics(_date.value.toString())
                _statistics.value = result
            } catch (e: Exception) {
                _error.value = "Не вдалося завантажити дані"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
