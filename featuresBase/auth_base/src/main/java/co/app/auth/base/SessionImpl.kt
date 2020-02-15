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

package co.app.auth.base

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import co.app.auth.domain.AuthError
import co.app.auth.domain.LoginRequest
import co.app.auth.domain.RegistrationRequest
import co.app.auth.domain.Session
import co.app.domain.session.Device
import com.google.gson.Gson
import org.json.JSONObject
import promise.commons.Promise
import promise.commons.model.Result
import promise.commons.pref.Preferences
import promise.commons.util.DoubleConverter
import promise.model.store.PreferenceStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@SuppressLint("HardwareIds")
private fun getDeviceInfo(promise: Promise): Device {

    val manager =
        promise.context().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    val info = manager!!.connectionInfo
    val address = info.macAddress
    return Device(
        "mobile",
        "mobile description",
        true,
        address
    )
}

private const val SESSION_PREFERENCES_NAME = "session_pref"

private const val MOBILE_DEVICE_ID_KEY = "device_id"

@SessionScope
class SessionImpl @Inject constructor(
    private val authApi: AuthApi,
    private val gson: Gson,
    private val promise: Promise
) : Session {

    private val preferences: Preferences by lazy {
        Preferences(SESSION_PREFERENCES_NAME)
    }

    private val devicePreferenceStore: PreferenceStorage<Device> by lazy {
        PreferenceStorage(
            SESSION_PREFERENCES_NAME,
            object : DoubleConverter<Device, JSONObject, JSONObject> {
                override fun deserialize(e: JSONObject): Device =
                    gson.fromJson(e.toString(), Device::class.java)

                override fun serialize(t: Device): JSONObject = JSONObject(gson.toJson(t))
            })
    }

    private fun login(token: String, result: Result<Boolean, in AuthError>) {

    }


    fun registerDevice() {

    }

    override fun login(
        loginRequest: LoginRequest,
        result: Result<Boolean, in AuthError>
    ) {
        var deviceKey = preferences.getString(MOBILE_DEVICE_ID_KEY)
        if (deviceKey.isEmpty()) {
            deviceKey = "5d63013e46ad7a0010b378ed"
        }
        promise.execute({
            result.response(true)
        }, 2000)

        authApi.login(
            loginRequest
        )
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {


                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            })
    }

    override fun resetPassword(
        resetPasswordRequest: String,
        result: Result<Boolean, in AuthError>
    ) {
        authApi.resetPassword(resetPasswordRequest).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }

    override fun register(
        registrationRequest: RegistrationRequest,
        result: Result<Boolean, in AuthError>
    ) {

    }

}