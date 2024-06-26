package com.ns.turkcellfinal.data.model.login

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    val email: String,
    val firstName: String,
    val gender: String,
    val id: Int,
    val image: String,
    val lastName: String,
    val refreshToken: String,
    val token: String,
    val username: String
): Parcelable


data class LoginRequest(
    val username: String,
    val password: String
)
