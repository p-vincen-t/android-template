package com.nesstbase.auth

import androidx.collection.ArrayMap
import com.nesstbase.apis.AuthApi
import com.nesstbase.errors.AuthError
import com.nesstbase.repos.AsyncUserRepository
import com.nesstbase.scopes.AppScope
import promise.model.Result
import promisemodel.repo.StoreRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AppScope
class Session @Inject constructor() {

    @Inject
    lateinit var userRepository: StoreRepository<User>

    @Inject lateinit var authApi: AuthApi


    fun user(): User {
        return User("Peter Vincent", "dev4vin@gmail.com")
    }

    fun login(identifier: String,
              password: String,
              result: Result<User, AuthError>) {
        userRepository.one(ArrayMap<String, Any>().apply {
            put(AsyncUserRepository.LOGIN_IDENTIFIER_NAME, identifier)
            put(AsyncUserRepository.PASSWORD_IDENTIFIER_NAME, password)
        }, { user, _ -> result.response(user)  }, {})
    }

    fun resetPassword(identifier: String,
                      result: Result<Boolean, AuthError>) {

        authApi.resetPassword(identifier).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }

    fun register(names: String,
                 email: String,
                 phone: String,
                 password: String,
                 result: Result<Boolean, AuthError>) {

        authApi.register(ArrayMap<String, Any>().apply {

        }).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }

}