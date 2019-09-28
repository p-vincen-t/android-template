package com.nesst.validators

import android.util.Patterns

fun isEmailValid(input: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(input).matches()

fun isPhoneNumberValid(input: String) = Patterns.PHONE.matcher(input).matches()

fun isIdentifierValid(input: String): Pair<Boolean, String?> {
    if (input.isBlank()) return  Pair(false, "Email or phone number is required")
    return if (input.contains('@')) {
        if (isEmailValid(input)) {
            Pair(true, null)
        } else Pair(false, "Your email address seems incorrect")
    } else {
        if (isPhoneNumberValid(input)) {
            Pair(true, null)
        } else Pair(false, "Your phone number seems incorrect")
    }
}

// A placeholder password validation check
fun isPasswordValid(password: String): Boolean {
    return password.length > 5
}