package com.example.orthovision.data.remote.repository

import com.example.orthovision.data.model.Doctor
import com.example.orthovision.data.remote.api.DoctorApi

class DoctorsRepository(private val api: DoctorApi) {
    suspend fun getDoctorsByClinic(clinicId: String): List<Doctor> {
        return api.getDoctorsByClinic(clinicId).doctors
    }

    suspend fun getDoctorById(doctorId: String): Doctor {
        return api.getDoctorById(doctorId)
    }

}
