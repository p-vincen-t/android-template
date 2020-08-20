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
import promise.db.PromiseDatabase
import promise.db.criteria.Criteria

interface ChatMessageRecordDao {

    //    @Query("SELECT * FROM chats")
//    @Transaction
    fun getMessages(): List<ChatMessageToChatMessageOneToOneRelationship>

    //    @Query("SELECT * FROM chats WHERE senderId == :userId ORDER BY sentTime DESC")
//    @Transaction
    fun getMessages(userId: ID): List<ChatMessageToChatMessageOneToOneRelationship>

    //    @Query("SELECT * FROM chats WHERE id >= :skip LIMIT :take")
//    @Transaction
    fun getPaginatedMessages(
        skip: Int,
        take: Int
    ): List<ChatMessageToChatMessageOneToOneRelationship>

    //    @Query("SELECT * FROM chats WHERE senderId == :userId AND id >= :skip LIMIT :take")
//    @Transaction
    fun getPaginatedMessages(
        userId: ID,
        skip: Int,
        take: Int
    ): List<ChatMessageToChatMessageOneToOneRelationship>

    //    @Query("SELECT * FROM chats AS c WHERE c.senderId
//    IN (SELECT userId FROM chat_users WHERE chat_users.userName LIKE  :query ) OR c.message LIKE :query")
//    @Transaction
    fun searchMessages(query: String): List<ChatMessageToChatMessageOneToOneRelationship>

}

class ChatMessageRecordDaoImpl(private val promiseDatabase: PromiseDatabase) :
    ChatMessageRecordDao {

    override fun getMessages(): List<ChatMessageToChatMessageOneToOneRelationship> =
        promiseDatabase.tableOf(ChatMessageRecord::class.java).findAll().map {
            ChatMessageToChatMessageOneToOneRelationship().apply {
                chatMessage = it
                replyChatMessage = promiseDatabase.tableOf(ChatMessageRecord::class.java)
                    .findOne(ChatMessageRecordsTable.chatReplyIdColumn.with(it.uId!!.id))
            }
        }

    override fun getMessages(userId: ID): List<ChatMessageToChatMessageOneToOneRelationship> =
        promiseDatabase.tableOf(ChatMessageRecord::class.java).findAll(
            ChatMessageRecordsTable.senderIdColumn.with(userId.id)
            , ChatMessageRecordsTable.sentTimeColumn.descending()
        ).map {
            ChatMessageToChatMessageOneToOneRelationship().apply {
                chatMessage = it
                replyChatMessage = promiseDatabase.tableOf(ChatMessageRecord::class.java)
                    .findOne(ChatMessageRecordsTable.chatReplyIdColumn.with(it.uId!!.id))
            }
        }

    override fun getPaginatedMessages(
        skip: Int,
        take: Int
    ): List<ChatMessageToChatMessageOneToOneRelationship> =
        promiseDatabase.tableOf(ChatMessageRecord::class.java).find().paginateDescending(skip, take)
            .map {
                ChatMessageToChatMessageOneToOneRelationship().apply {
                    chatMessage = it
                    replyChatMessage = promiseDatabase.tableOf(ChatMessageRecord::class.java)
                        .findOne(ChatMessageRecordsTable.chatReplyIdColumn.with(it.uId!!.id))
                }
            }

    override fun getPaginatedMessages(
        userId: ID,
        skip: Int,
        take: Int
    ): List<ChatMessageToChatMessageOneToOneRelationship> {
        val chatMessagesTable = promiseDatabase.tableOf(ChatMessageRecord::class.java)
        val cursor = chatMessagesTable.query(
            chatMessagesTable.queryBuilder()
                .whereAnd(Criteria.equals(ChatMessageRecordsTable.senderIdColumn, userId.id))
                .skip(skip)
                .take(take)
        )
        val chats: ArrayList<ChatMessageToChatMessageOneToOneRelationship> = ArrayList()
        while (cursor.moveToNext()) {
            val message = chatMessagesTable.deserialize(cursor)
            chats.add(ChatMessageToChatMessageOneToOneRelationship().apply {
                chatMessage = message
                replyChatMessage = chatMessagesTable
                    .findOne(ChatMessageRecordsTable.chatReplyIdColumn.with(message.uId!!.id))
            })
        }
        cursor.close()
        return chats
    }

    override fun searchMessages(query: String): List<ChatMessageToChatMessageOneToOneRelationship> {
        val chatUsersTable = promiseDatabase.tableOf(ChatUserRecord::class.java)
        val chatMessagesTable = promiseDatabase.tableOf(ChatMessageRecord::class.java)
        val userIds: promise.commons.model.List<Any> = promise.commons.model.List()
        val idsCursor = chatUsersTable.query(
            chatUsersTable.queryBuilder()
                .select(ChatUserRecordsTable.userIdColumn)
                .whereAnd(Criteria.contains(ChatUserRecordsTable.userNameColumn, query))
        )
        while (idsCursor.moveToNext())
            userIds.add(idsCursor.getString(ChatUserRecordsTable.userIdColumn.getIndex(idsCursor)))
        idsCursor.close()
        val cursor = chatMessagesTable.query(
            chatMessagesTable.queryBuilder()
                .whereAnd(
                    Criteria.`in`(ChatMessageRecordsTable.senderIdColumn, userIds)
                        .or(Criteria.contains(ChatMessageRecordsTable.messageColumn, query))
                )
        )
        val chats: ArrayList<ChatMessageToChatMessageOneToOneRelationship> = ArrayList()
        while (cursor.moveToNext()) {
            val message = chatMessagesTable.deserialize(cursor)
            chats.add(ChatMessageToChatMessageOneToOneRelationship().apply {
                chatMessage = message
                replyChatMessage = chatMessagesTable
                    .findOne(ChatMessageRecordsTable.chatReplyIdColumn.with(message.uId!!.id))
            })
        }
        cursor.close()
        return chats
    }
}