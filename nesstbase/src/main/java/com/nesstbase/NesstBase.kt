package com.nesstbase

import android.app.Application
import promise.Promise

open class NesstBase : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Promise.init(this).threads(100)
        appComponent = DaggerAppComponent.create()
    }
}