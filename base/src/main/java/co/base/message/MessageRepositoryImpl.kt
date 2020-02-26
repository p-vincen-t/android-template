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

package co.base.message

import co.app.domain.message.ChatMessage
import co.app.domain.message.ChatThread
import co.app.domain.message.MessageRepository
import co.app.domain.message.MessagesError
import co.base.repos.RepoScope
import promise.commons.model.Result
import promise.model.repo.StoreRepository
import javax.inject.Inject

@RepoScope
class MessageRepositoryImpl @Inject constructor(private val repository: StoreRepository<ChatMessage>) :
    MessageRepository {
    override fun getMessageThreads(
        skip: Int,
        take: Int,
        response: Result<List<ChatThread>, MessagesError>
    ) {

    }

    override fun getMessages(
        chatThread: ChatThread,
        skip: Int,
        take: Int,
        response: Result<List<ChatMessage>, MessagesError>
    ) {

    }

    override fun sendMessage(chatMessage: ChatMessage, result: Result<Int, MessagesError>) {

    }
}