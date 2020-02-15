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

import promise.commons.model.Result

const val DELIVERED = 0
const val SENT = 1
const val NOT_SENT = 2

interface MessageRepository {

    fun getMessageThreads(skip: Int,
                          take: Int,
                          response: Result<List<ChatThread>, MessagesError>)
    fun getMessages(chatThread: ChatThread,
                    skip: Int,
                    take: Int,
                    response: Result<List<ChatMessage>, MessagesError>)
    fun sendMessage(chatMessage: ChatMessage,
                    result: Result<Int, MessagesError>)
}