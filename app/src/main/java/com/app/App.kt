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

package com.app

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.collection.ArrayMap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.domain.session.Session
import com.base.AppBase
import com.base.AppBase.Companion.TEMP_PREFERENCE_NAME
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.gson.Gson
import okhttp3.OkHttpClient
import promise.commons.Promise
import promise.commons.createInstance
import promise.commons.data.log.LogUtil
import java.util.*

class App : AppBase(), LifecycleObserver {

    lateinit var session: Session

    lateinit var promise: Promise

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
        promise = appComponent.promise()
        session = sessionComponent.session()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        manager.installedModules.forEach {
            registerModule(it)
        }
    }

    fun registerModule(module: String) {
        LogUtil.d(TAG, "registering module : ", module)
        if (modules.containsKey(module)) return
        val registrar =
            createInstance(Class.forName("$PACKAGE_NAME.$module.ModuleRegistrar").kotlin) as ModuleRegister
        registrar.register(this)
        modules[module] = registrar
    }

    fun isWalletModuleInstalled(): Boolean = manager.installedModules.contains(WALLET_FEATURE_NAME)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        inBackground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        inBackground = false
    }

    fun gson(): Gson = appComponent.gson()

    fun okHttpClient(): OkHttpClient = dataComponent.okHttpClient()

    companion object {

        private const val PACKAGE_NAME = BuildConfig.PACKAGE_NAME
        const val MESSAGING_FEATURE_NAME = "messaging"
        const val WALLET_FEATURE_NAME = "wallet"
        const val WALLET_FRAGMENT = "$PACKAGE_NAME.$WALLET_FEATURE_NAME.WalletFragment"
    }
}

internal const val LANG_EN = "en"

internal const val LANG_PL = "pl"

object LanguageHelper {
    lateinit var prefs: SharedPreferences
    var language: String
        get() = prefs.getString("language", LANG_EN)!!
        set(value) = prefs.edit().putString(LANG_EN, value).apply()

    fun init(ctx: Context) {
        prefs = ctx.getSharedPreferences(TEMP_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Get a Context that overrides the language selection in the Configuration instance used by
     * getResources() and getAssets() by one that is stored in the LanguageHelper preferences.
     *
     * @param ctx a base context to base the new context on
     */
    fun getLanguageConfigurationContext(ctx: Context): Context {
        val conf = getLanguageConfiguration()
        return ctx.createConfigurationContext(conf)
    }

    /**
     * Get an empty Configuration instance that only sets the language that is
     * stored in the LanguageHelper preferences.
     * For use with Context#createConfigurationContext or Activity#applyOverrideConfiguration().
     */
    private fun getLanguageConfiguration(): Configuration {
        val conf = Configuration()
        conf.setLocale(Locale.forLanguageTag(language))
        return conf
    }
}