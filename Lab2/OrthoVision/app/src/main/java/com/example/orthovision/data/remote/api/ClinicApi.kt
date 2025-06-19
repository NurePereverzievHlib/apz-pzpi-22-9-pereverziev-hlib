package com.example.orthovision.data.remote.api

import retrofit2.http.GET
import com.example.orthovision.data.model.ClinicsResponse

interface ClinicApi {
    @GET("clinics")
    suspend fun getAllClinics(): ClinicsResponse
}
