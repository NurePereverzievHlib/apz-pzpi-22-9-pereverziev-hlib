package com.example.orthovision.ui.screens.doctorDetails

import com.example.orthovision.data.api.RetrofitInstance
import com.example.orthovision.data.remote.api.AppointmentApi
import com.example.orthovision.data.remote.repository.AppointmentsRepository

object DoctorDetailsFactory {
    fun provideAppointmentsRepository(): AppointmentsRepository {
        val api = RetrofitInstance.retrofit.create(AppointmentApi::class.java)
        return AppointmentsRepository(api)
    }
}