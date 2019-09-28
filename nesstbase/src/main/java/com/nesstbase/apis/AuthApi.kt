package com.nesstbase.apis

import retrofit2.Call
import retrofit2.http.*

/**
 * authentication api
 *
 */
interface AuthApi {
    /**
     * login api
     *
     * @param identifier email or phone number
     * @param password user password
     * @return token
     */
    @FormUrlEncoded
    @POST("login")
    @Headers("HOST: AUTH")
    fun login(
        @Field("identifier") identifier: String,
        @Field("password") password: String,
        @Field("deviceId") deviceId: String
    ): Call<String>

    /**
     * reset password api
     *
     * @param identifier phone or email
     * @return
     */
    @FormUrlEncoded
    @POST("reset")
    @Headers("HOST: AUTH")
    fun resetPassword(@Field("identifier") identifier: String): Call<String>

    /**
     * registration api
     *
     * @param params email, names, phone, password
     * @return
     */
    @FormUrlEncoded
    @POST("register")
    @Headers("HOST: AUTH")
    fun register(@FieldMap params: Map<String, Any>): Call<String>

}