package com.example.orthovision.data.remote.repository

import com.example.orthovision.data.model.Clinic
import com.example.orthovision.data.model.toClinic
import com.example.orthovision.data.remote.api.ClinicApi

class ClinicRepository(private val api: ClinicApi) {
    suspend fun getAllClinics(): List<Clinic> {
        val response = api.getAllClinics()
        return response.clinics.map { it.toClinic() }
    }
}
