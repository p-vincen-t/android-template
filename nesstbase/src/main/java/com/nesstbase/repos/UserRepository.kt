package com.nesstbase.repos

import com.nesstbase.apis.AuthApi
import com.nesstbase.auth.User
import com.nesstbase.errors.AuthError
import promise.pref.Preferences
import promisemodel.repo.AbstractAsyncIDataStore
import promisemodel.repo.AbstractSyncIDataStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AsyncUserRepository constructor(private  val authApi: AuthApi) : AbstractAsyncIDataStore<User>() {
    override fun one(res: (User, Any?) -> Unit, err: ((Exception) -> Unit)?, args: Map<String, Any?>?) {
        if (args != null) {
            authApi.login(args[LOGIN_IDENTIFIER_NAME] as String,
                args[PASSWORD_IDENTIFIER_NAME] as String)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {

                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {

                    }
                })
        } else err?.invoke(AuthError(AuthError.AUTH_CREDENTIALS_MISSING))
    }

    companion object {
        const val LOGIN_IDENTIFIER_NAME = "login_identifier"
        const val PASSWORD_IDENTIFIER_NAME = "password"
    }
}

class SyncUserRepository: AbstractSyncIDataStore<User>() {
    private val preferences: Preferences by lazy { Preferences(SESSION_PREFERENCES_NAME) }

    override fun one(args: Map<String, Any?>?): Pair<User?, Any?> {
        return Pair(null, null)
    }

    companion object {
        const val SESSION_PREFERENCES_NAME = "session_pref"
    }
}