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

package co.base.account

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import co.app.common.ID
import co.app.common.account.Device
import co.app.common.account.UserAccount
import co.app.common.photo.Photo
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import promise.commons.AndroidPromise
import promise.commons.model.function.FilterFunction
import promise.commons.pref.Preferences
import promise.commons.tx.PromiseResult
import promise.commons.util.DoubleConverter
import promise.model.KeyStore
import promise.model.PreferenceKeyStore
import javax.inject.Inject
import promise.commons.model.List as PromiseList

private const val MOBILE_DEVICE_ID_KEY = "device_id"
const val SessionPrefName = "session_pref"

@SuppressLint("HardwareIds")
fun getDeviceInfo(promise: AndroidPromise): Device {
    val manager =
        promise.context().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    val info = manager!!.connectionInfo
    val address = info.macAddress
    return DeviceImpl(
        "mobile",
        "mobile description",
        true,
        address,
        ""
    )
}

private fun devicePreferenceStore(gson: Gson): PreferenceKeyStore<Device> =
    object : PreferenceKeyStore<Device>(
        SessionPrefName,
        object : DoubleConverter<Device, JSONObject, JSONObject> {
            override fun deserialize(e: JSONObject): Device =
                gson.fromJson(e.toString(), Device::class.java)
            override fun serialize(t: Device): JSONObject = JSONObject(gson.toJson(t))
        }) {
        override fun findIndexFunction(t: Device): FilterFunction<JSONObject> =
            FilterFunction<JSONObject> {
                return@FilterFunction t.getId() == it.getString("device_id")
            }
    }

sealed class UserAccountImpl : UserAccount {
    abstract override var id: ID
    override var names: String = ""
    override var emailAddress: String = ""
    override var phoneNumber: String = ""
    override var device: List<Device> = listOf()

    @field:SerializedName("country")
    val country: String? = null

    @field:SerializedName("g_id")
    val gId: Any? = null

    @field:SerializedName("g_photo")
    val gPhoto: Any? = null

    @field:SerializedName("city")
    val city: String? = null

    @field:SerializedName("wishlist")
    val wishlist: String? = null

    @field:SerializedName("product_upload")
    val productUpload: Int? = null

    @field:SerializedName("skype")
    val skype: String? = null

    @field:SerializedName("password")
    val password: String? = null

    @field:SerializedName("user_type")
    val userType: String? = null

    @field:SerializedName("langlat")
    val langlat: String? = null

    @field:SerializedName("downloads")
    val downloads: String? = null

    @field:SerializedName("surname")
    val surname: String? = null

    @field:SerializedName("state")
    val state: String? = null

    @field:SerializedName("package_info")
    val packageInfo: String? = null

    @field:SerializedName("email")
    val email: String? = null

    @field:SerializedName("zip")
    val zip: String? = null

    @field:SerializedName("wallet")
    val wallet: Any? = null

    @field:SerializedName("address2")
    val address2: String? = null

    @field:SerializedName("address1")
    val address1: String? = null

    @field:SerializedName("last_login")
    val lastLogin: String? = null

    @field:SerializedName("facebook")
    val facebook: String? = null

    @field:SerializedName("email_verified_at")
    val emailVerifiedAt: Any? = null

    @field:SerializedName("google_plus")
    val googlePlus: String? = null

    @field:SerializedName("creation_date")
    val creationDate: String? = null

    @field:SerializedName("user_id")
    val userId: Int? = null

    @field:SerializedName("phone")
    val phone: String? = null

    @field:SerializedName("fb_id")
    val fbId: Any? = null

    @field:SerializedName("left_product_type")
    val leftProductType: String? = null

    @field:SerializedName("user_type_till")
    val userTypeTill: Any? = null

    @field:SerializedName("username")
    val username: String? = null

    inner class UserChildAccountImpl(override var id: ID) : UserAccount.UserChildAccount {
        override var photo: Photo
            get() = Photo()
            set(value) {}

    }

    class ReadAccount constructor(preferences: Preferences, gson: Gson) :
        UserAccountImpl() {
        override fun create(map: JSONObject): Unit = throw IllegalAccessError("Cant create account")
        override var childAccounts: List<UserAccount.UserChildAccount>?
            get() = promise.commons.model.List.generate(2) {
                UserChildAccountImpl(ID.generate())
            }
            set(value) {
                throw IllegalAccessException("cant set child accounts")
            }

        init {
            names = preferences.getString("names")
            emailAddress = preferences.getString("email_address")
            phoneNumber = preferences.getString("phone_number")
            devicePreferenceStore(gson)["devices", PromiseResult<KeyStore.Extras<Device>, Throwable>()
                .withCallback {
                    device = it.all()
                }]
        }

        companion object {
            fun hasAccount(preferences: Preferences): Boolean = preferences.getBoolean("isLoggedIn")
        }
        override var id: ID = ID.from(preferences.getString("id"))
    }

    class WriteAccount(
        private val preferences: Preferences,
        private val promise: AndroidPromise,
        private val gson: Gson
    ) : UserAccountImpl() {

        override var childAccounts: List<UserAccount.UserChildAccount>? = null

        override var id: ID
            get() = throw IllegalAccessException("cant read id of Write account")
            set(value) {}

        override fun create(map: JSONObject) {
            var deviceKey = map.getString(MOBILE_DEVICE_ID_KEY)
            if (deviceKey.isEmpty()) {
                deviceKey = "5d63013e46ad7a0010b378ed"
            }
            preferences.save(MOBILE_DEVICE_ID_KEY, deviceKey)

            devicePreferenceStore(gson = gson).save("devices",
                PromiseList.fromArray(
                    getDeviceInfo(promise)
                ), PromiseResult<Boolean, Throwable>()
                    .withCallback
                    {

                    })
            preferences.save("isLoggedIn", true)
        }
    }
}

