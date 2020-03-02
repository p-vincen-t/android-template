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

package co.base.data

import co.app.common.account.UserAccount
import com.google.gson.Gson
import co.base.AppComponent
import co.base.message.ChatMessageRecordDao
import co.base.message.ChatUserDao
import co.base.search.SearchRecordTable
import dagger.BindsInstance
import dagger.Component
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import promise.commons.Promise

@DataScope
@Component(
    dependencies = [AppComponent::class],
    modules = [ApiModule::class, DatabaseModule::class]
)

interface DataComponent {
    fun gson(): Gson
    fun promise(): Promise
    fun chatMessageDao(): ChatMessageRecordDao
    fun chatUserDao(): ChatUserDao
    fun searchRecordTable(): SearchRecordTable
    fun okHttpClient(): OkHttpClient
    fun apiUrl(): HttpUrl
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance userAccount: UserAccount,
                   @BindsInstance interceptor: Interceptor,
                   appComponent: AppComponent): DataComponent
    }
}