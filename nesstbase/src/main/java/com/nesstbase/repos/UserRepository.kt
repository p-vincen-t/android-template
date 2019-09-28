package com.nesstbase.repos

import com.nesstbase.apis.AuthApi
import com.nesstbase.auth.User
import com.nesstbase.errors.AuthError
import com.nesstbase.auth.Device
import org.json.JSONObject
import promise.commons.pref.Preferences
import promise.commons.util.DoubleConverter
import promise.model.repo.AbstractAsyncIDataStore
import promise.model.repo.AbstractSyncIDataStore
import promise.model.store.PreferenceStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val SESSION_PREFERENCES_NAME = "session_pref"

const val MOBILE_DEVICE_ID_KEY = "mobile_device_id_key"

val preferences: Preferences by lazy { Preferences(SESSION_PREFERENCES_NAME) }

val devicePreferenceStore: PreferenceStorage<Device> by lazy {
    PreferenceStorage(SESSION_PREFERENCES_NAME, object:
        DoubleConverter<Device, JSONObject, JSONObject> {
        override fun deserialize(e: JSONObject?): Device {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun serialize(t: Device?): JSONObject {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    })
}

class AsyncUserRepository constructor(private val authApi: AuthApi) :
    AbstractAsyncIDataStore<User>() {
    override fun one(
        res: (User, Any?) -> Unit,
        err: ((Exception) -> Unit)?,
        args: Map<String, Any?>?
    ) {
        if (args != null) {
            if (args.containsKey(LOGIN_IDENTIFIER_NAME) && args.containsKey(PASSWORD_IDENTIFIER_NAME)) {
                val deviceKey = preferences.getString(MOBILE_DEVICE_ID_KEY)
                if (deviceKey.isEmpty()) {
                    err?.invoke(AuthError(AuthError.MISSING_DEVICE_KEY))
                    return
                }
                authApi.login(
                    args[LOGIN_IDENTIFIER_NAME] as String,
                    args[PASSWORD_IDENTIFIER_NAME] as String,
                    deviceKey)
                    .enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {

                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {

                        }
                    })
            }

        } else err?.invoke(AuthError(AuthError.AUTH_CREDENTIALS_MISSING))
    }

    companion object {
        const val LOGIN_IDENTIFIER_NAME = "login_identifier"
        const val PASSWORD_IDENTIFIER_NAME = "password"
    }
}

class SyncUserRepository : AbstractSyncIDataStore<User>() {

    override fun one(args: Map<String, Any?>?): Pair<User?, Any?> {
        return Pair(null, null)
    }

}