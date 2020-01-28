package com.nesstbase

import androidx.multidex.MultiDexApplication
import com.nesstbase.repos.DaggerReposComponent
import com.nesstbase.repos.ReposComponent
import com.nesstbase.session.DaggerSessionComponent
import com.nesstbase.session.SessionComponent
import promise.commons.Promise

open class NesstBase : MultiDexApplication() {

    lateinit var sessionComponent: SessionComponent

    lateinit var reposComponent: ReposComponent

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Promise.init(this,100)
        appComponent = DaggerAppComponent.create()
        sessionComponent = DaggerSessionComponent.builder()
            .dataComponent(appComponent.dataComponent())
            .build()
        reposComponent = DaggerReposComponent.factory().create(
            sessionComponent.session(),
            appComponent.dataComponent()
        )
    }

    companion object {
        @JvmField
        val TEMP_PREFERENCE_NAME = "nesst_prefs_temp"
    }
}