package com.example.orthovision.data.remote.repository

import com.example.orthovision.data.model.Appointment
import com.example.orthovision.data.model.AppointmentCreateRequest
import com.example.orthovision.data.model.AppointmentTime
import com.example.orthovision.data.model.AppointmentsResponse
import com.example.orthovision.data.remote.api.AppointmentApi

class AppointmentsRepository(private val api: AppointmentApi) {

    suspend fun getAppointmentsByDoctor(doctorId: Int, clinicId: Int? = null): List<AppointmentTime>? {
        return try {
            api.getAppointmentsByDoctor(doctorId, clinicId).data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createAppointment(request: AppointmentCreateRequest): Boolean {
        return try {
            val response = api.createAppointment(request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getPendingAppointments(patientId: Int): List<Appointment> {
        return try {
            val response = api.getAppointmentsByPatientId(patientId)
            return response.data.filter { it.Status == "pending" }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun cancelAppointment(appointmentId: Int): Boolean {
        return try {
            val response = api.deleteAppointment(appointmentId)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
