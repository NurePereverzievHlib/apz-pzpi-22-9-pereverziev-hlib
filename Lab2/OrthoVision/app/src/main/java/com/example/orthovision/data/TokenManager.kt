package com.example.orthovision.data

import android.content.Context

object TokenManager {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    private val prefs by lazy {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? = prefs.getString("auth_token", null)

    fun saveUserId(userId: Int) {
        prefs.edit().putInt("user_id", userId).apply()
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun saveUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String? = prefs.getString("user_name", null)
}



