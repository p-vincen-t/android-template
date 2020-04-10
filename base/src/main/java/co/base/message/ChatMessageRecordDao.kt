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

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.app.common.ID
import co.base.BaseDao

@Dao
interface ChatMessageRecordDao : BaseDao<ChatMessageRecord> {

    @Query("SELECT * FROM chats")
    @Transaction
    fun getMessages(): List<ChatMessageToChatMessageOneToOneRelationship>

    @Query("SELECT * FROM chats WHERE senderId == :userId ORDER BY sentTime DESC")
    @Transaction
    fun getMessages(userId: ID): List<ChatMessageToChatMessageOneToOneRelationship>

    @Query("SELECT * FROM chats WHERE id >= :skip LIMIT :take")
     fun getPaginatedMessages(skip: Int, take: Int): List<ChatMessageToChatMessageOneToOneRelationship>

    @Query("SELECT * FROM chats WHERE senderId == :userId AND id >= :skip LIMIT :take")
    fun getPaginatedMessages(userId: ID, skip: Int, take: Int): List<ChatMessageToChatMessageOneToOneRelationship>

    @Query("SELECT * FROM chats AS c WHERE c.senderId IN (SELECT userId FROM chat_users WHERE chat_users.userName LIKE  :query ) OR c.message LIKE :query")
    fun searchMessages(query: String): List<ChatMessageToChatMessageOneToOneRelationship>

}