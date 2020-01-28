package com.nesstbase

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import promise.commons.Promise

@Module
object DependenciesModule {

    @Provides
    @JvmStatic
    fun promise(): Promise = Promise.instance()

    @Provides
    @AppScope
    @JvmStatic
    fun provideGson(): Gson {
        return Gson()
    }

}