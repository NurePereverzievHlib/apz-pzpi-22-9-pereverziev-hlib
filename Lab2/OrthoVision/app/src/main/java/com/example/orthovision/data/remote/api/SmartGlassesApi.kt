package com.example.orthovision.data.remote.api

import com.example.orthovision.data.model.GlassesStatistics
import retrofit2.http.GET
import retrofit2.http.Query

interface SmartGlassesApi {
    @GET("smart-glasses/statistics")
    suspend fun getStatistics(@Query("date") date: String): GlassesStatistics
}