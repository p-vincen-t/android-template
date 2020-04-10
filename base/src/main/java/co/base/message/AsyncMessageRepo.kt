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

import co.app.common.CacheController
import co.app.common.search.Search
import co.app.domain.message.ChatApi
import co.app.domain.message.ChatDatabase
import co.app.domain.message.ChatMessage
import co.app.domain.message.ChatThread
import org.apache.commons.cli.MissingArgumentException
import promise.commons.model.List
import promise.model.AbstractAsyncIDataStore
import java.util.concurrent.TimeUnit

const val SKIP_ARG = "skip_arg"
const val TAKE_ARG = "take_arg"
const val CHAT_THREAD = "chat_thread"

class AsyncMessageRepo constructor(
    private val chatDatabase: ChatDatabase,
    private val chatApi: ChatApi
) : AbstractAsyncIDataStore<ChatMessage>() {

    init {
        CacheController.initCache(CacheController.MESSAGES, TimeUnit.SECONDS)
    }

    @Throws(MissingArgumentException::class)
    override fun findAll(
        res: (List<out ChatMessage>?) -> Unit,
        err: ((Exception) -> Unit)?,
        args: Map<String, Any?>?
    ) {
        when {
            args == null -> {
                fun invalidateMessages() = chatApi.getMessages().fold({
                    chatDatabase.invalidateMessages(it!!)
                    res(List(it))
                }, {
                    err?.invoke(Exception(it))
                })
                if (CacheController.shouldInvalidate(CacheController.MESSAGES))
                    invalidateMessages()
                else {
                    val messages = chatDatabase.getMessages()
                    if (messages.isEmpty()) invalidateMessages()
                    else res(List(messages))
                }
            }


            args.containsKey(SKIP_ARG) && args.containsKey(TAKE_ARG) && args.containsKey(
                CHAT_THREAD
            ) -> {
                res(
                    List(
                        chatDatabase.getPaginatedMessages(
                            args[CHAT_THREAD] as ChatThread,
                            args[SKIP_ARG] as Int,
                            args[TAKE_ARG] as Int
                        )
                    )
                )
            }
        }
    }

}
