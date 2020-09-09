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

import co.app.common.ID
import co.app.common.photo.PhotoDatabase
import co.app.domain.message.ChatMessage
import promise.commons.model.Identifiable
import promise.database.*
import java.util.*

@Entity(
    tableName = "chats"
)
class ChatMessageRecord @SuppressWarnings("unused") constructor() : Identifiable<Int> {

    var view: String? = null

    @Ignore
    val other: Any? = null

    var subject: String = ""

    var name: String = ""

    var message: String = ""

    var email: String = ""

    @PrimaryKeyAutoIncrement
    var chatId = 0

    @Index
    var uId: ID? = null

    @Index
    @ForeignKey(referencedEntity = ChatUserRecord::class, referencedEntityColumnName = "userId")
    var senderId: ID? = null

    var description: String = ""

    var photoIds: Array<ID>? = null

    var sentTime: Date? = null

    @Index
    @ForeignKey(referencedEntity = ChatMessageRecord::class, referencedEntityColumnName = "uId")
    var chatReplyId: ID? = null

    @Ignore
    var chatReplyMessage: ChatMessageRecord? = null

    var forwardedFlag: Boolean = false

    fun isForwardedFlag() = forwardedFlag

    fun toChatMessage(chatUserDao: ChatUserDao, photoRecordDao: PhotoDatabase): ChatMessage {
        val user = chatUserDao.getChatUser(senderId!!)!!.toChatUser(photoRecordDao)
        return ChatMessage(user, message, sentTime!!.time).apply {
            chatDescription = description
            fromCurrentUser = senderId!!.id == user.userId.id
            forwarded = forwardedFlag
            if (photoIds != null) photos =
                photoRecordDao.getPhotosByRef("chat_photos", photoIds!!)
            if (chatMessageReply != null) chatMessageReply =
                chatReplyMessage!!.toChatMessage(chatUserDao, photoRecordDao)
        }
    }

    override fun getId(): Int = chatId

    override fun setId(t: Int) {
        this.chatId = t;
    }

}