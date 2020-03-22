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

import co.app.domain.message.ChatMessage
import org.apache.commons.cli.MissingArgumentException
import promise.commons.model.List
import promise.model.AbstractAsyncIDataStore
import promise.model.AbstractSyncIDataStore

const val SKIP_ARG = "skip_arg"
const val TAKE_ARG = "take_arg"
const val SEARCH_ARG = "search_arg"

class AsyncMessageRepo constructor(
    private val chatMessageRecordDao: ChatMessageRecordDao,
    private val chatUserDao: ChatUserDao
) : AbstractAsyncIDataStore<ChatMessage>() {
    @Throws(MissingArgumentException::class)
    override fun all(
        res: (List<out ChatMessage>, Any?) -> Unit,
        err: ((Exception) -> Unit)?,
        args: Map<String, Any?>?
    ) {
        when {
            args == null -> {
                res(List<ChatMessage>(chatMessageRecordDao.getDistinctMessages().value!!.map {
                    it.toChatMessage(chatUserDao)
                }), null)
            }
            args.containsKey(SEARCH_ARG) -> {
                res(List<ChatMessage>(chatMessageRecordDao.getDistinctMessages().value!!.map {
                    it.toChatMessage(chatUserDao)
                }), null)
                /*res(List<ChatMessage>(chatMessageRecordDao.searchMessages(args[SEARCH_ARG].toString())
                    .map {
                        it.toChatMessage(chatUserDao)
                    }), null
                )*/
            }
            args.containsKey(SKIP_ARG) && args.containsKey(TAKE_ARG) -> {

            }
        }
    }
}

class SyncMessageRepo constructor(private val chatMessageRecordDao: ChatMessageRecordDao) :
    AbstractSyncIDataStore<ChatMessage>()