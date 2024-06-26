package com.ns.turkcellfinal.presentation.account

import com.ns.turkcellfinal.data.model.login.LoginResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor() {

    var user: LoginResponse? = null
        private set

    fun setUser(loginResponse: LoginResponse) {
        user = loginResponse
    }

    fun clearUser() {
        user = null
    }
}