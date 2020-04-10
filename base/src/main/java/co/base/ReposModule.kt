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

import co.app.domain.message.ChatMessage
import co.app.domain.message.MessageRepository
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.domain.message.ChatDatabase
import co.app.common.search.SearchDatabase
import co.app.domain.message.ChatApi
import co.base.message.AsyncMessageRepo
import co.base.message.MessageRepositoryImpl
import co.base.search.SearchRepositoryImpl
import co.base.search.SyncSearchRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import promise.model.Repository
import promise.model.StoreRepository

@Module
abstract class RepoBinders {
    @Binds
    abstract fun bindMessageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository

    @Binds
    abstract fun bindSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository
}

@Module
object ReposModule {

    @Provides
    @JvmStatic
    fun provideMessageRepo(
        appDatabase: ChatDatabase,
        chatApi: ChatApi
    ): Repository<ChatMessage> =
        StoreRepository.async(
            AsyncMessageRepo::class,
            arrayOf(appDatabase, chatApi)
        )

    @Provides
    @JvmStatic
    fun provideSearchRepo(searchDatabase: SearchDatabase): Repository<Search> =
        StoreRepository.sync(
            SyncSearchRepo::class,
            arrayOf(searchDatabase)
        )
}