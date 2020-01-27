package com.nesstbase

import com.google.gson.Gson
import com.nesstbase.data.DataComponent
import dagger.Component
import promise.commons.Promise

@Component(modules = [DependenciesModule::class])
@AppScope
interface AppComponent {
    fun promise(): Promise
    fun gson(): Gson
    fun dataComponent(): DataComponent
}