package com.nesstbase.apis

import retrofit2.Call
import retrofit2.http.*


interface AuthApi {

    @FormUrlEncoded
    @POST("login")
    @Headers("HOST: auth")
    fun login(@Field("identifier") identifier: String, @Field("password") password: String): Call<String>

    @FormUrlEncoded
    @POST("reset")
    @Headers("HOST: auth")
    fun resetPassword(@Field("identifier") identifier: String): Call<String>

    @FormUrlEncoded
    @POST("register")
    @Headers("HOST: auth")
    fun register(@FieldMap params: Map<String, Any>): Call<String>

}