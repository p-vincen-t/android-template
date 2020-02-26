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

package co.base.repos

import co.app.domain.message.ChatMessage
import co.app.domain.message.MessageRepository
import co.base.message.AsyncMessageRepo
import co.base.message.ChatMessageRecordDao
import co.base.message.MessageRepositoryImpl
import co.base.message.SyncMessageRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import promise.model.repo.StoreRepository

@Module
abstract class RepoBinders {
    @Binds
    abstract fun bindMessageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository
}

@Module
object ReposModule {

    @Provides
    @JvmStatic
    fun provideMessageRepo(chatMessageRecordDao: ChatMessageRecordDao): StoreRepository<ChatMessage> =
        StoreRepository.of(
            SyncMessageRepo::class,
            AsyncMessageRepo::class,
            arrayOf(chatMessageRecordDao),
            arrayOf(chatMessageRecordDao)
        )
}