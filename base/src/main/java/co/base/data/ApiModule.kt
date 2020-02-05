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

package co.base.data

import co.app.common.account.UserAccount
import com.google.gson.Gson
import co.base.BuildConfig
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import promise.commons.Promise
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
object ApiModule {

    @DataScope
    @JvmStatic
    @Provides
    fun provideOkHttpClient(userAccount: UserAccount, interceptor: Interceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(interceptor)
            .addInterceptor(object: Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    var request: Request = chain.request()
                    request = request.newBuilder()
                        .addHeader("client-type", "android")
                        .addHeader("api-key", userAccount.id)
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .build()
                    return chain.proceed(request)
                }
            })
            .addInterceptor(loggingInterceptor)
        return client.build()
    }

    @DataScope
    @JvmStatic
    @Provides
    fun provideApiUrl(): String = BuildConfig.API_URL

    @DataScope
    @JvmStatic
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        gson: Gson,
        promise: Promise,
        apiUrl: String
    ): Retrofit {

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory
                    .createWithScheduler(Schedulers.from(promise.executor()))
            )
            .baseUrl(apiUrl)
            .client(client)
            .build()
    }
}