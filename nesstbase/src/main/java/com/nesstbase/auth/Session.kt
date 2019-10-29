package com.nesstbase.auth

import android.content.Context
import androidx.collection.ArrayMap
import com.nesstbase.apis.AuthApi
import com.nesstbase.errors.AuthError
import com.nesstbase.scopes.AppScope
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import android.net.wifi.WifiManager
import promise.commons.Promise
import promise.commons.model.Result
import promise.commons.pref.Preferences
import promise.commons.util.DoubleConverter
import promise.model.store.PreferenceStorage


private fun getDeviceInfo(promise: Promise): Device {

    val manager = promise.context().getSystemService(Context.WIFI_SERVICE) as WifiManager?
    val info = manager!!.connectionInfo
    val address = info.macAddress
    return Device("mobile", "mobile description", true,address)
}

private const val SESSION_PREFERENCES_NAME = "session_pref"

private const val MOBILE_DEVICE_ID_KEY = "mobile_device_id_key"

private val preferences: Preferences by lazy { Preferences(SESSION_PREFERENCES_NAME) }

private val devicePreferenceStore: PreferenceStorage<Device> by lazy {
    PreferenceStorage(
        SESSION_PREFERENCES_NAME,
        object : DoubleConverter<Device, JSONObject, JSONObject> {
            override fun deserialize(e: JSONObject?): Device {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun serialize(t: Device?): JSONObject {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
}

/**
 * handles all authentication functionality
 *
 */
@AppScope
class Session @Inject constructor() {

    @Inject
    lateinit var authApi: AuthApi

    fun user(): Account {
        return Account("Peter Vincent", "dev4vin@gmail.com")
    }

    private fun login(token: String, result: Result<in Account, in AuthError>) {

    }

    fun login(
        identifier: String,
        password: String,
        result: Result<Account, AuthError>
    ) {
        var deviceKey = preferences.getString(MOBILE_DEVICE_ID_KEY)
        if (deviceKey.isEmpty()) {
            deviceKey = "5d63013e46ad7a0010b378ed"
        }
        authApi.login(
            identifier,
            password,
            deviceKey
        )
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {


                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            })
    }

    fun resetPassword(
        identifier: String,
        result: Result<Boolean, AuthError>
    ) {
        authApi.resetPassword(identifier).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }

    fun register(
        names: String,
        email: String,
        phone: String,
        password: String,
        result: Result<Boolean, AuthError>
    ) {

        authApi.register(ArrayMap<String, Any>().apply {
            put("names", names)
            put("email", email)
            put("phone", phone)
            put("password", password)
        }).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }

    fun registerDevice() {

    }
}