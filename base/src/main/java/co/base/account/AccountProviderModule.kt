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

package co.base.account

import co.app.common.account.AccountManager
import co.app.common.account.UserAccount
import co.base.AppScope
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import promise.commons.AndroidPromise
import promise.commons.pref.Preferences

@Module
abstract class AccountManagerModule {
    @Binds
    abstract fun bindAccountManager(accountManagerImpl: AccountManagerImpl): AccountManager
}

@Module
object AccountProviderModule {
    @Provides
    @JvmStatic
    fun provideAccount(gson: Gson): UserAccount? {
        val pref = Preferences(SessionPrefName)
        if (UserAccountImpl.ReadAccount.hasAccount(pref))
            return UserAccountImpl.ReadAccount(pref, gson)
        return null
    }

}

@Module
object AccountModule {

    @Provides
    @JvmStatic
    fun provideWriteAccount(promise: AndroidPromise, gson: Gson): UserAccount =
        UserAccountImpl.WriteAccount(Preferences(SessionPrefName), promise, gson)

}