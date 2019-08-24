package com.nesstbase.apis

import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded


interface AuthApi {
    @FormUrlEncoded
    @POST("login")
    @Headers("HOST: auth")
    fun login(@Field("identifier") identifier: String, @Field("password") password: String): Call<String>

}