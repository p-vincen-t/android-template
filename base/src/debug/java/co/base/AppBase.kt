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

import androidx.multidex.MultiDexApplication
import co.app.common.ID
import co.app.common.NetworkUtils
import co.app.common.account.AccountComponent
import co.app.common.account.DaggerAccountComponent
import co.app.common.account.UserAccount
import co.app.common.errors.NetworkError
import co.base.data.DaggerDataComponent
import co.base.data.DataComponent
import co.base.repos.DaggerReposComponent
import co.base.repos.ReposComponent
import com.facebook.stetho.Stetho
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.instacart.library.truetime.TrueTimeRx
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.Response
import promise.commons.AndroidPromise
import promise.commons.data.log.LogUtil
import promise.commons.model.Message
import javax.inject.Inject

const val NETWORK_ERROR_MESSAGE = "network_error_message"

open class AppBase : MultiDexApplication() {

    lateinit var userAccount: UserAccount

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var promise: AndroidPromise

    lateinit var accountComponent: AccountComponent

    lateinit var reposComponent: ReposComponent

    lateinit var appComponent: AppComponent

    lateinit var dataComponent: DataComponent

    lateinit var apiUrl: String

    override fun onCreate() {
        super.onCreate()
        AndroidPromise.init(this, 100, BuildConfig.DEBUG)
        appComponent = DaggerAppComponent.factory().create(
            GsonBuilder()
                .registerTypeAdapter(ID::class.java, ID.IDTypeAdapter::class.java)
                .setPrettyPrinting()
                .create()
        )
        appComponent.inject(this)
        compositeDisposable.add(
            TrueTimeRx.build()
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.from(promise.executor()))
                .subscribe(
                    {
                        LogUtil.d(
                            TAG,
                            "TrueTime was initialized and we have a time: $it"
                        )
                    }
                ) { throwable: Throwable -> throwable.printStackTrace() }
        )
        initUserAccount()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    fun initUserAccount() {
        accountComponent = DaggerAccountComponent.factory()
            .create(appComponent.gson(), appComponent.promise())
        userAccount = accountComponent.userAccount()
        dataComponent = DaggerDataComponent.factory()
            .create(userAccount, object : Interceptor {
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
        apiUrl = dataComponent.apiUrl().toString()
        reposComponent = DaggerReposComponent.factory().create(
            userAccount,
            dataComponent
        )
    }

    override fun onTerminate() {
        AndroidPromise.instance().terminate()
        super.onTerminate()
    }

    companion object {
        val TAG: String = LogUtil.makeTag(AppBase::class.java)
        const val TEMP_PREFERENCE_NAME = "prefs_temp"
    }
}