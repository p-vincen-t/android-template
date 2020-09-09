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

import androidx.annotation.IntDef
import promise.commons.AndroidPromise
import promise.commons.model.Message

abstract class NetworkConnection {

    companion object {

        const val SENDER = "__NetworkConnection"
        const val TYPE_WIFI = 1
        const val TYPE_MOBILE = 2
        const val TYPE_NOT_CONNECTED = 0
        @IntDef(
            TYPE_MOBILE,
            TYPE_WIFI,
            TYPE_NOT_CONNECTED
        )
        @Retention(AnnotationRetention.RUNTIME)
        annotation class ConnectionType
    }

    fun isConnected() = connectedInterface() != TYPE_NOT_CONNECTED

    fun isNotConnected() = !isConnected()

    @ConnectionType
    abstract fun connectedInterface(): Int

    fun isMobile() = connectedInterface() == TYPE_MOBILE
    fun isWifi() = connectedInterface() == TYPE_WIFI

    abstract fun subscribeToConnectionChangeEvents()

    abstract fun getMacAddress(): String

    fun notifyNoConnection(message: Any = "You are not connected to the network") {
        AndroidPromise.instance().send(Message(SENDER, message))
    }
}