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

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import co.app.common.NetworkConnection

class NetworkConnectionImpl(private val context: Application) : NetworkConnection() {

    override fun connectedInterface(): Int {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @SuppressLint("MissingPermission") val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }
    @SuppressLint("MissingPermission", "HardwareIds")
    override fun getMacAddress(): String {
        val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val info = manager!!.connectionInfo
        return info.macAddress
    }

    override fun subscribeToConnectionChangeEvents() {
        //context.applicationContext.registerReceiver(ConnectionChangeReceiver())
    }

    class ConnectionChangeReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
        }
    }
}