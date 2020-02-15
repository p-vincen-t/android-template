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

package co.app.wallet.base.data

import com.google.gson.Gson
import co.app.wallet.base.data.api.ApiModule
import co.app.wallet.base.data.db.DatabaseModule
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import promise.commons.Promise

@Component(modules = [ApiModule::class, DatabaseModule::class])
@DataScope
interface DataComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance promise: Promise,
                   @BindsInstance gson: Gson,
                   @BindsInstance okHttpClient: OkHttpClient): DataComponent
    }
}