package com.nesstbase.session

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import com.google.gson.Gson
import com.nesst.appdomain.errors.AuthError
import com.nesst.appdomain.session.*
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
    private val gson: Gson
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

    override fun user(): Account {
        return Account("Peter Vincent", "link here")
    }

    private fun login(token: String, result: Result<in Account, in AuthError>) {

    }


    fun registerDevice() {

    }

    override fun login(
        loginRequest: LoginRequest,
        result: Result<Account, in AuthError>
    ) {
        var deviceKey = preferences.getString(MOBILE_DEVICE_ID_KEY)
        if (deviceKey.isEmpty()) {
            deviceKey = "5d63013e46ad7a0010b378ed"
        }
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