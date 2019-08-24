package com.nesstbase.errors

class AuthError() : Exception() {

    var code: Int = 0

    constructor(code: Int) : this() {
        this.code = code
    }

    companion object {
        const val AUTH_CREDENTIALS_MISSING = 1
    }
}