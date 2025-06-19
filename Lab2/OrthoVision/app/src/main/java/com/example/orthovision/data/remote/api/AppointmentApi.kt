package com.example.orthovision.data.remote.api

import com.example.orthovision.data.model.Appointment
import com.example.orthovision.data.model.AppointmentCreateRequest
import com.example.orthovision.data.model.AppointmentResponse
import com.example.orthovision.data.model.AppointmentsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AppointmentApi {
    @GET("appointment-times/search")
    suspend fun getAppointmentsByDoctor(
        @Query("doctor_id") doctorId: Int,
        @Query("clinic_id") clinicId: Int?
    ): AppointmentsResponse

    @POST("appointments")
    suspend fun createAppointment(@Body appointment: AppointmentCreateRequest): Response<AppointmentsResponse>

    @GET("appointments/patient/{id}")
    suspend fun getAppointmentsByPatientId(@Path("id") patientId: Int):  AppointmentResponse

    @DELETE("appointments/{id}")
    suspend fun deleteAppointment(@Path("id") appointmentId: Int): Response<Unit>
}
