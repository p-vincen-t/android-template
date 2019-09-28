package com.nesstbase.auth

data class LoginAuthResult(val token: String, val refresh_token: String)