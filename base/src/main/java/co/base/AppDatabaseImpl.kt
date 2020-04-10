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

package co.base

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import co.app.common.ID
import co.app.common.account.AppUser
import co.app.common.photo.Photo
import co.app.common.photo.PhotoDatabase
import co.app.common.search.Search
import co.app.domain.message.ChatDatabase
import co.app.domain.message.ChatMessage
import co.app.domain.message.ChatThread
import co.base.message.*
import org.json.JSONObject
import promise.commons.data.log.LogUtil
import promise.commons.model.function.FilterFunction
import promise.commons.util.DoubleConverter
import promise.model.PreferenceKeyStore
import java.util.*

private const val THREADS_KEY = "threads"

@Database(
    entities = [
        ChatMessageRecord::class,
        ChatUserRecord::class
    ],
    version = 1
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabaseImpl : RoomDatabase(), ChatDatabase {

    abstract fun chatMessageDao(): ChatMessageRecordDao

    abstract fun messageThreadDao(): MessageThreadRecordDao

    abstract fun chatUserDao(): ChatUserDao

    override fun getMessages(thread: ChatThread): List<ChatMessage> =
        chatMessageDao().getMessages(thread.user.userId)
            .filter { it.chatMessage != null }
            .map { message ->
                message.chatMessage.apply {
                    this!!.chatReplyMessage = message.replyChatMessage
                }
            }.map { chatMessageRecord ->
                chatMessageRecord!!.toChatMessage(chatUserDao(),
                    photoDatabase
                )
            }

    override fun getPaginatedMessages(
        thread: ChatThread,
        skip: Int,
        take: Int
    ): List<ChatMessage> = chatMessageDao().getPaginatedMessages(thread.user.userId, skip, take)
        .filter { it.chatMessage != null }
        .map { message ->
            message.chatMessage.apply {
                this!!.chatReplyMessage = message.replyChatMessage
            }
        }.map { chatMessageRecord ->
            chatMessageRecord!!.toChatMessage(chatUserDao(),
                photoDatabase
            )
        }

    override fun getChatPhotos(chatID: ID): List<Photo> {
        TODO("Not yet implemented")
    }

    override fun getUserPhoto(userId: ID): Photo? {
        TODO("Not yet implemented")
    }

    override fun invalidateMessages(messages: List<ChatMessage>) {
        TODO("Not yet implemented")
    }

    override fun getMessageThreads(): List<ChatThread> {
        val threads = messageThreadDao().getMessageThreads()
        if (threads != null) {
            val messages = threads.map {
                val user = it.chatUser!!.toChatUser(photoDatabase)
                val message = it.chatMessages!!.last()
                ChatThread(
                    ID.from(UUID.randomUUID().toString()),
                    user,
                    message.description,
                    message.toChatMessage(chatUserDao(),
                        photoDatabase
                    )
                )
            }
            threadStore.clear(
                THREADS_KEY
            )
            threadStore.save(THREADS_KEY, promise.commons.model.List(messages))
            return messages
        }
        return emptyList()
    }

    override fun getMessageThread(id: ID): ChatThread? {
        val threads = threadStore!![THREADS_KEY].all()
        if (threads.isEmpty()) return null
        return threads.find {
            it!!.id == id
        }
    }

    override fun getMessages(): List<ChatMessage> = chatMessageDao().getMessages()
        .filter { it.chatMessage != null }
        .map { message ->
            message.chatMessage.apply {
                this!!.chatReplyMessage = message.replyChatMessage
            }
        }.map { chatMessageRecord ->
            chatMessageRecord!!.toChatMessage(chatUserDao(),
                photoDatabase
            )
        }

    override fun search(search: Search): List<ChatThread> {
        if (search.query.endsWith("k")) return emptyList()
        return chatMessageDao().searchMessages(search.query)
            .asSequence()
            .map { relationship ->
                relationship.chatMessage?.apply {
                    chatReplyMessage = relationship.replyChatMessage
                }
            }
            .filterNotNull()
            .groupBy {
                it.senderId
            }.map {
                val message = it.value.last()
                ChatThread(
                    ID.generate(),
                    chatUserDao().getChatUser(it.key!!).toChatUser(photoDatabase),
                    "product",
                    message.toChatMessage(chatUserDao(),
                        photoDatabase
                    )
                )
            }
            .toList()

        /*//res(List(messages), null)
        return promise.commons.model.List.generate(30) {
            ChatMessage(if (it % 2 == 0) appUser else appUser2, "message", Date().time)
        }*/
    }

    companion object {

        val TAG: String = LogUtil.makeTag(AppDatabaseImpl::class.java)

        val converter: DoubleConverter<ChatThread, JSONObject, JSONObject> = object :
            DoubleConverter<ChatThread, JSONObject, JSONObject> {
            override fun deserialize(e: JSONObject): ChatThread {
                val user = AppUser(
                    ID.from(e.getString("usr")),
                    null,
                    null
                )
                return ChatThread(
                    ID.from(e.getString("id")),
                    user,
                    e.getString("dsc"),
                    ChatMessage(user, e.getString("mes"))
                )
            }

            override fun serialize(t: ChatThread): JSONObject =
                JSONObject().apply {
                    put("id", t.id.id)
                    put("usr", t.user.userId.id)
                    put("dsc", t.productOrServiceDescription)
                    put("mes", t.lastChatMessage.message)
                }
        }

        fun filterFunction(t: ChatThread): FilterFunction<JSONObject> =
            FilterFunction<JSONObject> { j ->
                t.id.id!! == j.getString("id") &&
                        t.user.userId.id == j.getString("usr")
            }

        @Volatile
        lateinit var threadStore: PreferenceKeyStore<ChatThread>

        @Volatile
        lateinit var photoDatabase: PhotoDatabase

        @Volatile
        var instance: AppDatabaseImpl? = null

        private var LOCK = Any()

        operator fun invoke(
            application: Application,
            dbName: String,
            photoTable: PhotoDatabase,
            prefName: String
        ): AppDatabaseImpl = instance
            ?: synchronized(LOCK) {
                instance ?: Room.databaseBuilder(
                        application.applicationContext,
                        AppDatabaseImpl::class.java,
                        dbName
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            LogUtil.i(TAG, "database created")
                        }
                    })
                    .build()
                    .also {
                        instance = it
                        photoDatabase = photoTable
                        threadStore = object : PreferenceKeyStore<ChatThread>(
                            prefName,
                            converter = converter
                        ) {
                            override fun findIndexFunction(t: ChatThread): FilterFunction<JSONObject> =
                                filterFunction(
                                    t
                                )
                        }
                    }
            }

        operator fun invoke(
            application: Application,
            photoTable: PhotoDatabase,
            prefName: String
        ): AppDatabaseImpl = instance
            ?: synchronized(LOCK) {
                instance ?: Room.inMemoryDatabaseBuilder(
                        application.applicationContext,
                        AppDatabaseImpl::class.java
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        instance = it
                        photoDatabase = photoTable
                        threadStore = object : PreferenceKeyStore<ChatThread>(
                            prefName,
                            converter = converter
                        ) {
                            override fun findIndexFunction(t: ChatThread): FilterFunction<JSONObject> =
                                filterFunction(
                                    t
                                )
                        }
                    }
            }
    }

}