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

import co.app.common.AppUser
import co.app.common.ID
import co.app.common.photo.Photo
import co.app.domain.message.ChatMessage
import co.app.domain.message.ChatThread
import co.app.domain.message.MessageRepository
import co.app.domain.message.MessagesError
import co.base.repos.RepoScope
import promise.commons.data.log.LogUtil
import promise.commons.tx.PromiseResult
import promise.model.StoreRepository
import java.util.*
import javax.inject.Inject

@RepoScope
class MessageRepositoryImpl @Inject constructor(private val repository: StoreRepository<ChatMessage>) :
    MessageRepository {
    override fun getMessageThreads(
        skip: Int,
        take: Int,
        response: PromiseResult<List<ChatThread>, MessagesError>
    ) {
        LogUtil.d(TAG, "called get threads")
        val appUser = AppUser(
            ID.from(UUID.randomUUID().toString()), "username",
            Photo()
        )
            response.response(promise.commons.model.List.generate(40) {
                ChatThread(
                    ID.from(UUID.randomUUID().toString()),appUser,
                    ChatMessage(appUser, "message", Date().time))
            })
    }

    override fun getThread(id: ID, response: PromiseResult<ChatThread, MessagesError>) {
        LogUtil.d(TAG, "called get thread")
        val appUser = AppUser(
            ID.from(UUID.randomUUID().toString()), "username",
            Photo()
        )
        response.response(ChatThread(
            ID.from(UUID.randomUUID().toString()),appUser,
            ChatMessage(appUser, "message", Date().time)))
    }

    override fun getMessages(
        chatThread: ChatThread,
        skip: Int,
        take: Int,
        response: PromiseResult<List<ChatMessage>, MessagesError>
    ) {

    }

    override fun sendMessage(chatMessage: ChatMessage, result: PromiseResult<Int, MessagesError>) {

    }

    companion object {
        val TAG: String = LogUtil.makeTag(MessageRepositoryImpl::class.java)
    }
}