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

package com.base

import androidx.multidex.MultiDexApplication
import com.app.domain.errors.NetworkError
import com.base.data.DaggerDataComponent
import com.base.data.DataComponent
import com.base.repos.DaggerReposComponent
import com.base.repos.ReposComponent
import com.base.session.DaggerSessionComponent
import com.base.session.SessionComponent
import com.base.utils.NetworkUtils
import okhttp3.Interceptor
import okhttp3.Response
import promise.commons.Promise
import promise.commons.data.log.LogUtil
import promise.commons.model.Message

const val NETWORK_ERROR_MESSAGE = "network_error_message"

open class AppBase : MultiDexApplication() {

    lateinit var sessionComponent: SessionComponent

    lateinit var reposComponent: ReposComponent

    lateinit var appComponent: AppComponent

    lateinit var dataComponent: DataComponent

    override fun onCreate() {
        super.onCreate()
        Promise.init(this,100)
        appComponent = DaggerAppComponent.create()

        dataComponent = DaggerDataComponent.factory()
            .create(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    if (NetworkUtils.getConnectivityStatus(this@AppBase.applicationContext) == NetworkUtils.TYPE_NOT_CONNECTED) {
                        val exception = NetworkError().apply {
                            request = chain.request().url
                        }
                        LogUtil.e(TAG, "Connection error: ", exception)
                        appComponent.promise().send(Message(NETWORK_ERROR_MESSAGE, exception))
                        throw exception
                    }
                    return chain.proceed(chain.request())
                }
            }, appComponent)

        sessionComponent = DaggerSessionComponent.builder()
            .dataComponent(dataComponent)
            .build()

        reposComponent = DaggerReposComponent.factory().create(
            sessionComponent.session(),
            dataComponent
        )
    }

    companion object {
        val TAG: String = LogUtil.makeTag(AppBase::class.java)
        const val TEMP_PREFERENCE_NAME = "prefs_temp"
    }
}