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

package co.app.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

object NetworkUtils {

    var NETWORK_LISTEN_ID = "network_listen_id"

    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0

    fun getMacAddress(context: Application): String {
        val manager = context.getSystemService(WIFI_SERVICE) as WifiManager?
        val info = manager!!.connectionInfo
        return info.macAddress
    }

}

