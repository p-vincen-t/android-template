/*
 * Copyright 2020, {{App}}
 * Licensed under the Apache License, Version 2.0, "{{App}} Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.base

import co.app.common.account.UserAccount
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.logging.HttpLoggingInterceptor
import promise.commons.AndroidPromise
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

@Module
object ApiModule {

    @DataScope
    @JvmStatic
    @Provides
    fun provideOkHttpClient(
        userAccount: UserAccount?,
        promise: AndroidPromise,
        interceptor: Interceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val cacheDirectory = File(
            promise.context()
                .cacheDir.absolutePath
                .plus("network")
        )
        val client = OkHttpClient.Builder()
            .cache(
                Cache(
                    directory = cacheDirectory,
                    maxSize = 10L * 1024L * 1024L // 10 MiB
                )
            )
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .authenticator(object : Authenticator {
                private fun responseCount(response: Response): Int {
                    var newResponse = response
                    var result = 1
                    while (newResponse.priorResponse.also { newResponse = it!! } != null) result++
                    return result
                }

                @Throws(IOException::class)
                override fun authenticate(route: Route?, response: Response): Request? {
                    // Give up, we've already attempted to authenticate.
                    if (response.request.header("Client") != null) return null
                    // If we've failed 3 times, give up.
                    if (responseCount(response) >= 3) return null
                    val credential = Credentials.basic("client-type", "android")
                    val credential2 = Credentials.basic("token", if (userAccount != null) userAccount.id.id!! else "")
                    return response.request.newBuilder()
                        .header("device", credential)
                        .header("user", credential2)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .build()
                }
            })
            .addInterceptor(loggingInterceptor)
        if (BuildConfig.DEBUG)
            client.addNetworkInterceptor(StethoInterceptor())
        return client.build()
    }

    @DataScope
    @JvmStatic
    @Provides
    fun provideApiUrl(): HttpUrl {
        return BuildConfig.API_URL.toHttpUrlOrNull()!!
    }

    @DataScope
    @JvmStatic
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        gson: Gson,
        promise: AndroidPromise,
        apiUrl: HttpUrl
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(
            RxJava2CallAdapterFactory
                .createWithScheduler(Schedulers.from(promise.executor()))
        )
        .baseUrl(apiUrl)
        .client(client)
        .build()
}