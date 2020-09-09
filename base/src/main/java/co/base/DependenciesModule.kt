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

package co.base

import android.app.Application
import co.app.common.NetworkConnection
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import promise.commons.AndroidPromise
import promise.commons.pref.Preferences

@Module
class DependenciesModule(private val application: Application) {

    @Provides
    @AppScope
    fun providePreferences(): Preferences = Preferences()

    @Provides
    @AppScope
    fun provideCompositeDisposable(promise: AndroidPromise): CompositeDisposable =
        promise.compositeDisposable

    @Provides
    @AppScope
    fun promise(): AndroidPromise {
        AndroidPromise.init(application, 100, BuildConfig.DEBUG)
        return AndroidPromise.instance()
    }

    @Provides
    @AppScope
    fun provideNetworkConnection(): NetworkConnection =
        NetworkConnectionImpl(application)
}