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

import android.content.Intent
import co.app.BindService
import co.app.domain.message.ChatMessage
import co.app.domain.message.MessageRepository
import promise.commons.data.log.LogUtil
import javax.inject.Inject

class ChatService : BindService<ChatService>() {

    override fun getService(): ChatService = this

    @Inject
    lateinit var messageRepository: MessageRepository

    override fun onCreate() {
        super.onCreate()
        DaggerChatComponent.builder()
            .reposComponent(app.reposComponent)
            .build()
            .inject(this)

    }

    fun sendMessage(chatMessage: ChatMessage) {
        messageRepository.sendMessage(chatMessage)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.e(TAG, "service started")
        return START_STICKY
    }

    fun onMessageReceived(chatMessage: ChatMessage) {}


    companion object {
        val TAG: String = LogUtil.makeTag(ChatService::class.java)
    }
}
