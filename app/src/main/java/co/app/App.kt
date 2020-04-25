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

package co.app

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.collection.ArrayMap
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import co.app.app.*
import co.app.common.ID
import co.app.common.NetworkUtils
import co.app.common.account.UserAccount
import co.app.common.errors.NetworkError
import co.app.settings.ThemePreference
import co.base.account.AccountComponent
import co.base.account.DaggerAccountComponent
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import promise.commons.AndroidPromise
import promise.commons.createInstance
import promise.commons.data.log.LogUtil
import promise.commons.model.Message
import javax.inject.Inject
import kotlin.collections.set

const val NETWORK_ERROR_MESSAGE = "network_error_message"

class App : MultiDexApplication(), LifecycleObserver {

    inline fun <reified T : Service> connectService(
        noinline result: (T) -> Unit,
        noinline error: ((Throwable) -> Unit)? = null
    ) {
        val intent = Intent(this, T::class.java)
        bindService(
            intent,
            object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {
                    error?.invoke(Exception("service disconnected"))
                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val serviceBinder = service as BindService<Service>.LocalBinder
                    result(serviceBinder.service as T)
                }
            },
            Context.BIND_AUTO_CREATE
        )
    }

    val themePreferenceRepo: ThemePreference by lazy {
        ThemePreference()
    }

    private val manager: SplitInstallManager by lazy {
        SplitInstallManagerFactory.create(this)
    }

    var inBackground = false

    val modules: ArrayMap<String, ModuleRegister> = ArrayMap()

    override fun attachBaseContext(base: Context) {
        LanguageHelper.init(base)
        val ctx = LanguageHelper.getLanguageConfigurationContext(base)
        super.attachBaseContext(ctx)
        SplitCompat.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidPromise.init(this, 100, BuildConfig.DEBUG)
        themePreferenceRepo.setTheme()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        TAG = LogUtil.makeTag(App::class.java)
        appComponent.inject(this)
        promise.execute {
            registerModule("app")
            manager.installedModules.forEach {
                registerModule(it)
            }
            EmojiCompat.init(
                FontRequestEmojiCompatConfig(
                    this,
                    FontRequest(
                        "com.google.android.gms.fonts",
                        "com.google.android.gms",
                        "Noto Color Emoji Compat",
                        R.array.com_google_android_gms_fonts_certs
                    )
                ).setReplaceAll(true)
            )
        }
    }

    fun registerModule(module: String) {
        LogUtil.d(TAG, "registering module : ", module)
        if (modules.containsKey(module)) return
        val registrar =
            createInstance(Class.forName("$PACKAGE_NAME.$module.ModuleRegistrar").kotlin) as ModuleRegister
        registrar.onRegister(this)
        modules[module] = registrar
    }

    fun isModuleInstalled(module: String): Boolean = manager.installedModules.contains(module)

    fun isWalletModuleInstalled(): Boolean = isModuleInstalled(WALLET_FEATURE_NAME)

    fun isAuthModuleInstalled(): Boolean = isModuleInstalled(AUTH_FEATURE_NAME)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        inBackground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        inBackground = false
    }

    fun gson(): Gson = appComponent.gson()

    fun okHttpClient(): OkHttpClient = dataComponent().okHttpClient()


    lateinit var TAG: String

    fun userAccount(): UserAccount? = accountComponent.userAccount()

    private var dataComponent: DataComponent? = null
    private var reposComponent: ReposComponent? = null

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var promise: AndroidPromise

    val accountComponent: AccountComponent by lazy {
        DaggerAccountComponent.factory()
            .create(appComponent.gson(), appComponent.promise())
    }

    fun reposComponent(): ReposComponent = reposComponent ?: DaggerReposComponent.factory().create(
        userAccount(),
        dataComponent()
    ).also {
        reposComponent = it
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(
            GsonBuilder()
                .registerTypeAdapter(ID::class.java, ID.IDTypeAdapter())
                .setPrettyPrinting()
                .create()
        )
    }

    fun dataComponent(): DataComponent = dataComponent ?: DaggerDataComponent.factory()
        .create(userAccount(), object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                if (NetworkUtils.getConnectivityStatus(this@App.applicationContext) == NetworkUtils.TYPE_NOT_CONNECTED) {
                    val exception = NetworkError().apply {
                        request = chain.request().url
                    }
                    LogUtil.e(TAG, "Connection error: ", exception)
                    appComponent.promise().send(Message(NETWORK_ERROR_MESSAGE, exception))
                    throw exception
                }
                return chain.proceed(chain.request())
            }
        }, appComponent).also { dataComponent = it }

    fun apiUrl(): String = dataComponent().apiUrl().toString()

    fun initUserAccount() {
        dataComponent = null
        reposComponent = null
    }

    fun initComponents() {
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
        if (co.base.BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)
    }


    override fun onTerminate() {
        AndroidPromise.instance().terminate()
        super.onTerminate()
    }

    companion object {

        const val TEMP_PREFERENCE_NAME = "prefs_temp"

        private const val PACKAGE_NAME = BuildConfig.PACKAGE_NAME
        const val WALLET_FEATURE_NAME = "wallet"

        const val WALLET_ACTIVITY = "$PACKAGE_NAME.$WALLET_FEATURE_NAME.WalletActivity"

        const val AUTH_FEATURE_NAME = "auth"
        const val AUTH_ACTIVITY = "$PACKAGE_NAME.$AUTH_FEATURE_NAME.AuthActivity"
    }
}


