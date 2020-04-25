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

package co.app.auth.base

import co.app.auth.domain.Session
import co.base.account.AccountModule
import co.app.AppComponent
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import promise.commons.AndroidPromise

@SessionScope
@Component( dependencies = [co.app.AppComponent::class], modules = [SessionModule::class, AccountModule::class, ApiModule::class])
interface SessionComponent {

    fun session(): Session
    fun promise(): AndroidPromise

    @Component.Factory
    interface Factory {
        fun create(
                   @BindsInstance okHttpClient: OkHttpClient,
                   @BindsInstance url:String,
                   appComponent: co.app.AppComponent
        ): SessionComponent
    }
}