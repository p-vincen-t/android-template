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

import co.app.common.account.UserAccount
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import promise.commons.AndroidPromise
import promise.commons.pref.Preferences

@Module
object AccountProvider {
    @Provides
    @JvmStatic
    @AccountScope
    fun provideAccount(preferences: Preferences,gson: Gson): UserAccount? {
        if (UserAccountImpl.ReadAccount.hasAccount(preferences))
            return UserAccountImpl.ReadAccount(preferences, gson)
        return null
    }

    @Provides
    @AccountScope
    @JvmStatic
    fun providePreferences(): Preferences = Preferences(SessionPrefName)
}

@Module
object AccountModule {

    @Provides
    @JvmStatic
    fun providePreferences(): Preferences = Preferences(SessionPrefName)

    @Provides
    @JvmStatic
    fun provideWriteAccount(preferences: Preferences,
                            promise: AndroidPromise, gson: Gson): UserAccount =
        UserAccountImpl.WriteAccount(preferences, promise, gson)

}