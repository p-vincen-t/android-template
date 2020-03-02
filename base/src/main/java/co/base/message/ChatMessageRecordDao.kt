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

import androidx.lifecycle.LiveData
import androidx.room.*
import co.app.common.toDistinct
import co.base.data.BaseDao

@Dao
abstract class ChatMessageRecordDao: BaseDao<ChatMessageRecord> {

    @Query("SELECT * FROM chats")
    protected abstract fun getMessages(): LiveData<List<ChatMessageRecord>>

    fun getDistinctMessages(): LiveData<List<ChatMessageRecord>> = getMessages().toDistinct()

    @Query("SELECT * FROM chats WHERE id >= :skip LIMIT :take")
    protected abstract fun getPaginatedMessages(skip: Int, take: Int): LiveData<List<ChatMessageRecord>>

    //@Query("SELECT * FROM chats")
    //@Query("SELECT * FROM chats AS c WHERE c.senderId IN (SELECT userId FROM chat_users WHERE chat_users.userName LIKE  :query ) OR c.message LIKE :query")
    //abstract fun searchMessages(query: String): List<ChatMessageRecord>

}