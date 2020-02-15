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

import co.app.common.models.Photo
import co.app.domain.message.*
import co.base.repos.RepoScope
import promise.commons.model.Result
import java.util.*
import javax.inject.Inject

@RepoScope
class FakeMessageRepositoryImpl @Inject constructor() : MessageRepository {
    override fun getMessageThreads(
        skip: Int,
        take: Int,
        response: Result<List<ChatThread>, MessagesError>
    ) {
        val chatUser = ChatUser("1", "name", Photo())
        response.response(promise.commons.model.List.generate(10) {
            ChatThread(
                chatUser,
                ChatMessage(chatUser, "message here", Date().time)
            )
        })
    }

    override fun getMessages(
        chatThread: ChatThread,
        skip: Int,
        take: Int,
        response: Result<List<ChatMessage>, MessagesError>
    ) {
        response.response(promise.commons.model.List.generate(10) {
            val chatUser = ChatUser(if (it % 2 == 0) "0" else "1", "name", Photo())
            ChatMessage(chatUser, "text here", Date().time)
        })
    }

    override fun sendMessage(chatMessage: ChatMessage, result: Result<Int, MessagesError>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}