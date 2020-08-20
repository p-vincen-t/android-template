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

import co.app.common.ID
import co.app.common.account.AppUser
import co.app.common.photo.Photo
import co.app.common.photo.PhotoDatabase
import co.app.common.search.Search
import co.app.common.search.SearchDatabase
import co.app.domain.message.ChatDatabase
import co.app.domain.message.ChatMessage
import co.app.domain.message.ChatThread
import co.base.common.PhotoRecord
import co.base.common.PhotoRecordsTable
import co.base.message.*
import co.base.search.SearchRecord
import org.json.JSONObject
import promise.commons.model.List
import promise.commons.model.function.FilterFunction
import promise.commons.util.DoubleConverter
import promise.database.DatabaseEntity
import promise.db.FastDatabase
import promise.db.FastTable
import promise.db.PromiseDatabase
import promise.db.criteria.Criteria
import promise.model.IdentifiableList
import promise.model.PreferenceKeyStore
import java.util.*

private const val THREADS_KEY = "threads"
private const val DB_PREF_NAME = "app_db_pref"

@DatabaseEntity(
    persistableEntities = [
        SearchRecord::class,
        PhotoRecord::class,
        ChatMessageRecord::class,
        ChatUserRecord::class
    ]
)
abstract class AppDatabase(fastDatabase: FastDatabase) : PromiseDatabase(fastDatabase),
    SearchDatabase,
    PhotoDatabase,
    ChatDatabase {

    private fun chatMessageDao(): ChatMessageRecordDao = ChatMessageRecordDaoImpl(this)

    private fun messageThreadDao(): MessageThreadRecordDao = MessageThreadRecordDaoImpl(this)

    private fun chatUserDao(): ChatUserDao = ChatUserDaoImpl(this)

    var threadStore: PreferenceKeyStore<ChatThread>

    init {
        threadStore = object : PreferenceKeyStore<ChatThread>(
            DB_PREF_NAME,
            converter = converter
        ) {
            override fun findIndexFunction(t: ChatThread): FilterFunction<JSONObject> =
                filterFunction(
                    t
                )
        }
    }

    private val photoTable: FastTable<PhotoRecord> by lazy { tableOf(PhotoRecord::class.java) }

    private val searchTable: FastTable<SearchRecord> by lazy { tableOf(SearchRecord::class.java) }

    private fun getPhotoRecordByRef(refName: String, id: ID): PhotoRecord? =
        photoTable.findOne(
            PhotoRecordsTable.refNameColumn.with(refName),
            PhotoRecordsTable.refIdColumn.with(id.id)
        )

    private fun getPhotoRecordsByRef(refName: String, ids: Array<ID>): List<PhotoRecord> {
        val cursor = photoTable.query(
            photoTable.queryBuilder()
                .whereAnd(Criteria.equals(PhotoRecordsTable.refNameColumn, refName))
                .whereAnd(
                    Criteria.`in`(
                        PhotoRecordsTable.refIdColumn,
                        List(ids.map { it.id }).toArray()
                    )
                )
                .distinct()
        )
        cursor.moveToFirst()
        val records = List<PhotoRecord>()
        while (cursor.moveToNext()) {
            records.add(photoTable.deserialize(cursor).apply {
                uid = cursor.getInt(PhotoRecordsTable.idColumn.index)
            })
        }
        cursor.close()
        return records
    }

    override fun save(search: Search) {
        searchTable.save(SearchRecord.from(search))
    }

    override fun save(searched: kotlin.collections.List<Search>) {
        searchTable.save(IdentifiableList(searched.map {
            SearchRecord.from(it)
        }))
    }

    override fun getPhotoByRef(refName: String, id: ID): Photo? =
        getPhotoRecordByRef(refName, id)?.toPhoto()

    override fun savePhoto(photo: Photo, refName: String, id: ID): Photo {
        val photoRecord = PhotoRecord()
        photoRecord.refName = refName
        photoRecord.refId = id.id
        photoRecord.url = photo.url()
        photoRecord.type = photo.type()
        photoTable.save(photoRecord)
        return photo
    }

    override fun savePhotos(
        photos: kotlin.collections.List<Photo>,
        ids: Array<ID>,
        refName: String
    ): Boolean {
        if (photos.size != ids.size) throw IllegalArgumentException("photos length must be same as ids length")
        val photoRecords = IdentifiableList<PhotoRecord>()
        photos.forEachIndexed { index, photo ->
            val photoRecord = PhotoRecord()
            photoRecord.refName = refName
            photoRecord.refId = ids[index].id
            photoRecord.url = photo.url()
            photoRecord.type = photo.type()
            photoRecords.add(photoRecord)
        }
        return photoTable.save(photoRecords)
    }

    override fun getPhotosByRef(refName: String, ids: Array<ID>): List<Photo> =
        getPhotoRecordsByRef(refName, ids).map { it.toPhoto() }


    override fun getChatPhotos(chatID: ID): List<Photo> {
        TODO("Not yet implemented")
    }

    override fun getUserPhoto(userId: ID): Photo? {
        TODO("Not yet implemented")
    }

    override fun invalidateMessages(messages: kotlin.collections.List<ChatMessage>) {
        TODO("Not yet implemented")
    }

    override fun getMessageThreads(): kotlin.collections.List<ChatThread> {
        val threads = messageThreadDao().getMessageThreads()
        if (threads != null) {
            val messages = threads.map {
                val user = it.chatUser!!.toChatUser(this)
                val message = it.chatMessages!!.last()
                ChatThread(
                    ID.from(UUID.randomUUID().toString()),
                    user,
                    message.description,
                    message.toChatMessage(
                        chatUserDao(),
                        this
                    )
                )
            }
            threadStore.clear(
                THREADS_KEY
            )
            threadStore.save(THREADS_KEY, List(messages))
            return messages
        }
        return emptyList()
    }

    override fun getMessages(thread: ChatThread): kotlin.collections.List<ChatMessage> {
        TODO("Not yet implemented")
    }

    override fun getMessageThread(id: ID): ChatThread? {
        val threads = threadStore[THREADS_KEY].all()
        if (threads.isEmpty()) return null
        return threads.find {
            it!!.id == id
        }
    }

    override fun getPaginatedMessages(
        thread: ChatThread,
        skip: Int,
        take: Int
    ): kotlin.collections.List<ChatMessage> {
        TODO("Not yet implemented")
    }

    override fun getMessages(): kotlin.collections.List<ChatMessage> =
        chatMessageDao().getMessages()
            .filter { it.chatMessage != null }
            .map { message ->
                message.chatMessage.apply {
                    this!!.chatReplyMessage = message.replyChatMessage
                }
            }.map { chatMessageRecord ->
                chatMessageRecord!!.toChatMessage(
                    chatUserDao(),
                    this
                )
            }

    override fun search(search: Search): kotlin.collections.List<ChatThread> {
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
                    chatUserDao().getChatUser(it.key!!)!!.toChatUser(this),
                    "product",
                    message.toChatMessage(
                        chatUserDao(),
                        this
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
            FilterFunction { j ->
                t.id.id!! == j.getString("id") &&
                        t.user.userId.id == j.getString("usr")
            }

    }

}