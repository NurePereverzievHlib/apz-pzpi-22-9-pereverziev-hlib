package com.example.orthovision.data.api

import com.example.orthovision.data.TokenManager
import com.example.orthovision.data.remote.api.AppointmentApi
import com.example.orthovision.data.remote.api.AuthApi
import com.example.orthovision.data.remote.api.AuthInterceptor
import com.example.orthovision.data.remote.api.ClinicApi
import com.example.orthovision.data.remote.api.DoctorApi
import com.example.orthovision.data.remote.api.MedicalRecordApi
import com.example.orthovision.data.remote.api.SmartGlassesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://a9ee-176-37-37-65.ngrok-free.app"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor() { TokenManager.getToken() })
            .build()
    }

    internal val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val clinicApi: ClinicApi by lazy {
        retrofit.create(ClinicApi::class.java)
    }

    val doctorApi: DoctorApi by lazy {
        retrofit.create(DoctorApi::class.java)
    }

    val appointmentApi: AppointmentApi by lazy {
        retrofit.create(AppointmentApi::class.java)
    }

    val medicalRecordApi: MedicalRecordApi by lazy {
        retrofit.create(MedicalRecordApi::class.java)
    }

    val smartGlassesApi: SmartGlassesApi by lazy {
        retrofit.create(SmartGlassesApi::class.java)
    }
}
