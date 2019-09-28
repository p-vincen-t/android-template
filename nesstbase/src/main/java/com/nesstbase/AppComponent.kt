package com.nesstbase

import com.nesstbase.auth.Session
import com.nesstbase.scopes.AppScope
import dagger.Component
import promise.commons.Promise

@Component(modules = [DependenciesModule::class])
@AppScope
interface AppComponent {
    fun promise(): Promise
    fun session(): Session
}