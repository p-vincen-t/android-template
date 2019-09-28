package com.nesstbase.apis

import com.google.gson.Gson
import com.nesstbase.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * builds the retrofit instance
 *
 * @return a retrofit instance
 */
fun retrofit(): Retrofit {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BASIC
    val client = OkHttpClient.Builder().addInterceptor(interceptor)
//    client.addInterceptor {
//        LogUtil.d("_BaseApi",
//            " url ", it.request().url(),
//            " headers ", it.request().headers(),
//            " body ", it.request().body())
//        it.proceed(it.request())
//    }

    val gson = Gson()

    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BuildConfig.API_URL)
        .client(client.build())
        .build()
}