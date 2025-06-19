package com.example.orthovision.data.remote.repository

import com.example.orthovision.data.remote.api.MedicalRecordApi

class MedicalRecordRepository(private val api: MedicalRecordApi) {
    suspend fun fetchMedicalRecords(userId: Int) = api.getMedicalRecords(userId).data
}
