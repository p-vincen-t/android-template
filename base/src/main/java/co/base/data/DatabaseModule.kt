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

import co.base.message.ChatMessageRecordDao
import co.base.message.ChatUserDao
import co.base.message.MessageThreadRecordDao
import co.base.search.SearchRecordTable
import dagger.Module
import dagger.Provides
import promise.commons.AndroidPromise

private const val DB_NAME = "app_db"

@Module
object DatabaseModule {

    @Provides
    @JvmStatic
    fun provideDatabase(promise: AndroidPromise): AppDatabase =
        AppDatabase(
            promise.context(),
            DB_NAME
        )

    @Provides
    @JvmStatic
    @DataScope
    fun provideSearchRecordTable(): SearchRecordTable =
        BehaviourDatabase.searchRecordTable

    @Provides
    @JvmStatic
    fun provideChatUserDao(database: AppDatabase): ChatUserDao = database.chatUserDao()

    @Provides
    @JvmStatic
    fun provideChatMessageDao(database: AppDatabase): ChatMessageRecordDao =
        database.chatMessageDao()

    @Provides
    @JvmStatic
    fun provideMessageThreadDao(database: AppDatabase): MessageThreadRecordDao =
        database.messageThreadDao()
}