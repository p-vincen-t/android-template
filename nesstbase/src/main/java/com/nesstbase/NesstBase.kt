package com.nesstbase

import androidx.multidex.MultiDexApplication
import promise.commons.Promise

open class NesstBase : MultiDexApplication() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Promise.init(this,100)
        appComponent = DaggerAppComponent.create()
    }

    companion object {
        @JvmField
        val TEMP_PREFERENCE_NAME = "nesst_prefs_temp"
    }
}