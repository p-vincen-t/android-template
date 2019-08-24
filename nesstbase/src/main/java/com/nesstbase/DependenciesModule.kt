package com.nesstbase

import com.nesstbase.apis.AuthApi
import com.nesstbase.apis.retrofit
import com.nesstbase.auth.User
import com.nesstbase.repos.AsyncUserRepository
import com.nesstbase.repos.SyncUserRepository
import com.nesstbase.scopes.AppScope
import dagger.Module
import dagger.Provides
import promise.Promise
import promisemodel.repo.StoreRepository

@Module
object DependenciesModule {

    @Provides
    @AppScope
    @JvmStatic
    fun promise(): Promise = Promise.instance()

    @Provides
    @AppScope
    @JvmStatic
    fun authApi(): AuthApi = retrofit().create(AuthApi::class.java)

    @Provides
    @AppScope
    @JvmStatic
    fun userRepository(authApi: AuthApi): StoreRepository<User> =
        StoreRepository.createClass(
        SyncUserRepository::class,
        AsyncUserRepository::class,
        null,
        arrayOf(authApi)
    )
}