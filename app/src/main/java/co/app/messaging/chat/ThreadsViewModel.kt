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

package co.app.messaging.chat

import androidx.lifecycle.LiveData
import co.app.BaseViewModel
import co.app.domain.message.ChatThread
import co.app.domain.message.MessageRepository
import co.app.domain.message.MessagesError
import promise.commons.data.log.LogUtil
import promise.commons.tx.PromiseResult

class ThreadsViewModel(
    private val messageRepository: MessageRepository
) : BaseViewModel() {

    fun threads(): LiveData<List<ChatThread>> = messageRepository.chatThreads

    fun loadThreads() {
        pageThreads(0, 20)
    }

    fun pageThreads(skip: Int, take: Int) =
        messageRepository.getChatThreads(skip, take)
            .foldOnUI(PromiseResult<Any, Throwable>()
                .withCallback {
                    LogUtil.d(TAG, "threads", it)
                })

    companion object {
        val TAG: String = LogUtil.makeTag(ThreadsViewModel::class.java)
    }
}