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

package co.app.domain.message

import androidx.lifecycle.LiveData
import co.app.common.ID
import promise.commons.tx.Either
import promise.commons.tx.PromiseResult

const val DELIVERED = 0
const val SENT = 1
const val NOT_SENT = 2

interface MessageRepository {
    /**
     *  listen on ui
     */
    val messages: LiveData<List<ChatMessage>>

    /**
     * listen on ui
     */
    val chatThreads: LiveData<List<ChatThread>>

    /**
     * call on ui
     */
    fun getChatThreads(skip: Int, take: Int): Either<Any, MessagesError>

    /**
     * call on ui
     */
    fun getChatMessages(chatThread: ChatThread, skip: Int, take: Int): Either<Any, MessagesError>

    /**
     * call on service
     */
    fun getMessageChatThreads(skip: Int, take: Int): Either<List<ChatThread>, MessagesError>

    /**
     * call on both ui and service
     */
    fun getChatThread(id: ID) : Either<ChatThread, MessagesError>

    /**
     * call on service
     */
    fun getMessages(chatThread: ChatThread, skip: Int, take: Int): Either<List<ChatMessage>, MessagesError>

    /**
     * call on both ui and service
     */
    fun sendMessage(chatMessage: ChatMessage): Either<Int, MessagesError>

}