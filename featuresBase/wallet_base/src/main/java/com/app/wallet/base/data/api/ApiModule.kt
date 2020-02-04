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

package com.app.wallet.base.data.api

import com.google.gson.Gson
import com.app.wallet.base.data.DataScope
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import promise.commons.Promise
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
object ApiModule {

    @Provides
    @DataScope
    @JvmStatic
    fun provideRetrofitInstance(
        client: OkHttpClient,
        gson: Gson,
        promise: Promise
    ): Retrofit {
        return Retrofit.Builder()
            /*.addConverterFactory(ResponseConverters.emptyToNullConverter)*/
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory
                .createWithScheduler(Schedulers.from(promise.executor())))
            .baseUrl("")
            .client(client)
            .build()
    }
}