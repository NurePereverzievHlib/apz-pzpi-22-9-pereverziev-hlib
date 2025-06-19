package com.example.orthovision.data.remote.repository

import com.example.orthovision.data.model.GlassesStatistics
import com.example.orthovision.data.remote.api.SmartGlassesApi


class SmartGlassesStatisticsRepository(private val api: SmartGlassesApi) {
    suspend fun getStatistics(date: String): GlassesStatistics {
        return api.getStatistics(date)
    }
}
