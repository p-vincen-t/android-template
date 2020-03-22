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
import co.app.common.ID
import co.app.domain.message.ChatMessage
import co.base.common.PhotoRecord

@Entity(
    tableName = "chats",
    foreignKeys = [
        ForeignKey(
            entity = ChatUserRecord::class,
            parentColumns = ["userId"],
            childColumns = ["senderId"]
        ),
        ForeignKey(
            entity = ChatMessageRecord::class,
            parentColumns = ["uId"],
            childColumns = ["chatReplyId"]
        )
    ],
    indices = [
        Index(value = ["uId"], name = "uid_index", unique = true )
    ]

)
class ChatMessageRecord {

    @PrimaryKey(autoGenerate = true)
    var id = 0

    var uId: ID? = null

    @ColumnInfo(index = true)
    var senderId: ID? = null

    var message: String = ""

    @Embedded(prefix = "photo_")
    var photoRecord: PhotoRecord? = null

    var sentTime: Long = 0

    @ColumnInfo(index = true)
    var chatReplyId: ID? = null

    var forwardedFlag: Boolean = false

    fun toChatMessage(chatUserDao: ChatUserDao): ChatMessage =
        ChatMessage(chatUserDao.getChatUser(senderId!!).toChatUser(), message, sentTime).apply {

        }
}