package com.example.orthovision.ui.screens.smartGlasses

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.orthovision.data.remote.repository.SmartGlassesStatisticsRepository
import com.example.orthovision.ui.statistics.SmartGlassesStatisticsScreen

class SmartGlassesStatisticsScreenFactory(
    private val repository: SmartGlassesStatisticsRepository
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmartGlassesStatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmartGlassesStatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


