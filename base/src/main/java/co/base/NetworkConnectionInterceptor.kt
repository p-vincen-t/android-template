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

import co.app.common.NetworkConnection
import co.app.common.errors.NetworkError
import okhttp3.Interceptor
import okhttp3.Response
import promise.commons.data.log.LogUtil
import javax.inject.Inject

@AppScope
class NetworkConnectionInterceptor @Inject constructor(private val networkConnection: NetworkConnection) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (networkConnection.isNotConnected()) {
            val exception = NetworkError().apply {
                request = chain.request().url
            }
            LogUtil.e(TAG, "Connection error: ", exception)
            networkConnection.notifyNoConnection(exception)
            throw exception
        }
        return chain.proceed(chain.request())
    }

    companion object {
        val TAG: String = LogUtil.makeTag(NetworkConnectionInterceptor::class.java)
    }
}