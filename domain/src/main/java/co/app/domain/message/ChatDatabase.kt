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

package co.app.domain.message

import co.app.common.ID
import co.app.common.photo.Photo
import co.app.common.search.Search

/**
 *
 */
interface ChatDatabase {
    /**
     * gets photos sent in chat with [chatID]
     */
    fun getChatPhotos(chatID: ID): List<Photo>

    /**
     * gets a user photo of user with id [userId]
     */
    fun getUserPhoto(userId: ID): Photo?

    /**
     * clears messages in the database and saves the new list [messages]
     */
    fun invalidateMessages(messages: List<ChatMessage>)

    /**
     * gets the threads of messages currently available
     */
    fun getMessageThreads(): List<ChatThread>

    /**
     * gets all messages in a [thread]
     */
    fun getMessages(thread: ChatThread): List<ChatMessage>

    /**
     * gets all messages in the database
     */
    fun getMessages(): List<ChatMessage>

    /**
     * gets the thread with the given [id]
     */
    fun getMessageThread(id: ID): ChatThread?

    /**
     * used to get paginated messages for the ui
     * [thread] the group of the messages
     * [skip] and [take] are sizes to load at a time
     */
    fun getPaginatedMessages(thread: ChatThread, skip: Int, take: Int): List<ChatMessage>

    /**
     *  search messages with [search] query to load thread
     */
    fun search(search: Search): List<ChatThread>
}