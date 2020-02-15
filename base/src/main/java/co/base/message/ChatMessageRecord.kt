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

import androidx.room.*
import co.app.domain.message.ChatMessage
import co.app.domain.message.ChatUser

@Entity(tableName = "chats")
class ChatMessageRecord {

    @PrimaryKey(autoGenerate = true)
    var id = 0
    @ColumnInfo(index = true)
    var uId: String = ""

    @ColumnInfo(index = true)
    var senderId: String? = ""

    var sender: ChatUser? = null
    var message: String = ""
    var sentTime: Long = 0
    @Relation(parentColumn = "" , entity = ChatMessageRecord::class, entityColumn ="")
    var chatMessageReplyRecord: ChatMessageRecord? = null
    var forwardedFlag: Boolean = false

    fun toChatMessage(): ChatMessage = ChatMessage(
        sender!!,
        message,
        sentTime
    ).apply {
        if (chatMessageReplyRecord != null) {
            chatMessageReply = chatMessageReplyRecord!!.toChatMessage()
        }
        forwarded = forwardedFlag
    }
}